package com.br.manager.domain.sale_receipt.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.entity.Nozzle;
import com.br.manager.domain.operational.entity.Product;
import com.br.manager.domain.operational.entity.Tank;
import com.br.manager.domain.operational.repository.NozzleRepository;
import com.br.manager.domain.operational.repository.ProductRepository;
import com.br.manager.domain.operational.repository.TankRepository;
import com.br.manager.domain.sale_receipt.dto.SaleItemInputDTO;
import com.br.manager.domain.sale_receipt.dto.SaleItemResponseDTO;
import com.br.manager.domain.sale_receipt.entity.Sale;
import com.br.manager.domain.sale_receipt.entity.SaleItem;
import com.br.manager.domain.sale_receipt.enums.SaleStatus;
import com.br.manager.domain.sale_receipt.fixture.SaleFixture;
import com.br.manager.domain.sale_receipt.fixture.SaleItemFixture;
import com.br.manager.domain.sale_receipt.fixture.SaleReceiptReferenceFixture;
import com.br.manager.domain.sale_receipt.mapper.SaleItemMapper;
import com.br.manager.domain.sale_receipt.repository.SaleItemRepository;
import com.br.manager.domain.sale_receipt.repository.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaleItemServiceTest {

    private static final String LOCKED_SALE_MESSAGE = "Vendas pagas ou com crédito pendente não podem ser alteradas.";

    @Mock
    private SaleItemRepository saleItemRepository;
    @Mock
    private SaleRepository saleRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private NozzleRepository nozzleRepository;
    @Mock
    private TankRepository tankRepository;
    @Spy
    private SaleItemMapper saleItemMapper = new SaleItemMapper();

    @InjectMocks
    private SaleItemService saleItemService;

    private Sale sale;
    private Product product;

    @BeforeEach
    void setUp() {
        sale = SaleFixture.persistedEntity(SaleStatus.OPEN);
        product = SaleReceiptReferenceFixture.product(true);
    }

    private void givenOpenSaleAndActiveProduct() {
        when(saleRepository.findById(sale.getId())).thenReturn(Optional.of(sale));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
    }

    @SuppressWarnings("unchecked")
    private List<SaleItem> capturedSavedItems() {
        ArgumentCaptor<List<SaleItem>> captor = ArgumentCaptor.forClass(List.class);
        verify(saleItemRepository).saveAllAndFlush(captor.capture());
        return captor.getValue();
    }

    @Nested
    class SaveSaleItems {

        @Test
        void shouldPersistProductItemLinkedToSale() {
            givenOpenSaleAndActiveProduct();
            when(saleItemRepository.saveAllAndFlush(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

            List<SaleItemResponseDTO> response = saleItemService.saveSaleItems(sale.getId(),
                    List.of(SaleItemFixture.productInput(product.getId())));

            assertThat(response).singleElement()
                    .satisfies(item -> assertThat(item.quantity()).isEqualByComparingTo(SaleItemFixture.QUANTITY))
                    .satisfies(item -> assertThat(item.unitPrice()).isEqualByComparingTo(SaleItemFixture.UNIT_PRICE))
                    .satisfies(item -> assertThat(item.productId()).isEqualTo(product.getId()));
            assertThat(capturedSavedItems()).singleElement()
                    .satisfies(item -> assertThat(item.getSale()).isSameAs(sale));
        }

        @Test
        void shouldDefaultDiscountToZeroWhenNotInformed() {
            givenOpenSaleAndActiveProduct();
            SaleItemInputDTO input = SaleItemFixture.productInput(product.getId());
            input.setDiscountAmount(null);

            saleItemService.saveSaleItems(sale.getId(), List.of(input));

            assertThat(capturedSavedItems().get(0).getDiscountAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void shouldCalculateFuelQuantityFromMeterReadingsAndUseNozzleTank() {
            givenOpenSaleAndActiveProduct();
            Tank tank = SaleReceiptReferenceFixture.tank(product.getId());
            Nozzle nozzle = SaleReceiptReferenceFixture.nozzle(UUID.randomUUID(), tank.getId(), product.getId());
            when(nozzleRepository.findById(nozzle.getId())).thenReturn(Optional.of(nozzle));
            when(tankRepository.findById(tank.getId())).thenReturn(Optional.of(tank));

            saleItemService.saveSaleItems(sale.getId(), List.of(SaleItemFixture.fuelInput(product.getId(), nozzle.getId())));

            SaleItem saved = capturedSavedItems().get(0);
            assertThat(saved.getQuantity()).isEqualByComparingTo(SaleItemFixture.FUEL_QUANTITY);
            assertThat(saved.getTankId()).isEqualTo(tank.getId());
            assertThat(saved.getNozzleId()).isEqualTo(nozzle.getId());
        }

        @Test
        void shouldThrowNotFoundWhenSaleDoesNotExist() {
            UUID saleId = UUID.randomUUID();
            when(saleRepository.findById(saleId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> saleItemService.saveSaleItems(saleId, List.of(SaleItemFixture.productInput(product.getId()))))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(saleId.toString());
        }

        @Test
        void shouldRejectItemsForPaidSale() {
            sale.setStatus(SaleStatus.PAID);
            when(saleRepository.findById(sale.getId())).thenReturn(Optional.of(sale));

            assertThatThrownBy(() -> saleItemService.saveSaleItems(sale.getId(), List.of(SaleItemFixture.productInput(product.getId()))))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(LOCKED_SALE_MESSAGE);
            verifyNoInteractions(productRepository, saleItemRepository);
        }

        @Test
        void shouldRejectItemsForSaleWithPendingCredit() {
            sale.setStatus(SaleStatus.PENDING_CREDIT);
            when(saleRepository.findById(sale.getId())).thenReturn(Optional.of(sale));

            assertThatThrownBy(() -> saleItemService.saveSaleItems(sale.getId(), List.of(SaleItemFixture.productInput(product.getId()))))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(LOCKED_SALE_MESSAGE);
        }

        @Test
        void shouldRejectDuplicatedLineNumbers() {
            when(saleRepository.findById(sale.getId())).thenReturn(Optional.of(sale));
            List<SaleItemInputDTO> inputs = List.of(
                    SaleItemFixture.productInput(product.getId()),
                    SaleItemFixture.productInput(product.getId()));

            assertThatThrownBy(() -> saleItemService.saveSaleItems(sale.getId(), inputs))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Os números de linha dos itens da venda devem ser únicos.");
            verifyNoInteractions(saleItemRepository);
        }

        @Test
        void shouldPersistItemsWithDistinctLineNumbers() {
            givenOpenSaleAndActiveProduct();
            List<SaleItemInputDTO> inputs = List.of(
                    SaleItemFixture.productInput(product.getId(), SaleItemFixture.LINE_NUMBER),
                    SaleItemFixture.productInput(product.getId(), SaleItemFixture.SECOND_LINE_NUMBER));

            saleItemService.saveSaleItems(sale.getId(), inputs);

            assertThat(capturedSavedItems())
                    .extracting(SaleItem::getLineNumber)
                    .containsExactly(SaleItemFixture.LINE_NUMBER, SaleItemFixture.SECOND_LINE_NUMBER);
        }

        @Test
        void shouldThrowNotFoundWhenProductDoesNotExist() {
            when(saleRepository.findById(sale.getId())).thenReturn(Optional.of(sale));
            when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> saleItemService.saveSaleItems(sale.getId(), List.of(SaleItemFixture.productInput(product.getId()))))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(product.getId().toString());
        }

        @Test
        void shouldThrowNotFoundWhenProductIsInactive() {
            product.setActive(false);
            givenOpenSaleAndActiveProduct();

            assertThatThrownBy(() -> saleItemService.saveSaleItems(sale.getId(), List.of(SaleItemFixture.productInput(product.getId()))))
                    .isInstanceOf(NotFoundBusinessException.class);
            verify(saleItemRepository, never()).saveAllAndFlush(any());
        }

        @Test
        void shouldThrowNotFoundWhenNozzleDoesNotExist() {
            givenOpenSaleAndActiveProduct();
            UUID nozzleId = UUID.randomUUID();
            when(nozzleRepository.findById(nozzleId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> saleItemService.saveSaleItems(sale.getId(), List.of(SaleItemFixture.fuelInput(product.getId(), nozzleId))))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(nozzleId.toString());
        }

        @Test
        void shouldThrowNotFoundWhenNozzleTankDoesNotExist() {
            givenOpenSaleAndActiveProduct();
            Nozzle nozzle = SaleReceiptReferenceFixture.nozzle(UUID.randomUUID(), UUID.randomUUID(), product.getId());
            when(nozzleRepository.findById(nozzle.getId())).thenReturn(Optional.of(nozzle));
            when(tankRepository.findById(nozzle.getTankId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> saleItemService.saveSaleItems(sale.getId(), List.of(SaleItemFixture.fuelInput(product.getId(), nozzle.getId()))))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(nozzle.getTankId().toString());
        }

        @Test
        void shouldRejectFuelItemWithoutMeterReadings() {
            Nozzle nozzle = givenFuelNozzle();
            SaleItemInputDTO input = SaleItemFixture.fuelInput(product.getId(), nozzle.getId());
            input.setMeterEnd(null);

            assertThatThrownBy(() -> saleItemService.saveSaleItems(sale.getId(), List.of(input)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Itens de combustível exigem leitura inicial e final do medidor.");
        }

        @Test
        void shouldRejectFuelItemWhenMeterEndIsNotGreaterThanStart() {
            Nozzle nozzle = givenFuelNozzle();
            SaleItemInputDTO input = SaleItemFixture.fuelInput(product.getId(), nozzle.getId());
            input.setMeterEnd(SaleItemFixture.METER_START);

            assertThatThrownBy(() -> saleItemService.saveSaleItems(sale.getId(), List.of(input)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("A leitura final deve ser maior que a leitura inicial.");
        }

        @Test
        void shouldRejectProductItemWithoutPositiveQuantity() {
            givenOpenSaleAndActiveProduct();
            SaleItemInputDTO input = SaleItemFixture.productInput(product.getId());
            input.setQuantity(BigDecimal.ZERO);

            assertThatThrownBy(() -> saleItemService.saveSaleItems(sale.getId(), List.of(input)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("A quantidade do item deve ser maior que zero.");
        }

        @Test
        void shouldRejectProductItemWithNullQuantity() {
            givenOpenSaleAndActiveProduct();
            SaleItemInputDTO input = SaleItemFixture.productInput(product.getId());
            input.setQuantity(null);

            assertThatThrownBy(() -> saleItemService.saveSaleItems(sale.getId(), List.of(input)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("A quantidade do item deve ser maior que zero.");
        }

        @Test
        void shouldRejectItemWithoutPositiveUnitPrice() {
            givenOpenSaleAndActiveProduct();
            SaleItemInputDTO input = SaleItemFixture.productInput(product.getId());
            input.setUnitPrice(BigDecimal.ZERO);

            assertThatThrownBy(() -> saleItemService.saveSaleItems(sale.getId(), List.of(input)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("O preço unitário deve ser maior que zero.");
            verify(saleItemRepository, never()).saveAllAndFlush(any());
        }

        @Test
        void shouldRejectItemWithNullUnitPrice() {
            givenOpenSaleAndActiveProduct();
            SaleItemInputDTO input = SaleItemFixture.productInput(product.getId());
            input.setUnitPrice(null);

            assertThatThrownBy(() -> saleItemService.saveSaleItems(sale.getId(), List.of(input)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("O preço unitário deve ser maior que zero.");
        }

        private Nozzle givenFuelNozzle() {
            givenOpenSaleAndActiveProduct();
            Tank tank = SaleReceiptReferenceFixture.tank(product.getId());
            Nozzle nozzle = SaleReceiptReferenceFixture.nozzle(UUID.randomUUID(), tank.getId(), product.getId());
            when(nozzleRepository.findById(nozzle.getId())).thenReturn(Optional.of(nozzle));
            when(tankRepository.findById(tank.getId())).thenReturn(Optional.of(tank));
            return nozzle;
        }
    }

    @Nested
    class FindBySale {

        @Test
        void shouldReturnItemsOrderedByLineNumber() {
            SaleItem item = SaleItemFixture.persistedEntity(product.getId());
            when(saleItemRepository.findBySaleIdOrderByLineNumber(sale.getId())).thenReturn(List.of(item));

            assertThat(saleItemService.findBySale(sale.getId()))
                    .extracting(SaleItemResponseDTO::id)
                    .containsExactly(item.getId());
        }

        @Test
        void shouldReturnEmptyListWhenSaleHasNoItems() {
            when(saleItemRepository.findBySaleIdOrderByLineNumber(sale.getId())).thenReturn(List.of());

            assertThat(saleItemService.findBySale(sale.getId())).isEmpty();
        }
    }
}
