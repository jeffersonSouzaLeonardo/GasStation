package com.br.manager.domain.price_acquisition.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.entity.Supplier;
import com.br.manager.domain.operational.repository.SupplierRepository;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.organization.repository.UserRepository;
import com.br.manager.domain.price_acquisition.dto.PurchaseOrderInputDTO;
import com.br.manager.domain.price_acquisition.dto.PurchaseOrderItemInputDTO;
import com.br.manager.domain.price_acquisition.dto.PurchaseOrderResponseDTO;
import com.br.manager.domain.price_acquisition.entity.PurchaseOrder;
import com.br.manager.domain.price_acquisition.entity.PurchaseOrderItem;
import com.br.manager.domain.price_acquisition.fixture.PriceAcquisitionReferenceFixture;
import com.br.manager.domain.price_acquisition.fixture.PurchaseOrderFixture;
import com.br.manager.domain.price_acquisition.fixture.PurchaseOrderItemFixture;
import com.br.manager.domain.price_acquisition.mapper.PurchaseOrderMapper;
import com.br.manager.domain.price_acquisition.repository.PurchaseOrderItemRepository;
import com.br.manager.domain.price_acquisition.repository.PurchaseOrderRepository;
import com.br.manager.domain.price_acquisition.support.PriceAcquisitionViolations;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Mock
    private PurchaseOrderItemService purchaseOrderItemService;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private PurchaseOrderMapper purchaseOrderMapper = new PurchaseOrderMapper();

    @InjectMocks
    private PurchaseOrderService purchaseOrderService;

    private final Supplier supplier = PriceAcquisitionReferenceFixture.activeSupplier();
    private final User creator = PriceAcquisitionReferenceFixture.activeUser();

    private PurchaseOrderInputDTO validInput() {
        return PurchaseOrderFixture.validInput(supplier.getId(), creator.getId(),
                List.of(PurchaseOrderItemFixture.validInput(null, UUID.randomUUID())));
    }

    private PurchaseOrder stubExisting(String status) {
        PurchaseOrder existing = PurchaseOrderFixture.persistedEntity(status, supplier, creator);
        when(purchaseOrderRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
        return existing;
    }

    private void stubSaveReturningArgument() {
        when(purchaseOrderRepository.saveAndFlush(any(PurchaseOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Nested
    class Create {

        @Test
        void shouldCreateDraftOrderWithSupplierCreatorAndItems() {
            PurchaseOrderInputDTO input = validInput();
            input.setId(UUID.randomUUID());
            input.setStatus(PurchaseOrder.Status.COMPLETED);
            UUID generatedId = UUID.randomUUID();
            when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplier));
            when(userRepository.findById(creator.getId())).thenReturn(Optional.of(creator));
            ArgumentCaptor<PurchaseOrder> captor = ArgumentCaptor.forClass(PurchaseOrder.class);
            when(purchaseOrderRepository.saveAndFlush(captor.capture())).thenAnswer(invocation -> {
                PurchaseOrder order = invocation.getArgument(0);
                assertThat(order.getId()).isNull();
                order.setId(generatedId);
                return order;
            });
            PurchaseOrderItem savedItem = PurchaseOrderItemFixture.persistedEntity(null);
            when(purchaseOrderItemRepository.findByPurchaseOrderId(generatedId)).thenReturn(List.of(savedItem));

            PurchaseOrderResponseDTO response = purchaseOrderService.create(input);

            PurchaseOrder saved = captor.getValue();
            assertThat(saved.getStatus()).isEqualTo(PurchaseOrder.Status.DRAFT);
            assertThat(saved.getSupplier()).isSameAs(supplier);
            assertThat(saved.getCreatedBy()).isSameAs(creator);
            assertThat(saved.getOrderedAt()).isEqualTo(PurchaseOrderFixture.ORDERED_AT);
            assertThat(saved.getExpectedAt()).isEqualTo(PurchaseOrderFixture.EXPECTED_AT);
            verify(purchaseOrderItemService).savePurchaseOrderItems(generatedId, input.getItems());
            assertThat(response.getId()).isEqualTo(generatedId);
            assertThat(response.getStatus()).isEqualTo(PurchaseOrder.Status.DRAFT);
            assertThat(response.getSupplierId()).isEqualTo(supplier.getId());
            assertThat(response.getCreatedBy()).isEqualTo(creator.getId());
            assertThat(response.getItems()).hasSize(1);
            assertThat(response.getTotalAmount()).isEqualByComparingTo(PurchaseOrderItemFixture.TOTAL_COST);
        }

        @Test
        void shouldDefaultOrderedAtToNowWhenNotInformed() {
            PurchaseOrderInputDTO input = validInput();
            input.setOrderedAt(null);
            when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplier));
            when(userRepository.findById(creator.getId())).thenReturn(Optional.of(creator));
            stubSaveReturningArgument();
            LocalDateTime before = LocalDateTime.now();

            PurchaseOrderResponseDTO response = purchaseOrderService.create(input);

            assertThat(response.getOrderedAt()).isBetween(before, LocalDateTime.now());
        }

        @Test
        void shouldRejectNullPayload() {
            assertThatThrownBy(() -> purchaseOrderService.create(null))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("O payload do pedido de compra é obrigatório.");
        }

        @Test
        void shouldRequireSupplier() {
            PurchaseOrderInputDTO input = validInput();
            input.setSupplierId(null);

            assertThatThrownBy(() -> purchaseOrderService.create(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("O ID do fornecedor é obrigatório.");
        }

        @Test
        void shouldRequireCreator() {
            PurchaseOrderInputDTO input = validInput();
            input.setCreatedBy(null);

            assertThatThrownBy(() -> purchaseOrderService.create(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("O usuário criador é obrigatório.");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   "})
        void shouldRequireNumber(String number) {
            PurchaseOrderInputDTO input = validInput();
            input.setNumber(number);

            assertThatThrownBy(() -> purchaseOrderService.create(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("O número do pedido de compra é obrigatório.");
        }

        @Test
        void shouldRequireNonNullNumber() {
            PurchaseOrderInputDTO input = validInput();
            input.setNumber(null);

            assertThatThrownBy(() -> purchaseOrderService.create(input))
                    .hasMessage("O número do pedido de compra é obrigatório.");
        }

        @Test
        void shouldRequireExpectedAt() {
            PurchaseOrderInputDTO input = validInput();
            input.setExpectedAt(null);

            assertThatThrownBy(() -> purchaseOrderService.create(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("A data prevista é obrigatória.");
        }

        @Test
        void shouldRequireAtLeastOneItem() {
            PurchaseOrderInputDTO empty = validInput();
            empty.setItems(List.of());
            PurchaseOrderInputDTO nullItems = validInput();
            nullItems.setItems(null);

            assertThatThrownBy(() -> purchaseOrderService.create(empty))
                    .hasMessage("O pedido de compra deve conter pelo menos um item.");
            assertThatThrownBy(() -> purchaseOrderService.create(nullItems))
                    .hasMessage("O pedido de compra deve conter pelo menos um item.");
            verifyNoInteractions(purchaseOrderRepository);
        }

        @Test
        void shouldThrowNotFoundWhenSupplierDoesNotExist() {
            when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> purchaseOrderService.create(validInput()))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(supplier.getId().toString());
            verify(purchaseOrderRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldThrowNotFoundWhenSupplierIsInactive() {
            Supplier inactive = PriceAcquisitionReferenceFixture.inactiveSupplier();
            PurchaseOrderInputDTO input = validInput();
            input.setSupplierId(inactive.getId());
            when(supplierRepository.findById(inactive.getId())).thenReturn(Optional.of(inactive));

            assertThatThrownBy(() -> purchaseOrderService.create(input))
                    .isInstanceOf(NotFoundBusinessException.class);
            verify(purchaseOrderRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldThrowNotFoundWhenCreatorDoesNotExist() {
            when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplier));
            when(userRepository.findById(creator.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> purchaseOrderService.create(validInput()))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(creator.getId().toString());
        }

        @Test
        void shouldThrowNotFoundWhenCreatorIsInactive() {
            User inactive = PriceAcquisitionReferenceFixture.inactiveUser();
            PurchaseOrderInputDTO input = validInput();
            input.setCreatedBy(inactive.getId());
            when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplier));
            when(userRepository.findById(inactive.getId())).thenReturn(Optional.of(inactive));

            assertThatThrownBy(() -> purchaseOrderService.create(input))
                    .isInstanceOf(NotFoundBusinessException.class);
            verify(purchaseOrderRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoBusinessException() {
            when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplier));
            when(userRepository.findById(creator.getId())).thenReturn(Optional.of(creator));
            when(purchaseOrderRepository.saveAndFlush(any(PurchaseOrder.class)))
                    .thenThrow(PriceAcquisitionViolations.constraintViolationException());

            assertThatThrownBy(() -> purchaseOrderService.create(validInput()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PriceAcquisitionViolations.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapItemErrorInBusinessException() {
            PurchaseOrderInputDTO input = validInput();
            when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplier));
            when(userRepository.findById(creator.getId())).thenReturn(Optional.of(creator));
            stubSaveReturningArgument();
            doThrow(new DataIntegrityViolationException("fk"))
                    .when(purchaseOrderItemService).savePurchaseOrderItems(any(), any());

            assertThatThrownBy(() -> purchaseOrderService.create(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao salvar pedido de compra")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateInformedFieldsAndReplaceItems() {
            PurchaseOrder existing = stubExisting(PurchaseOrder.Status.DRAFT);
            PurchaseOrderInputDTO input = new PurchaseOrderInputDTO();
            input.setId(existing.getId());
            input.setNumber(PurchaseOrderFixture.UPDATED_NUMBER);
            input.setOrderedAt(PurchaseOrderFixture.ORDERED_AT.plusDays(1));
            input.setExpectedAt(PurchaseOrderFixture.EXPECTED_AT.plusDays(1));
            input.setStatus(PurchaseOrder.Status.APPROVED);
            List<PurchaseOrderItemInputDTO> items = List.of(PurchaseOrderItemFixture.validInput(existing.getId(), UUID.randomUUID()));
            input.setItems(items);
            when(purchaseOrderRepository.saveAndFlush(existing)).thenReturn(existing);
            when(purchaseOrderItemRepository.findByPurchaseOrderId(existing.getId()))
                    .thenReturn(List.of(PurchaseOrderItemFixture.persistedEntity(existing)));

            PurchaseOrderResponseDTO response = purchaseOrderService.update(input);

            assertThat(response.getNumber()).isEqualTo(PurchaseOrderFixture.UPDATED_NUMBER);
            assertThat(response.getOrderedAt()).isEqualTo(PurchaseOrderFixture.ORDERED_AT.plusDays(1));
            assertThat(response.getExpectedAt()).isEqualTo(PurchaseOrderFixture.EXPECTED_AT.plusDays(1));
            assertThat(response.getStatus()).isEqualTo(PurchaseOrder.Status.APPROVED);
            assertThat(response.getItems()).hasSize(1);
            verify(purchaseOrderItemService).replaceByPurchaseOrder(existing, items);
        }

        @Test
        void shouldKeepCurrentValuesWhenFieldsAreBlankOrNull() {
            PurchaseOrder existing = stubExisting(PurchaseOrder.Status.DRAFT);
            PurchaseOrderInputDTO input = new PurchaseOrderInputDTO();
            input.setId(existing.getId());
            input.setNumber(" ");
            input.setStatus("");
            when(purchaseOrderRepository.saveAndFlush(existing)).thenReturn(existing);

            PurchaseOrderResponseDTO response = purchaseOrderService.update(input);

            assertThat(response.getNumber()).isEqualTo(PurchaseOrderFixture.NUMBER);
            assertThat(response.getStatus()).isEqualTo(PurchaseOrder.Status.DRAFT);
            assertThat(response.getOrderedAt()).isEqualTo(PurchaseOrderFixture.ORDERED_AT);
            verify(purchaseOrderItemService, never()).replaceByPurchaseOrder(any(), any());
            verify(purchaseOrderItemRepository, never()).findByPurchaseOrderId(any());
        }

        @Test
        void shouldThrowNotFoundWhenOrderDoesNotExist() {
            PurchaseOrderInputDTO input = validInput();
            input.setId(UUID.randomUUID());
            when(purchaseOrderRepository.findById(input.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> purchaseOrderService.update(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(input.getId().toString());
        }

        @Test
        @Disabled("BUG: PurchaseOrderService.update chama inputDTO.getId() (linha 107) antes da checagem "
                + "'inputDTO == null' (linha 111), que vira codigo morto; payload nulo gera NPE e a mensagem generica "
                + "'Erro ao atualizar pedido de compra' em vez da mensagem de validacao.")
        void shouldRejectNullPayloadWithValidationMessage() {
            assertThatThrownBy(() -> purchaseOrderService.update(null))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("O payload do pedido de compra é obrigatório.");
        }

        @Test
        void shouldConvertConstraintViolationIntoBusinessException() {
            PurchaseOrder existing = stubExisting(PurchaseOrder.Status.DRAFT);
            PurchaseOrderInputDTO input = new PurchaseOrderInputDTO();
            input.setId(existing.getId());
            when(purchaseOrderRepository.saveAndFlush(existing)).thenThrow(PriceAcquisitionViolations.constraintViolationException());

            assertThatThrownBy(() -> purchaseOrderService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PriceAcquisitionViolations.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            PurchaseOrder existing = stubExisting(PurchaseOrder.Status.DRAFT);
            PurchaseOrderInputDTO input = new PurchaseOrderInputDTO();
            input.setId(existing.getId());
            when(purchaseOrderRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("db"));

            assertThatThrownBy(() -> purchaseOrderService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao atualizar pedido de compra")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class StatusTransitions {

        private void assertSavedStatus(PurchaseOrderResponseDTO response, String expected) {
            ArgumentCaptor<PurchaseOrder> captor = ArgumentCaptor.forClass(PurchaseOrder.class);
            verify(purchaseOrderRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(expected);
            assertThat(response.getStatus()).isEqualTo(expected);
        }

        @Test
        void shouldApproveDraftOrder() {
            PurchaseOrder existing = stubExisting(PurchaseOrder.Status.DRAFT);
            stubSaveReturningArgument();

            assertSavedStatus(purchaseOrderService.approve(existing.getId()), PurchaseOrder.Status.APPROVED);
        }

        @Test
        void shouldSendApprovedOrder() {
            PurchaseOrder existing = stubExisting(PurchaseOrder.Status.APPROVED);
            stubSaveReturningArgument();

            assertSavedStatus(purchaseOrderService.send(existing.getId()), PurchaseOrder.Status.SENT);
        }

        @Test
        void shouldCompleteSentOrder() {
            PurchaseOrder existing = stubExisting(PurchaseOrder.Status.SENT);
            stubSaveReturningArgument();

            assertSavedStatus(purchaseOrderService.complete(existing.getId()), PurchaseOrder.Status.COMPLETED);
        }

        @ParameterizedTest
        @ValueSource(strings = {PurchaseOrder.Status.DRAFT, PurchaseOrder.Status.APPROVED, PurchaseOrder.Status.SENT})
        void shouldCancelOpenOrder(String status) {
            PurchaseOrder existing = stubExisting(status);
            stubSaveReturningArgument();

            assertSavedStatus(purchaseOrderService.cancel(existing.getId()), PurchaseOrder.Status.CANCELED);
        }

        @ParameterizedTest
        @ValueSource(strings = {PurchaseOrder.Status.COMPLETED, PurchaseOrder.Status.CANCELED})
        void shouldRejectCancelOfClosedOrder(String status) {
            PurchaseOrder existing = stubExisting(status);

            assertThatThrownBy(() -> purchaseOrderService.cancel(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Apenas pedidos em rascunho, aprovados ou enviados podem ser cancelados.");
            verify(purchaseOrderRepository, never()).saveAndFlush(any());
        }

        @ParameterizedTest
        @ValueSource(strings = {PurchaseOrder.Status.APPROVED, PurchaseOrder.Status.SENT, PurchaseOrder.Status.COMPLETED, PurchaseOrder.Status.CANCELED})
        void shouldRejectApproveWhenNotDraft(String status) {
            PurchaseOrder existing = stubExisting(status);

            assertThatThrownBy(() -> purchaseOrderService.approve(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("O pedido de compra só pode mudar de DRAFT para APPROVED.");
            verify(purchaseOrderRepository, never()).saveAndFlush(any());
        }

        @ParameterizedTest
        @ValueSource(strings = {PurchaseOrder.Status.DRAFT, PurchaseOrder.Status.SENT, PurchaseOrder.Status.COMPLETED})
        void shouldRejectSendWhenNotApproved(String status) {
            PurchaseOrder existing = stubExisting(status);

            assertThatThrownBy(() -> purchaseOrderService.send(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("O pedido de compra só pode mudar de APPROVED para SENT.");
        }

        @ParameterizedTest
        @ValueSource(strings = {PurchaseOrder.Status.DRAFT, PurchaseOrder.Status.APPROVED, PurchaseOrder.Status.CANCELED})
        void shouldRejectCompleteWhenNotSent(String status) {
            PurchaseOrder existing = stubExisting(status);

            assertThatThrownBy(() -> purchaseOrderService.complete(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("O pedido de compra só pode mudar de SENT para COMPLETED.");
        }

        @Test
        void shouldThrowNotFoundForUnknownOrder() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> purchaseOrderService.approve(id)).isInstanceOf(NotFoundBusinessException.class);
            assertThatThrownBy(() -> purchaseOrderService.send(id)).isInstanceOf(NotFoundBusinessException.class);
            assertThatThrownBy(() -> purchaseOrderService.complete(id)).isInstanceOf(NotFoundBusinessException.class);
            assertThatThrownBy(() -> purchaseOrderService.cancel(id)).isInstanceOf(NotFoundBusinessException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldFindByIdLoadingItems() {
            PurchaseOrder existing = stubExisting(PurchaseOrder.Status.DRAFT);
            when(purchaseOrderItemRepository.findByPurchaseOrderId(existing.getId()))
                    .thenReturn(List.of(PurchaseOrderItemFixture.persistedEntity(existing)));

            PurchaseOrderResponseDTO response = purchaseOrderService.findById(existing.getId());

            assertThat(response.getId()).isEqualTo(existing.getId());
            assertThat(response.getItems()).hasSize(1);
            assertThat(response.getTotalAmount()).isEqualByComparingTo(PurchaseOrderItemFixture.TOTAL_COST);
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> purchaseOrderService.findById(id))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(id.toString());
        }

        @Test
        void shouldListAllLoadingItems() {
            PurchaseOrder first = PurchaseOrderFixture.persistedEntity(PurchaseOrder.Status.DRAFT);
            PurchaseOrder second = PurchaseOrderFixture.persistedEntity(PurchaseOrder.Status.SENT);
            when(purchaseOrderRepository.findAll()).thenReturn(List.of(first, second));
            when(purchaseOrderItemRepository.findByPurchaseOrderId(first.getId()))
                    .thenReturn(List.of(PurchaseOrderItemFixture.persistedEntity(first)));

            List<PurchaseOrderResponseDTO> response = purchaseOrderService.findAll();

            assertThat(response).extracting(PurchaseOrderResponseDTO::getId).containsExactly(first.getId(), second.getId());
            assertThat(response.get(0).getItems()).hasSize(1);
            assertThat(response.get(1).getItems()).isEmpty();
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldDeleteExistingOrder() {
            PurchaseOrder existing = stubExisting(PurchaseOrder.Status.DRAFT);

            purchaseOrderService.delete(existing.getId());

            verify(purchaseOrderRepository).delete(existing);
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownOrder() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> purchaseOrderService.delete(id)).isInstanceOf(NotFoundBusinessException.class);
            verify(purchaseOrderRepository, never()).delete(any());
        }
    }
}
