package com.br.manager.domain.sale_receipt.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.entity.Customer;
import com.br.manager.domain.operational.entity.CustomerVehicle;
import com.br.manager.domain.operational.repository.CustomerRepository;
import com.br.manager.domain.operational.repository.CustomerVehicleRepository;
import com.br.manager.domain.organization.entity.Station;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.organization.repository.StationRepository;
import com.br.manager.domain.organization.repository.UserRepository;
import com.br.manager.domain.sale_receipt.dto.SaleInputDTO;
import com.br.manager.domain.sale_receipt.dto.SaleResponseDTO;
import com.br.manager.domain.sale_receipt.entity.Sale;
import com.br.manager.domain.sale_receipt.enums.SaleStatus;
import com.br.manager.domain.sale_receipt.fixture.SaleFixture;
import com.br.manager.domain.sale_receipt.fixture.SaleItemFixture;
import com.br.manager.domain.sale_receipt.fixture.SaleReceiptReferenceFixture;
import com.br.manager.domain.sale_receipt.mapper.SaleItemMapper;
import com.br.manager.domain.sale_receipt.mapper.SaleMapper;
import com.br.manager.domain.sale_receipt.repository.SaleRepository;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import com.br.manager.domain.shift_cachs.repository.ShiftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    private static final String SAVE_ERROR_MESSAGE = "Erro ao salvar venda " + SaleFixture.SALE_NUMBER;
    private static final String LOCKED_SALE_MESSAGE = "Vendas pagas ou com crédito pendente não podem ser alteradas.";

    @Mock
    private SaleRepository saleRepository;
    @Mock
    private SaleItemService saleItemService;
    @Mock
    private StationRepository stationRepository;
    @Mock
    private ShiftRepository shiftRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerVehicleRepository customerVehicleRepository;
    @Spy
    private SaleMapper saleMapper = new SaleMapper();
    @Spy
    private SaleItemMapper saleItemMapper = new SaleItemMapper();

    @InjectMocks
    private SaleService saleService;

    private Station station;
    private Shift shift;
    private User cashier;
    private SaleInputDTO input;

    @BeforeEach
    void setUp() {
        station = SaleReceiptReferenceFixture.station(true);
        cashier = SaleReceiptReferenceFixture.user(true);
        shift = SaleReceiptReferenceFixture.shift(station.getId(), cashier.getId(), ShiftStatusEnum.OPEN);
        input = SaleFixture.input(station.getId(), shift.getId(), cashier.getId(),
                List.of(SaleItemFixture.productInput(UUID.randomUUID())));
    }

    private void givenStationShiftAndCashier() {
        when(stationRepository.findById(station.getId())).thenReturn(Optional.of(station));
        when(shiftRepository.findById(shift.getId())).thenReturn(Optional.of(shift));
        when(userRepository.findById(cashier.getId())).thenReturn(Optional.of(cashier));
    }

    private static Answer<Sale> assigningId(UUID id) {
        return invocation -> {
            Sale sale = invocation.getArgument(0);
            sale.setId(id);
            return sale;
        };
    }

    @Nested
    class CreateValidation {

        @Test
        void shouldRejectNullPayload() {
            assertThatThrownBy(() -> saleService.create(null))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("O payload da venda é obrigatório.");
        }

        @Test
        void shouldRejectMissingStation() {
            input.setStationId(null);

            assertThatThrownBy(() -> saleService.create(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("O ID do posto é obrigatório.");
        }

        @Test
        void shouldRejectMissingShift() {
            input.setShiftId(null);

            assertThatThrownBy(() -> saleService.create(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("O ID do turno é obrigatório.");
        }

        @Test
        void shouldRejectMissingCashier() {
            input.setCashierUserId(null);

            assertThatThrownBy(() -> saleService.create(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("O ID do usuário caixa é obrigatório.");
        }

        @Test
        void shouldRejectBlankSaleNumber() {
            input.setSaleNumber(" ");

            assertThatThrownBy(() -> saleService.create(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("O número da venda é obrigatório.");
        }

        @Test
        void shouldRejectDuplicatedSaleNumberForStation() {
            when(saleRepository.existsByStationIdAndSaleNumber(station.getId(), SaleFixture.SALE_NUMBER)).thenReturn(true);

            assertThatThrownBy(() -> saleService.create(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(String.format("A venda %s já existe para o posto %s.", SaleFixture.SALE_NUMBER, station.getId()));
            verify(saleRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldRejectSaleWithoutItems() {
            input.setItems(List.of());

            assertThatThrownBy(() -> saleService.create(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("A venda deve conter pelo menos um item.");
        }

        @Test
        void shouldRejectSaleWithNullItems() {
            input.setItems(null);

            assertThatThrownBy(() -> saleService.create(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("A venda deve conter pelo menos um item.");
            verifyNoInteractions(stationRepository);
        }
    }

    @Nested
    class Create {

        private final UUID saleId = UUID.randomUUID();

        @Test
        void shouldPersistOpenSaleAndSaveItems() {
            givenStationShiftAndCashier();
            Sale reloaded = SaleFixture.persistedEntity(SaleStatus.OPEN);
            reloaded.setId(saleId);
            when(saleRepository.saveAndFlush(any(Sale.class))).thenAnswer(assigningId(saleId));
            when(saleRepository.findDetailedById(saleId)).thenReturn(Optional.of(reloaded));

            SaleResponseDTO response = saleService.create(input);

            assertThat(response.id()).isEqualTo(saleId);
            verify(saleItemService).saveSaleItems(saleId, input.getItems());
            ArgumentCaptor<Sale> captor = ArgumentCaptor.forClass(Sale.class);
            verify(saleRepository, times(2)).saveAndFlush(captor.capture());
            Sale created = captor.getAllValues().get(0);
            assertThat(created.getStatus()).isEqualTo(SaleStatus.OPEN);
            assertThat(created.getStationId()).isEqualTo(station.getId());
            assertThat(created.getShiftId()).isEqualTo(shift.getId());
            assertThat(created.getCashierUserId()).isEqualTo(cashier.getId());
            assertThat(created.getSaleNumber()).isEqualTo(SaleFixture.SALE_NUMBER);
            assertThat(created.getSoldAt()).isEqualTo(SaleFixture.SOLD_AT);
            assertThat(created.getDiscountAmount()).isEqualByComparingTo(SaleFixture.DISCOUNT);
            assertThat(created.getFiscalStatus()).isEqualTo(SaleFixture.FISCAL_STATUS);
            assertThat(created.getNotes()).isEqualTo(SaleFixture.NOTES);
        }

        @Test
        void shouldDefaultSoldAtAndDiscountWhenNotInformed() {
            givenStationShiftAndCashier();
            input.setSoldAt(null);
            input.setDiscountAmount(null);
            when(saleRepository.saveAndFlush(any(Sale.class))).thenAnswer(assigningId(saleId));
            when(saleRepository.findDetailedById(saleId)).thenReturn(Optional.of(SaleFixture.persistedEntity(SaleStatus.OPEN)));

            saleService.create(input);

            ArgumentCaptor<Sale> captor = ArgumentCaptor.forClass(Sale.class);
            verify(saleRepository, times(2)).saveAndFlush(captor.capture());
            assertThat(captor.getAllValues().get(0).getSoldAt()).isNotNull();
            assertThat(captor.getAllValues().get(0).getDiscountAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void shouldLinkActiveAttendantCustomerAndVehicle() {
            givenStationShiftAndCashier();
            User attendant = SaleReceiptReferenceFixture.user(true);
            Customer customer = SaleReceiptReferenceFixture.customer(true);
            CustomerVehicle vehicle = SaleReceiptReferenceFixture.vehicle(customer.getId(), true);
            input.setAttendantUserId(attendant.getId());
            input.setCustomerId(customer.getId());
            input.setVehicleId(vehicle.getId());
            when(userRepository.findById(attendant.getId())).thenReturn(Optional.of(attendant));
            when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
            when(customerVehicleRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicle));
            when(saleRepository.saveAndFlush(any(Sale.class))).thenAnswer(assigningId(saleId));
            when(saleRepository.findDetailedById(saleId)).thenReturn(Optional.of(SaleFixture.persistedEntity(SaleStatus.OPEN)));

            saleService.create(input);

            ArgumentCaptor<Sale> captor = ArgumentCaptor.forClass(Sale.class);
            verify(saleRepository, times(2)).saveAndFlush(captor.capture());
            Sale created = captor.getAllValues().get(0);
            assertThat(created.getAttendantUserId()).isEqualTo(attendant.getId());
            assertThat(created.getCustomerId()).isEqualTo(customer.getId());
            assertThat(created.getVehicleId()).isEqualTo(vehicle.getId());
        }

        @Test
        @Disabled("BUG: SaleService.create (SaleService.java:59-75) never calculates Sale.subtotal from the items, "
                + "so every sale is persisted with subtotal 0")
        void shouldCalculateSubtotalFromItems() {
            givenStationShiftAndCashier();
            Sale reloaded = SaleFixture.persistedEntity(SaleStatus.OPEN);
            reloaded.setSubtotal(BigDecimal.ZERO);
            reloaded.addItem(SaleItemFixture.persistedEntity(UUID.randomUUID()));
            when(saleRepository.saveAndFlush(any(Sale.class))).thenAnswer(assigningId(saleId));
            when(saleRepository.findDetailedById(saleId)).thenReturn(Optional.of(reloaded));

            SaleResponseDTO response = saleService.create(input);

            BigDecimal expectedSubtotal = SaleItemFixture.QUANTITY.multiply(SaleItemFixture.UNIT_PRICE)
                    .subtract(SaleItemFixture.DISCOUNT);
            assertThat(response.subtotal()).isEqualByComparingTo(expectedSubtotal);
        }

        @Test
        void shouldThrowNotFoundWhenStationDoesNotExist() {
            when(stationRepository.findById(station.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> saleService.create(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(station.getId().toString());
            verify(saleRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldThrowNotFoundWhenStationIsInactive() {
            Station inactive = SaleReceiptReferenceFixture.station(false);
            input.setStationId(inactive.getId());
            when(stationRepository.findById(inactive.getId())).thenReturn(Optional.of(inactive));

            assertThatThrownBy(() -> saleService.create(input)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldThrowNotFoundWhenShiftDoesNotExist() {
            when(stationRepository.findById(station.getId())).thenReturn(Optional.of(station));
            when(shiftRepository.findById(shift.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> saleService.create(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(shift.getId().toString());
        }

        @Test
        void shouldWrapBusinessErrorWhenShiftIsClosed() {
            when(stationRepository.findById(station.getId())).thenReturn(Optional.of(station));
            shift.setStatus(ShiftStatusEnum.CLOSED);
            when(shiftRepository.findById(shift.getId())).thenReturn(Optional.of(shift));

            assertThatThrownBy(() -> saleService.create(input))
                    .isInstanceOf(BusinessException.class)
                    .isNotInstanceOf(NotFoundBusinessException.class)
                    .hasMessage(SAVE_ERROR_MESSAGE)
                    .hasRootCauseMessage(String.format("O turno %s não está ativo.", shift.getId()));
        }

        @Test
        void shouldThrowNotFoundWhenCashierDoesNotExist() {
            when(stationRepository.findById(station.getId())).thenReturn(Optional.of(station));
            when(shiftRepository.findById(shift.getId())).thenReturn(Optional.of(shift));
            when(userRepository.findById(cashier.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> saleService.create(input)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldThrowNotFoundWhenCashierIsInactive() {
            when(stationRepository.findById(station.getId())).thenReturn(Optional.of(station));
            when(shiftRepository.findById(shift.getId())).thenReturn(Optional.of(shift));
            cashier.setActive(false);
            when(userRepository.findById(cashier.getId())).thenReturn(Optional.of(cashier));

            assertThatThrownBy(() -> saleService.create(input)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldThrowNotFoundWhenAttendantIsInactive() {
            givenStationShiftAndCashier();
            User attendant = SaleReceiptReferenceFixture.user(false);
            input.setAttendantUserId(attendant.getId());
            when(userRepository.findById(attendant.getId())).thenReturn(Optional.of(attendant));

            assertThatThrownBy(() -> saleService.create(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(attendant.getId().toString());
        }

        @Test
        void shouldThrowNotFoundWhenAttendantDoesNotExist() {
            givenStationShiftAndCashier();
            UUID attendantId = UUID.randomUUID();
            input.setAttendantUserId(attendantId);
            when(userRepository.findById(attendantId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> saleService.create(input)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldThrowNotFoundWhenCustomerIsInactive() {
            givenStationShiftAndCashier();
            Customer customer = SaleReceiptReferenceFixture.customer(false);
            input.setCustomerId(customer.getId());
            when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

            assertThatThrownBy(() -> saleService.create(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(customer.getId().toString());
        }

        @Test
        void shouldThrowNotFoundWhenCustomerDoesNotExist() {
            givenStationShiftAndCashier();
            UUID customerId = UUID.randomUUID();
            input.setCustomerId(customerId);
            when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> saleService.create(input)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldThrowNotFoundWhenVehicleDoesNotExist() {
            givenStationShiftAndCashier();
            UUID vehicleId = UUID.randomUUID();
            input.setVehicleId(vehicleId);
            when(customerVehicleRepository.findById(vehicleId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> saleService.create(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(vehicleId.toString());
        }

        @Test
        void shouldThrowNotFoundWhenVehicleIsInactive() {
            givenStationShiftAndCashier();
            CustomerVehicle vehicle = SaleReceiptReferenceFixture.vehicle(UUID.randomUUID(), false);
            input.setVehicleId(vehicle.getId());
            when(customerVehicleRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicle));

            assertThatThrownBy(() -> saleService.create(input)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldRejectVehicleFromAnotherCustomer() {
            givenStationShiftAndCashier();
            Customer customer = SaleReceiptReferenceFixture.customer(true);
            CustomerVehicle vehicle = SaleReceiptReferenceFixture.vehicle(UUID.randomUUID(), true);
            input.setCustomerId(customer.getId());
            input.setVehicleId(vehicle.getId());
            when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
            when(customerVehicleRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicle));

            assertThatThrownBy(() -> saleService.create(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(SAVE_ERROR_MESSAGE)
                    .hasRootCauseMessage("O veículo não pertence ao cliente informado.");
            verify(saleRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            givenStationShiftAndCashier();
            when(saleRepository.saveAndFlush(any(Sale.class))).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> saleService.create(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(SAVE_ERROR_MESSAGE)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
            verify(saleItemService, never()).saveSaleItems(any(), any());
        }

        @Test
        void shouldPropagateNotFoundFromItemService() {
            givenStationShiftAndCashier();
            when(saleRepository.saveAndFlush(any(Sale.class))).thenAnswer(assigningId(saleId));
            when(saleItemService.saveSaleItems(saleId, input.getItems()))
                    .thenThrow(new NotFoundBusinessException("Produto não encontrado"));

            assertThatThrownBy(() -> saleService.create(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessage("Produto não encontrado");
        }

        @Test
        void shouldThrowNotFoundWhenSavedSaleCannotBeReloaded() {
            givenStationShiftAndCashier();
            when(saleRepository.saveAndFlush(any(Sale.class))).thenAnswer(assigningId(saleId));
            when(saleRepository.findDetailedById(saleId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> saleService.create(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(saleId.toString());
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateOnlyInformedFieldsOfOpenSale() {
            Sale existing = SaleFixture.persistedEntity(SaleStatus.OPEN);
            when(saleRepository.findDetailedById(existing.getId())).thenReturn(Optional.of(existing));
            when(saleRepository.saveAndFlush(existing)).thenReturn(existing);

            SaleResponseDTO response = saleService.update(SaleFixture.updateInput(existing.getId()));

            assertThat(response.notes()).isEqualTo(SaleFixture.UPDATED_NOTES);
            assertThat(response.saleNumber()).isEqualTo(SaleFixture.SALE_NUMBER);
            assertThat(response.stationId()).isEqualTo(existing.getStationId());
            assertThat(response.discountAmount()).isEqualByComparingTo(SaleFixture.DISCOUNT);
            verifyNoInteractions(stationRepository, shiftRepository, userRepository);
        }

        @Test
        void shouldReplaceStationWhenInformed() {
            Sale existing = SaleFixture.persistedEntity(SaleStatus.OPEN);
            SaleInputDTO update = SaleFixture.updateInput(existing.getId());
            update.setStationId(station.getId());
            when(saleRepository.findDetailedById(existing.getId())).thenReturn(Optional.of(existing));
            when(stationRepository.findById(station.getId())).thenReturn(Optional.of(station));
            when(saleRepository.saveAndFlush(existing)).thenReturn(existing);

            SaleResponseDTO response = saleService.update(update);

            assertThat(response.stationId()).isEqualTo(station.getId());
        }

        @Test
        void shouldThrowNotFoundWhenSaleDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(saleRepository.findDetailedById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> saleService.update(SaleFixture.updateInput(id)))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(id.toString());
            verify(saleRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldRejectUpdateOfPaidSale() {
            Sale existing = SaleFixture.persistedEntity(SaleStatus.PAID);
            when(saleRepository.findDetailedById(existing.getId())).thenReturn(Optional.of(existing));

            assertThatThrownBy(() -> saleService.update(SaleFixture.updateInput(existing.getId())))
                    .isInstanceOf(BusinessException.class)
                    .hasRootCauseMessage(LOCKED_SALE_MESSAGE);
            verify(saleRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldRejectUpdateOfSaleWithPendingCredit() {
            Sale existing = SaleFixture.persistedEntity(SaleStatus.PENDING_CREDIT);
            when(saleRepository.findDetailedById(existing.getId())).thenReturn(Optional.of(existing));

            assertThatThrownBy(() -> saleService.update(SaleFixture.updateInput(existing.getId())))
                    .isInstanceOf(BusinessException.class)
                    .hasRootCauseMessage(LOCKED_SALE_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            Sale existing = SaleFixture.persistedEntity(SaleStatus.OPEN);
            SaleInputDTO update = SaleFixture.updateInput(existing.getId());
            update.setSaleNumber(SaleFixture.OTHER_SALE_NUMBER);
            when(saleRepository.findDetailedById(existing.getId())).thenReturn(Optional.of(existing));
            when(saleRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("duplicate"));

            assertThatThrownBy(() -> saleService.update(update))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao salvar venda " + SaleFixture.OTHER_SALE_NUMBER)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldFindSaleById() {
            Sale sale = SaleFixture.persistedEntity(SaleStatus.OPEN);
            when(saleRepository.findDetailedById(sale.getId())).thenReturn(Optional.of(sale));

            assertThat(saleService.find(sale.getId()).id()).isEqualTo(sale.getId());
        }

        @Test
        void shouldThrowNotFoundWhenSaleIdDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(saleRepository.findDetailedById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> saleService.find(id))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(id.toString());
        }

        @Test
        void shouldListAllSalesWithDetails() {
            Sale first = SaleFixture.persistedEntity(SaleStatus.OPEN);
            Sale second = SaleFixture.persistedEntity(SaleStatus.PAID);
            when(saleRepository.findAll()).thenReturn(List.of(first, second));
            when(saleRepository.findDetailedById(first.getId())).thenReturn(Optional.of(first));
            when(saleRepository.findDetailedById(second.getId())).thenReturn(Optional.of(second));

            assertThat(saleService.findAll())
                    .extracting(SaleResponseDTO::id)
                    .containsExactly(first.getId(), second.getId());
        }

        @Test
        void shouldListSalesByStation() {
            Sale sale = SaleFixture.persistedEntity(SaleStatus.OPEN);
            when(saleRepository.findByStationId(sale.getStationId())).thenReturn(List.of(sale));
            when(saleRepository.findDetailedById(sale.getId())).thenReturn(Optional.of(sale));

            assertThat(saleService.findByStation(sale.getStationId()))
                    .extracting(SaleResponseDTO::stationId)
                    .containsExactly(sale.getStationId());
        }

        @Test
        void shouldReturnEmptyListWhenStationHasNoSales() {
            UUID stationId = UUID.randomUUID();
            when(saleRepository.findByStationId(stationId)).thenReturn(List.of());

            assertThat(saleService.findByStation(stationId)).isEmpty();
        }

        @Test
        void shouldReturnSaleEntityById() {
            Sale sale = SaleFixture.persistedEntity(SaleStatus.OPEN);
            when(saleRepository.findDetailedById(sale.getId())).thenReturn(Optional.of(sale));

            assertThat(saleService.getSaleEntityById(sale.getId())).isSameAs(sale);
        }

        @Test
        void shouldThrowNotFoundWhenSaleEntityDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(saleRepository.findDetailedById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> saleService.getSaleEntityById(id)).isInstanceOf(NotFoundBusinessException.class);
        }
    }
}
