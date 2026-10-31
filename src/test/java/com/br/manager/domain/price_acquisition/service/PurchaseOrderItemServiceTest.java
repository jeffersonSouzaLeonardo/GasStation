package com.br.manager.domain.price_acquisition.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.price_acquisition.dto.PurchaseOrderItemInputDTO;
import com.br.manager.domain.price_acquisition.dto.PurchaseOrderItemResponseDTO;
import com.br.manager.domain.price_acquisition.entity.PurchaseOrder;
import com.br.manager.domain.price_acquisition.entity.PurchaseOrderItem;
import com.br.manager.domain.price_acquisition.fixture.PurchaseOrderFixture;
import com.br.manager.domain.price_acquisition.fixture.PurchaseOrderItemFixture;
import com.br.manager.domain.price_acquisition.mapper.PurchaseOrderItemMapper;
import com.br.manager.domain.price_acquisition.repository.PurchaseOrderItemRepository;
import com.br.manager.domain.price_acquisition.repository.PurchaseOrderRepository;
import com.br.manager.domain.price_acquisition.support.PriceAcquisitionViolations;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderItemServiceTest {

    @Mock
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Spy
    private PurchaseOrderItemMapper purchaseOrderItemMapper = new PurchaseOrderItemMapper();

    @InjectMocks
    private PurchaseOrderItemService purchaseOrderItemService;

    private final PurchaseOrder purchaseOrder = PurchaseOrderFixture.persistedEntity(PurchaseOrder.Status.DRAFT);

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<List<PurchaseOrderItem>> listCaptor() {
        return ArgumentCaptor.forClass(List.class);
    }

    @Nested
    class Create {

        @Test
        void shouldCreateItemLinkedToOrder() {
            PurchaseOrderItemInputDTO input = PurchaseOrderItemFixture.validInput(purchaseOrder.getId(), UUID.randomUUID());
            when(purchaseOrderRepository.findById(purchaseOrder.getId())).thenReturn(Optional.of(purchaseOrder));
            ArgumentCaptor<PurchaseOrderItem> captor = ArgumentCaptor.forClass(PurchaseOrderItem.class);
            when(purchaseOrderItemRepository.saveAndFlush(captor.capture())).thenAnswer(invocation -> {
                PurchaseOrderItem item = invocation.getArgument(0);
                item.setId(UUID.randomUUID());
                return item;
            });

            PurchaseOrderItemResponseDTO response = purchaseOrderItemService.create(input);

            assertThat(captor.getValue().getPurchaseOrder()).isSameAs(purchaseOrder);
            assertThat(response.getId()).isNotNull();
            assertThat(response.getPurchaseOrderId()).isEqualTo(purchaseOrder.getId());
            assertThat(response.getProductId()).isEqualTo(input.getProductId());
            assertThat(response.getQuantity()).isEqualTo(PurchaseOrderItemFixture.QUANTITY);
            assertThat(response.getTotalCost()).isEqualByComparingTo(PurchaseOrderItemFixture.TOTAL_COST);
        }

        @Test
        void shouldRejectNullPayload() {
            assertThatThrownBy(() -> purchaseOrderItemService.create(null))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao criar item do pedido de compra")
                    .hasRootCauseMessage("O payload do item do pedido de compra é obrigatório.");
        }

        @Test
        void shouldRequirePurchaseOrderId() {
            PurchaseOrderItemInputDTO input = PurchaseOrderItemFixture.validInput(null, UUID.randomUUID());

            assertThatThrownBy(() -> purchaseOrderItemService.create(input))
                    .isInstanceOf(BusinessException.class)
                    .hasRootCauseMessage("O ID do pedido de compra é obrigatório.");
            verifyNoInteractions(purchaseOrderRepository, purchaseOrderItemRepository);
        }

        @Test
        void shouldRequireProductId() {
            PurchaseOrderItemInputDTO input = PurchaseOrderItemFixture.validInput(purchaseOrder.getId(), null);

            assertThatThrownBy(() -> purchaseOrderItemService.create(input))
                    .hasRootCauseMessage("O ID do produto é obrigatório.");
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1})
        void shouldRequirePositiveQuantity(int quantity) {
            PurchaseOrderItemInputDTO input = PurchaseOrderItemFixture.validInput(purchaseOrder.getId(), UUID.randomUUID());
            input.setQuantity(quantity);

            assertThatThrownBy(() -> purchaseOrderItemService.create(input))
                    .hasRootCauseMessage("A quantidade deve ser maior que zero.");
        }

        @Test
        void shouldRequireQuantity() {
            PurchaseOrderItemInputDTO input = PurchaseOrderItemFixture.validInput(purchaseOrder.getId(), UUID.randomUUID());
            input.setQuantity(null);

            assertThatThrownBy(() -> purchaseOrderItemService.create(input))
                    .hasRootCauseMessage("A quantidade deve ser maior que zero.");
        }

        @Test
        void shouldRequireUnitCost() {
            PurchaseOrderItemInputDTO input = PurchaseOrderItemFixture.validInput(purchaseOrder.getId(), UUID.randomUUID());
            input.setUnitCost(null);

            assertThatThrownBy(() -> purchaseOrderItemService.create(input))
                    .hasRootCauseMessage("O custo unitário é obrigatório.");
        }

        @Test
        void shouldThrowNotFoundWhenOrderDoesNotExist() {
            PurchaseOrderItemInputDTO input = PurchaseOrderItemFixture.validInput(UUID.randomUUID(), UUID.randomUUID());
            when(purchaseOrderRepository.findById(input.getPurchaseOrderId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> purchaseOrderItemService.create(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(input.getPurchaseOrderId().toString());
            verify(purchaseOrderItemRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoBusinessException() {
            PurchaseOrderItemInputDTO input = PurchaseOrderItemFixture.validInput(purchaseOrder.getId(), UUID.randomUUID());
            when(purchaseOrderRepository.findById(purchaseOrder.getId())).thenReturn(Optional.of(purchaseOrder));
            when(purchaseOrderItemRepository.saveAndFlush(any(PurchaseOrderItem.class)))
                    .thenThrow(PriceAcquisitionViolations.constraintViolationException());

            assertThatThrownBy(() -> purchaseOrderItemService.create(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PriceAcquisitionViolations.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            PurchaseOrderItemInputDTO input = PurchaseOrderItemFixture.validInput(purchaseOrder.getId(), UUID.randomUUID());
            when(purchaseOrderRepository.findById(purchaseOrder.getId())).thenReturn(Optional.of(purchaseOrder));
            when(purchaseOrderItemRepository.saveAndFlush(any(PurchaseOrderItem.class)))
                    .thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> purchaseOrderItemService.create(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao criar item do pedido de compra")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateInformedFields() {
            PurchaseOrderItem existing = PurchaseOrderItemFixture.persistedEntity(purchaseOrder);
            PurchaseOrderItemInputDTO input = new PurchaseOrderItemInputDTO();
            input.setId(existing.getId());
            input.setQuantity(PurchaseOrderItemFixture.UPDATED_QUANTITY);
            when(purchaseOrderItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(purchaseOrderItemRepository.saveAndFlush(existing)).thenReturn(existing);

            PurchaseOrderItemResponseDTO response = purchaseOrderItemService.update(input);

            assertThat(response.getQuantity()).isEqualTo(PurchaseOrderItemFixture.UPDATED_QUANTITY);
            assertThat(response.getUnitCost()).isEqualByComparingTo(PurchaseOrderItemFixture.UNIT_COST);
            assertThat(response.getTotalCost()).isEqualByComparingTo(PurchaseOrderItemFixture.UPDATED_TOTAL_COST);
            verify(purchaseOrderRepository, never()).findById(any());
        }

        @Test
        void shouldMoveItemToAnotherOrder() {
            PurchaseOrderItem existing = PurchaseOrderItemFixture.persistedEntity(purchaseOrder);
            PurchaseOrder other = PurchaseOrderFixture.persistedEntity(PurchaseOrder.Status.DRAFT);
            PurchaseOrderItemInputDTO input = new PurchaseOrderItemInputDTO();
            input.setId(existing.getId());
            input.setPurchaseOrderId(other.getId());
            when(purchaseOrderItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(purchaseOrderRepository.findById(other.getId())).thenReturn(Optional.of(other));
            when(purchaseOrderItemRepository.saveAndFlush(existing)).thenReturn(existing);

            assertThat(purchaseOrderItemService.update(input).getPurchaseOrderId()).isEqualTo(other.getId());
        }

        @Test
        void shouldRequireId() {
            PurchaseOrderItemInputDTO input = new PurchaseOrderItemInputDTO();

            assertThatThrownBy(() -> purchaseOrderItemService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao atualizar item do pedido de compra")
                    .hasRootCauseMessage("O ID do item do pedido de compra é obrigatório para atualização.");
            assertThatThrownBy(() -> purchaseOrderItemService.update(null))
                    .hasRootCauseMessage("O ID do item do pedido de compra é obrigatório para atualização.");
            verifyNoInteractions(purchaseOrderItemRepository);
        }

        @Test
        void shouldThrowNotFoundWhenItemDoesNotExist() {
            PurchaseOrderItemInputDTO input = new PurchaseOrderItemInputDTO();
            input.setId(UUID.randomUUID());
            when(purchaseOrderItemRepository.findById(input.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> purchaseOrderItemService.update(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(input.getId().toString());
        }

        @Test
        void shouldThrowNotFoundWhenNewOrderDoesNotExist() {
            PurchaseOrderItem existing = PurchaseOrderItemFixture.persistedEntity(purchaseOrder);
            PurchaseOrderItemInputDTO input = new PurchaseOrderItemInputDTO();
            input.setId(existing.getId());
            input.setPurchaseOrderId(UUID.randomUUID());
            when(purchaseOrderItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(purchaseOrderRepository.findById(input.getPurchaseOrderId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> purchaseOrderItemService.update(input))
                    .isInstanceOf(NotFoundBusinessException.class);
            verify(purchaseOrderItemRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoBusinessException() {
            PurchaseOrderItem existing = PurchaseOrderItemFixture.persistedEntity(purchaseOrder);
            PurchaseOrderItemInputDTO input = new PurchaseOrderItemInputDTO();
            input.setId(existing.getId());
            when(purchaseOrderItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(purchaseOrderItemRepository.saveAndFlush(existing)).thenThrow(PriceAcquisitionViolations.constraintViolationException());

            assertThatThrownBy(() -> purchaseOrderItemService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PriceAcquisitionViolations.VIOLATION_MESSAGE);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldDeleteExistingItem() {
            UUID id = UUID.randomUUID();
            when(purchaseOrderItemRepository.existsById(id)).thenReturn(true);

            purchaseOrderItemService.delete(id);

            verify(purchaseOrderItemRepository).deleteById(id);
        }

        @Test
        void shouldThrowNotFoundWhenItemDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(purchaseOrderItemRepository.existsById(id)).thenReturn(false);

            assertThatThrownBy(() -> purchaseOrderItemService.delete(id))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(id.toString());
            verify(purchaseOrderItemRepository, never()).deleteById(any());
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            UUID id = UUID.randomUUID();
            when(purchaseOrderItemRepository.existsById(id)).thenReturn(true);
            doThrow(new DataIntegrityViolationException("fk")).when(purchaseOrderItemRepository).deleteById(id);

            assertThatThrownBy(() -> purchaseOrderItemService.delete(id))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao excluir item do pedido de compra")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldFindById() {
            PurchaseOrderItem existing = PurchaseOrderItemFixture.persistedEntity(purchaseOrder);
            when(purchaseOrderItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            assertThat(purchaseOrderItemService.findById(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> purchaseOrderItemService.findById(id)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldListAll() {
            when(purchaseOrderItemRepository.findAll()).thenReturn(List.of(PurchaseOrderItemFixture.persistedEntity(purchaseOrder)));

            assertThat(purchaseOrderItemService.findAll()).hasSize(1);
        }

        @Test
        void shouldListByPurchaseOrder() {
            when(purchaseOrderItemRepository.findByPurchaseOrderId(purchaseOrder.getId()))
                    .thenReturn(List.of(PurchaseOrderItemFixture.persistedEntity(purchaseOrder)));

            assertThat(purchaseOrderItemService.findByPurchaseOrderId(purchaseOrder.getId()))
                    .extracting(PurchaseOrderItemResponseDTO::getPurchaseOrderId)
                    .containsExactly(purchaseOrder.getId());
        }
    }

    @Nested
    class ReplaceByPurchaseOrder {

        @Test
        void shouldDeleteExistingAndInsertNewItems() {
            PurchaseOrderItemInputDTO withId = PurchaseOrderItemFixture.validInput(purchaseOrder.getId(), UUID.randomUUID());
            withId.setId(UUID.randomUUID());
            ArgumentCaptor<List<PurchaseOrderItem>> captor = listCaptor();
            when(purchaseOrderItemRepository.saveAllAndFlush(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

            List<PurchaseOrderItemResponseDTO> response = purchaseOrderItemService.replaceByPurchaseOrder(purchaseOrder, List.of(withId));

            verify(purchaseOrderItemRepository).deleteByPurchaseOrder(purchaseOrder);
            assertThat(captor.getValue()).singleElement().satisfies(item -> {
                assertThat(item.getId()).isNull();
                assertThat(item.getPurchaseOrder()).isSameAs(purchaseOrder);
            });
            assertThat(response).hasSize(1);
        }

        @Test
        void shouldOnlyDeleteWhenListIsEmptyOrNull() {
            assertThat(purchaseOrderItemService.replaceByPurchaseOrder(purchaseOrder, List.of())).isEmpty();
            assertThat(purchaseOrderItemService.replaceByPurchaseOrder(purchaseOrder, null)).isEmpty();
            verify(purchaseOrderItemRepository, never()).saveAllAndFlush(anyList());
        }

        @Test
        void shouldConvertConstraintViolationIntoBusinessException() {
            when(purchaseOrderItemRepository.saveAllAndFlush(anyList())).thenThrow(PriceAcquisitionViolations.constraintViolationException());
            List<PurchaseOrderItemInputDTO> input = List.of(PurchaseOrderItemFixture.validInput(purchaseOrder.getId(), UUID.randomUUID()));

            assertThatThrownBy(() -> purchaseOrderItemService.replaceByPurchaseOrder(purchaseOrder, input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PriceAcquisitionViolations.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            doThrow(new DataIntegrityViolationException("fk")).when(purchaseOrderItemRepository).deleteByPurchaseOrder(purchaseOrder);

            assertThatThrownBy(() -> purchaseOrderItemService.replaceByPurchaseOrder(purchaseOrder, List.of()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(purchaseOrder.getId().toString())
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class SaveAllByPurchaseOrder {

        @Test
        void shouldAttachItemsToOrderAndSave() {
            when(purchaseOrderRepository.findById(purchaseOrder.getId())).thenReturn(Optional.of(purchaseOrder));
            ArgumentCaptor<List<PurchaseOrderItem>> captor = listCaptor();
            when(purchaseOrderItemRepository.saveAllAndFlush(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
            PurchaseOrderItemInputDTO input = PurchaseOrderItemFixture.validInput(null, UUID.randomUUID());
            input.setUnitCost(new BigDecimal("2.0000"));

            List<PurchaseOrderItemResponseDTO> response = purchaseOrderItemService.savePurchaseOrderItems(purchaseOrder.getId(), List.of(input));

            assertThat(captor.getValue()).singleElement().satisfies(item -> assertThat(item.getPurchaseOrder()).isSameAs(purchaseOrder));
            assertThat(response).singleElement().satisfies(dto -> {
                assertThat(dto.getPurchaseOrderId()).isEqualTo(purchaseOrder.getId());
                assertThat(dto.getTotalCost()).isEqualByComparingTo("2000");
            });
        }

        @Test
        void shouldSaveEmptyListWhenInputIsNull() {
            when(purchaseOrderRepository.findById(purchaseOrder.getId())).thenReturn(Optional.of(purchaseOrder));
            when(purchaseOrderItemRepository.saveAllAndFlush(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

            assertThat(purchaseOrderItemService.saveAllByPurchaseOrder(purchaseOrder.getId(), null)).isEmpty();
        }

        @Test
        void shouldThrowNotFoundWhenOrderDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> purchaseOrderItemService.saveAllByPurchaseOrder(id, List.of()))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(id.toString());
        }

        @Test
        void shouldConvertConstraintViolationIntoBusinessException() {
            when(purchaseOrderRepository.findById(purchaseOrder.getId())).thenReturn(Optional.of(purchaseOrder));
            when(purchaseOrderItemRepository.saveAllAndFlush(anyList())).thenThrow(PriceAcquisitionViolations.constraintViolationException());

            assertThatThrownBy(() -> purchaseOrderItemService.saveAllByPurchaseOrder(purchaseOrder.getId(), List.of()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PriceAcquisitionViolations.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            when(purchaseOrderRepository.findById(purchaseOrder.getId())).thenReturn(Optional.of(purchaseOrder));
            when(purchaseOrderItemRepository.saveAllAndFlush(anyList())).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> purchaseOrderItemService.saveAllByPurchaseOrder(purchaseOrder.getId(), List.of()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(purchaseOrder.getId().toString())
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }
}
