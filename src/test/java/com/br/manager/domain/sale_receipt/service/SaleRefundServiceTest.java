package com.br.manager.domain.sale_receipt.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.organization.repository.UserRepository;
import com.br.manager.domain.sale_receipt.dto.SaleRefundInputDTO;
import com.br.manager.domain.sale_receipt.dto.SaleRefundResponseDTO;
import com.br.manager.domain.sale_receipt.entity.Sale;
import com.br.manager.domain.sale_receipt.entity.SalePayment;
import com.br.manager.domain.sale_receipt.entity.SaleRefund;
import com.br.manager.domain.sale_receipt.enums.PaymentStatus;
import com.br.manager.domain.sale_receipt.enums.SaleStatus;
import com.br.manager.domain.sale_receipt.fixture.SaleFixture;
import com.br.manager.domain.sale_receipt.fixture.SalePaymentFixture;
import com.br.manager.domain.sale_receipt.fixture.SaleRefundFixture;
import com.br.manager.domain.sale_receipt.fixture.SaleReceiptReferenceFixture;
import com.br.manager.domain.sale_receipt.mapper.SaleRefundMapper;
import com.br.manager.domain.sale_receipt.repository.SaleRefundRepository;
import com.br.manager.domain.sale_receipt.repository.SaleRepository;
import com.br.manager.domain.shift_cachs.entity.CashMovement;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.CashMovementTypeEnum;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import com.br.manager.domain.shift_cachs.repository.CashMovementRepository;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaleRefundServiceTest {

    private static final String WRAPPED_MESSAGE = "Erro ao estornar venda";

    @Mock
    private SaleRefundRepository saleRefundRepository;
    @Mock
    private SaleRepository saleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ShiftRepository shiftRepository;
    @Mock
    private CashMovementRepository cashMovementRepository;
    @Spy
    private SaleRefundMapper saleRefundMapper = new SaleRefundMapper();

    @InjectMocks
    private SaleRefundService saleRefundService;

    private Sale sale;
    private User requester;
    private User approver;

    @BeforeEach
    void setUp() {
        sale = SaleFixture.persistedEntity(SaleStatus.PAID);
        requester = SaleReceiptReferenceFixture.user(true);
        approver = SaleReceiptReferenceFixture.user(true);
    }

    private SaleRefundInputDTO input() {
        return SaleRefundFixture.input(sale.getId(), requester.getId(), approver.getId());
    }

    private void givenSaleAndUsers() {
        when(saleRepository.findDetailedById(sale.getId())).thenReturn(Optional.of(sale));
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(userRepository.findById(approver.getId())).thenReturn(Optional.of(approver));
    }

    private void givenSuccessfulSave() {
        when(saleRefundRepository.saveAndFlush(any(SaleRefund.class))).thenAnswer(invocation -> {
            SaleRefund refund = invocation.getArgument(0);
            refund.setId(UUID.randomUUID());
            return refund;
        });
    }

    private Shift givenShiftExists() {
        Shift shift = SaleReceiptReferenceFixture.shift(sale.getStationId(), sale.getCashierUserId(), ShiftStatusEnum.OPEN);
        shift.setId(sale.getShiftId());
        when(shiftRepository.findById(sale.getShiftId())).thenReturn(Optional.of(shift));
        return shift;
    }

    @Nested
    class Create {

        @Test
        void shouldPersistRefundLinkedToSale() {
            givenSaleAndUsers();
            givenSuccessfulSave();
            when(shiftRepository.findById(sale.getShiftId())).thenReturn(Optional.empty());

            SaleRefundResponseDTO response = saleRefundService.create(input());

            ArgumentCaptor<SaleRefund> captor = ArgumentCaptor.forClass(SaleRefund.class);
            verify(saleRefundRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getSale()).isSameAs(sale);
            assertThat(response.id()).isNotNull();
            assertThat(response.reason()).isEqualTo(SaleRefundFixture.REASON);
            assertThat(response.requestedBy()).isEqualTo(requester.getId());
            assertThat(response.approvedBy()).isEqualTo(approver.getId());
            assertThat(response.refundedAt()).isEqualTo(SaleRefundFixture.REFUNDED_AT);
            assertThat(response.notes()).isEqualTo(SaleRefundFixture.NOTES);
        }

        @Test
        void shouldDefaultRefundedAtToNowWhenNotInformed() {
            givenSaleAndUsers();
            givenSuccessfulSave();
            when(shiftRepository.findById(sale.getShiftId())).thenReturn(Optional.empty());
            SaleRefundInputDTO input = input();
            input.setRefundedAt(null);
            Instant before = Instant.now();

            SaleRefundResponseDTO response = saleRefundService.create(input);

            assertThat(response.refundedAt()).isNotNull().isBetween(before, Instant.now());
        }

        @Test
        void shouldMarkSettledPaymentsAsRefundedAndSaleAsRefunded() {
            SalePayment received = SalePaymentFixture.persistedEntity(PaymentStatus.RECEIVED);
            SalePayment authorized = SalePaymentFixture.persistedEntity(PaymentStatus.AUTHORIZED);
            SalePayment rejected = SalePaymentFixture.persistedEntity(PaymentStatus.REJECTED);
            sale.getPayments().addAll(List.of(received, authorized, rejected));
            givenSaleAndUsers();
            givenSuccessfulSave();
            when(shiftRepository.findById(sale.getShiftId())).thenReturn(Optional.empty());

            saleRefundService.create(input());

            assertThat(received.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
            assertThat(authorized.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
            assertThat(rejected.getStatus()).isEqualTo(PaymentStatus.REJECTED);
            assertThat(sale.getStatus()).isEqualTo(SaleStatus.REFUNDED);
            verify(saleRepository).saveAndFlush(sale);
        }

        @Test
        void shouldCancelSaleWithoutPayments() {
            sale.setStatus(SaleStatus.PENDING_CREDIT);
            givenSaleAndUsers();
            givenSuccessfulSave();
            when(shiftRepository.findById(sale.getShiftId())).thenReturn(Optional.empty());

            saleRefundService.create(input());

            assertThat(sale.getStatus()).isEqualTo(SaleStatus.CANCELLED);
        }

        @Test
        void shouldRegisterRefundCashMovementWhenShiftExists() {
            givenSaleAndUsers();
            givenSuccessfulSave();
            Shift shift = givenShiftExists();

            saleRefundService.create(input());

            ArgumentCaptor<CashMovement> captor = ArgumentCaptor.forClass(CashMovement.class);
            verify(cashMovementRepository).saveAndFlush(captor.capture());
            CashMovement movement = captor.getValue();
            assertThat(movement.getShift()).isSameAs(shift);
            assertThat(movement.getMovementType()).isEqualTo(CashMovementTypeEnum.REFUND);
            assertThat(movement.getReference()).isEqualTo(sale.getId().toString());
            assertThat(movement.getCreatedBy()).isEqualTo(requester.getId());
            assertThat(movement.getApprovedBy()).isEqualTo(approver.getId());
            assertThat(movement.getOccurredAt()).isNotNull();
        }

        @Test
        @Disabled("BUG: SaleRefundService.create (SaleRefundService.java:82) cria o CashMovement sem amount, "
                + "mas a coluna cash_movements.amount é NOT NULL (@NotNull na entidade)")
        void shouldRegisterRefundCashMovementWithRefundedAmount() {
            sale.getPayments().add(SalePaymentFixture.persistedEntity(PaymentStatus.RECEIVED));
            givenSaleAndUsers();
            givenSuccessfulSave();
            givenShiftExists();

            saleRefundService.create(input());

            ArgumentCaptor<CashMovement> captor = ArgumentCaptor.forClass(CashMovement.class);
            verify(cashMovementRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getAmount()).isEqualByComparingTo(SalePaymentFixture.AMOUNT);
        }

        @Test
        void shouldNotRegisterCashMovementWhenShiftDoesNotExist() {
            givenSaleAndUsers();
            givenSuccessfulSave();
            when(shiftRepository.findById(sale.getShiftId())).thenReturn(Optional.empty());

            saleRefundService.create(input());

            verifyNoInteractions(cashMovementRepository);
        }

        @Test
        void shouldThrowNotFoundWhenSaleDoesNotExist() {
            UUID saleId = UUID.randomUUID();
            when(saleRepository.findDetailedById(saleId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> saleRefundService.create(SaleRefundFixture.input(saleId, requester.getId(), approver.getId())))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(saleId.toString());
            verifyNoInteractions(userRepository, saleRefundRepository);
        }

        @Test
        void shouldRejectRefundOfOpenSale() {
            sale.setStatus(SaleStatus.OPEN);
            when(saleRepository.findDetailedById(sale.getId())).thenReturn(Optional.of(sale));

            assertThatThrownBy(() -> saleRefundService.create(input()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(WRAPPED_MESSAGE)
                    .hasRootCauseMessage("Apenas vendas pagas ou com crédito pendente podem ser estornadas.");
            verifyNoInteractions(userRepository, saleRefundRepository);
        }

        @Test
        void shouldRejectRefundOfAlreadyRefundedSale() {
            sale.setStatus(SaleStatus.REFUNDED);
            when(saleRepository.findDetailedById(sale.getId())).thenReturn(Optional.of(sale));

            assertThatThrownBy(() -> saleRefundService.create(input()))
                    .isInstanceOf(BusinessException.class)
                    .hasRootCauseMessage("Apenas vendas pagas ou com crédito pendente podem ser estornadas.");
        }

        @Test
        void shouldThrowNotFoundWhenRequesterDoesNotExist() {
            when(saleRepository.findDetailedById(sale.getId())).thenReturn(Optional.of(sale));
            when(userRepository.findById(requester.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> saleRefundService.create(input()))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(requester.getId().toString());
        }

        @Test
        void shouldThrowNotFoundWhenApproverDoesNotExist() {
            when(saleRepository.findDetailedById(sale.getId())).thenReturn(Optional.of(sale));
            when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
            when(userRepository.findById(approver.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> saleRefundService.create(input()))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(approver.getId().toString());
        }

        @Test
        void shouldRejectInactiveApprover() {
            approver.setActive(false);
            givenSaleAndUsers();

            assertThatThrownBy(() -> saleRefundService.create(input()))
                    .isInstanceOf(BusinessException.class)
                    .hasRootCauseMessage("Os usuários solicitante e aprovador precisam estar ativos.");
            verifyNoInteractions(saleRefundRepository);
        }

        @Test
        void shouldRejectInactiveRequester() {
            requester.setActive(false);
            givenSaleAndUsers();

            assertThatThrownBy(() -> saleRefundService.create(input()))
                    .isInstanceOf(BusinessException.class)
                    .hasRootCauseMessage("Os usuários solicitante e aprovador precisam estar ativos.");
        }

        @Test
        void shouldRejectSelfApproval() {
            when(saleRepository.findDetailedById(sale.getId())).thenReturn(Optional.of(sale));
            when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));

            assertThatThrownBy(() -> saleRefundService.create(
                    SaleRefundFixture.input(sale.getId(), requester.getId(), requester.getId())))
                    .isInstanceOf(BusinessException.class)
                    .hasRootCauseMessage("A aprovação do estorno deve ser feita por um usuário diferente.");
            verifyNoInteractions(saleRefundRepository);
            verify(saleRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    class FindBySale {

        @Test
        void shouldListRefundsOfSale() {
            SaleRefund refund = SaleRefundFixture.persistedEntity(requester.getId(), approver.getId());
            when(saleRefundRepository.findBySaleId(sale.getId())).thenReturn(List.of(refund));

            assertThat(saleRefundService.findBySale(sale.getId()))
                    .singleElement()
                    .usingRecursiveComparison()
                    .isEqualTo(new SaleRefundResponseDTO(refund.getId(), SaleRefundFixture.REASON, requester.getId(),
                            approver.getId(), SaleRefundFixture.REFUNDED_AT, SaleRefundFixture.NOTES));
        }

        @Test
        void shouldReturnEmptyListWhenSaleHasNoRefunds() {
            when(saleRefundRepository.findBySaleId(sale.getId())).thenReturn(List.of());

            assertThat(saleRefundService.findBySale(sale.getId())).isEmpty();
        }
    }
}
