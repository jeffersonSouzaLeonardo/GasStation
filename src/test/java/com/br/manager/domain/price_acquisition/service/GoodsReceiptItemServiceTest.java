package com.br.manager.domain.price_acquisition.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.price_acquisition.dto.GoodsReceiptItemInputDTO;
import com.br.manager.domain.price_acquisition.dto.GoodsReceiptItemResponseDTO;
import com.br.manager.domain.price_acquisition.entity.GoodsReceipt;
import com.br.manager.domain.price_acquisition.entity.GoodsReceiptItem;
import com.br.manager.domain.price_acquisition.fixture.GoodsReceiptFixture;
import com.br.manager.domain.price_acquisition.fixture.GoodsReceiptItemFixture;
import com.br.manager.domain.price_acquisition.mapper.GoodsReceiptItemMapper;
import com.br.manager.domain.price_acquisition.repository.GoodsReceiptItemRepository;
import com.br.manager.domain.price_acquisition.repository.GoodsReceiptRepository;
import com.br.manager.domain.price_acquisition.support.PriceAcquisitionViolations;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoodsReceiptItemServiceTest {

    @Mock
    private GoodsReceiptItemRepository goodsReceiptItemRepository;

    @Mock
    private GoodsReceiptRepository goodsReceiptRepository;

    @Spy
    private GoodsReceiptItemMapper goodsReceiptItemMapper = new GoodsReceiptItemMapper();

    @InjectMocks
    private GoodsReceiptItemService goodsReceiptItemService;

    private final GoodsReceipt goodsReceipt = GoodsReceiptFixture.persistedEntity();

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<List<GoodsReceiptItem>> listCaptor() {
        return ArgumentCaptor.forClass(List.class);
    }

    private void stubReceipt() {
        when(goodsReceiptRepository.findById(goodsReceipt.getId())).thenReturn(Optional.of(goodsReceipt));
    }

    private List<GoodsReceiptItemInputDTO> twoItems() {
        return List.of(
                GoodsReceiptItemFixture.validInput(null, UUID.randomUUID(), UUID.randomUUID()),
                GoodsReceiptItemFixture.validInput(null, UUID.randomUUID(), null));
    }

    @Nested
    class SaveGoodsReceiptItem {

        @Test
        void shouldSaveItemLinkedToReceipt() {
            GoodsReceiptItemInputDTO input = GoodsReceiptItemFixture.validInput(goodsReceipt.getId(), UUID.randomUUID(), UUID.randomUUID());
            stubReceipt();
            when(goodsReceiptItemRepository.saveAndFlush(any(GoodsReceiptItem.class))).thenAnswer(invocation -> {
                GoodsReceiptItem item = invocation.getArgument(0);
                item.setId(UUID.randomUUID());
                return item;
            });

            GoodsReceiptItemResponseDTO response = goodsReceiptItemService.saveGoodsReceiptItem(input);

            assertThat(response.getId()).isNotNull();
            assertThat(response.getGoodsReceiptId()).isEqualTo(goodsReceipt.getId());
            assertThat(response.getProductId()).isEqualTo(input.getProductId());
            assertThat(response.getTankId()).isEqualTo(input.getTankId());
            assertThat(response.getQuantity()).isEqualByComparingTo(GoodsReceiptItemFixture.QUANTITY);
            assertThat(response.getUnitCost()).isEqualByComparingTo(GoodsReceiptItemFixture.UNIT_COST);
        }

        @Test
        void shouldThrowNotFoundWhenReceiptDoesNotExist() {
            GoodsReceiptItemInputDTO input = GoodsReceiptItemFixture.validInput(UUID.randomUUID(), UUID.randomUUID(), null);

            assertThatThrownBy(() -> goodsReceiptItemService.saveGoodsReceiptItem(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(input.getGoodsReceiptId().toString());
            verify(goodsReceiptItemRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoBusinessException() {
            GoodsReceiptItemInputDTO input = GoodsReceiptItemFixture.validInput(goodsReceipt.getId(), UUID.randomUUID(), null);
            stubReceipt();
            when(goodsReceiptItemRepository.saveAndFlush(any(GoodsReceiptItem.class)))
                    .thenThrow(PriceAcquisitionViolations.constraintViolationException());

            assertThatThrownBy(() -> goodsReceiptItemService.saveGoodsReceiptItem(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PriceAcquisitionViolations.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            GoodsReceiptItemInputDTO input = GoodsReceiptItemFixture.validInput(goodsReceipt.getId(), UUID.randomUUID(), null);
            stubReceipt();
            when(goodsReceiptItemRepository.saveAndFlush(any(GoodsReceiptItem.class))).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> goodsReceiptItemService.saveGoodsReceiptItem(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao salvar item do recebimento de mercadorias")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class SaveGoodsReceiptItems {

        @Test
        void shouldAttachAllItemsToReceipt() {
            stubReceipt();
            ArgumentCaptor<List<GoodsReceiptItem>> captor = listCaptor();
            when(goodsReceiptItemRepository.saveAllAndFlush(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

            List<GoodsReceiptItemResponseDTO> response = goodsReceiptItemService.saveGoodsReceiptItems(goodsReceipt.getId(), twoItems());

            assertThat(captor.getValue()).hasSize(2).allSatisfy(item -> assertThat(item.getGoodsReceipt()).isSameAs(goodsReceipt));
            assertThat(response).extracting(GoodsReceiptItemResponseDTO::getGoodsReceiptId).containsOnly(goodsReceipt.getId());
        }

        @Test
        void shouldReturnEmptyListWithoutSavingWhenInputIsEmpty() {
            stubReceipt();

            assertThat(goodsReceiptItemService.saveGoodsReceiptItems(goodsReceipt.getId(), List.of())).isEmpty();
            assertThat(goodsReceiptItemService.saveGoodsReceiptItems(goodsReceipt.getId(), null)).isEmpty();
            verify(goodsReceiptItemRepository, never()).saveAllAndFlush(anyList());
        }

        @Test
        void shouldThrowNotFoundWhenReceiptDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> goodsReceiptItemService.saveGoodsReceiptItems(id, twoItems()))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(id.toString());
        }

        @Test
        void shouldConvertConstraintViolationIntoBusinessException() {
            stubReceipt();
            when(goodsReceiptItemRepository.saveAllAndFlush(anyList())).thenThrow(PriceAcquisitionViolations.constraintViolationException());

            assertThatThrownBy(() -> goodsReceiptItemService.saveGoodsReceiptItems(goodsReceipt.getId(), twoItems()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PriceAcquisitionViolations.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            stubReceipt();
            when(goodsReceiptItemRepository.saveAllAndFlush(anyList())).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> goodsReceiptItemService.saveGoodsReceiptItems(goodsReceipt.getId(), twoItems()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao salvar itens do recebimento de mercadorias " + goodsReceipt.getId())
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class ReplaceByGoodsReceipt {

        @Test
        void shouldDeleteCurrentItemsAndSaveNewOnes() {
            List<GoodsReceiptItem> current = List.of(GoodsReceiptItemFixture.persistedEntity(goodsReceipt));
            stubReceipt();
            when(goodsReceiptItemRepository.findByGoodsReceiptId(goodsReceipt.getId())).thenReturn(current);
            ArgumentCaptor<List<GoodsReceiptItem>> captor = listCaptor();
            when(goodsReceiptItemRepository.saveAllAndFlush(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

            List<GoodsReceiptItemResponseDTO> response = goodsReceiptItemService.replaceByGoodsReceipt(goodsReceipt.getId(), twoItems());

            verify(goodsReceiptItemRepository).deleteAll(current);
            assertThat(captor.getValue()).hasSize(2).allSatisfy(item -> assertThat(item.getGoodsReceipt()).isSameAs(goodsReceipt));
            assertThat(response).hasSize(2);
        }

        @Test
        void shouldOnlyDeleteWhenNewListIsEmpty() {
            List<GoodsReceiptItem> current = List.of(GoodsReceiptItemFixture.persistedEntity(goodsReceipt));
            stubReceipt();
            when(goodsReceiptItemRepository.findByGoodsReceiptId(goodsReceipt.getId())).thenReturn(current);

            assertThat(goodsReceiptItemService.replaceByGoodsReceipt(goodsReceipt.getId(), null)).isEmpty();
            verify(goodsReceiptItemRepository).deleteAll(current);
            verify(goodsReceiptItemRepository, never()).saveAllAndFlush(anyList());
        }

        @Test
        void shouldThrowNotFoundWhenReceiptDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> goodsReceiptItemService.replaceByGoodsReceipt(id, twoItems()))
                    .isInstanceOf(NotFoundBusinessException.class);
            verify(goodsReceiptItemRepository, never()).deleteAll(anyList());
        }

        @Test
        void shouldConvertConstraintViolationIntoBusinessException() {
            stubReceipt();
            when(goodsReceiptItemRepository.saveAllAndFlush(anyList())).thenThrow(PriceAcquisitionViolations.constraintViolationException());

            assertThatThrownBy(() -> goodsReceiptItemService.replaceByGoodsReceipt(goodsReceipt.getId(), twoItems()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PriceAcquisitionViolations.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            stubReceipt();
            doThrow(new DataIntegrityViolationException("fk")).when(goodsReceiptItemRepository).deleteAll(anyList());

            assertThatThrownBy(() -> goodsReceiptItemService.replaceByGoodsReceipt(goodsReceipt.getId(), twoItems()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao substituir itens do recebimento de mercadorias " + goodsReceipt.getId())
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateInformedFields() {
            GoodsReceiptItem existing = GoodsReceiptItemFixture.persistedEntity(goodsReceipt);
            GoodsReceiptItemInputDTO input = new GoodsReceiptItemInputDTO();
            input.setId(existing.getId());
            input.setQuantity(GoodsReceiptItemFixture.UPDATED_QUANTITY);
            when(goodsReceiptItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(goodsReceiptItemRepository.saveAndFlush(existing)).thenReturn(existing);

            GoodsReceiptItemResponseDTO response = goodsReceiptItemService.update(input);

            assertThat(response.getQuantity()).isEqualByComparingTo(GoodsReceiptItemFixture.UPDATED_QUANTITY);
            assertThat(response.getUnitCost()).isEqualByComparingTo(GoodsReceiptItemFixture.UNIT_COST);
            verify(goodsReceiptRepository, never()).findById(any());
        }

        @Test
        void shouldMoveItemToAnotherReceipt() {
            GoodsReceiptItem existing = GoodsReceiptItemFixture.persistedEntity(GoodsReceiptFixture.persistedEntity());
            GoodsReceiptItemInputDTO input = new GoodsReceiptItemInputDTO();
            input.setId(existing.getId());
            input.setGoodsReceiptId(goodsReceipt.getId());
            when(goodsReceiptItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            stubReceipt();
            when(goodsReceiptItemRepository.saveAndFlush(existing)).thenReturn(existing);

            assertThat(goodsReceiptItemService.update(input).getGoodsReceiptId()).isEqualTo(goodsReceipt.getId());
        }

        @Test
        void shouldThrowNotFoundWhenItemDoesNotExist() {
            GoodsReceiptItemInputDTO input = new GoodsReceiptItemInputDTO();
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> goodsReceiptItemService.update(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(input.getId().toString());
        }

        @Test
        void shouldThrowNotFoundWhenNewReceiptDoesNotExist() {
            GoodsReceiptItem existing = GoodsReceiptItemFixture.persistedEntity(goodsReceipt);
            GoodsReceiptItemInputDTO input = new GoodsReceiptItemInputDTO();
            input.setId(existing.getId());
            input.setGoodsReceiptId(UUID.randomUUID());
            when(goodsReceiptItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            assertThatThrownBy(() -> goodsReceiptItemService.update(input)).isInstanceOf(NotFoundBusinessException.class);
            verify(goodsReceiptItemRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoBusinessException() {
            GoodsReceiptItem existing = GoodsReceiptItemFixture.persistedEntity(goodsReceipt);
            GoodsReceiptItemInputDTO input = new GoodsReceiptItemInputDTO();
            input.setId(existing.getId());
            when(goodsReceiptItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(goodsReceiptItemRepository.saveAndFlush(existing)).thenThrow(PriceAcquisitionViolations.constraintViolationException());

            assertThatThrownBy(() -> goodsReceiptItemService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PriceAcquisitionViolations.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            GoodsReceiptItem existing = GoodsReceiptItemFixture.persistedEntity(goodsReceipt);
            GoodsReceiptItemInputDTO input = new GoodsReceiptItemInputDTO();
            input.setId(existing.getId());
            when(goodsReceiptItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(goodsReceiptItemRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> goodsReceiptItemService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao atualizar item do recebimento de mercadorias")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldDeleteExistingItem() {
            GoodsReceiptItem existing = GoodsReceiptItemFixture.persistedEntity(goodsReceipt);
            when(goodsReceiptItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            goodsReceiptItemService.delete(existing.getId());

            verify(goodsReceiptItemRepository).delete(existing);
        }

        @Test
        void shouldThrowNotFoundWhenItemDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> goodsReceiptItemService.delete(id)).isInstanceOf(NotFoundBusinessException.class);
            verify(goodsReceiptItemRepository, never()).delete(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoBusinessException() {
            GoodsReceiptItem existing = GoodsReceiptItemFixture.persistedEntity(goodsReceipt);
            when(goodsReceiptItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            doThrow(PriceAcquisitionViolations.constraintViolationException()).when(goodsReceiptItemRepository).delete(existing);

            assertThatThrownBy(() -> goodsReceiptItemService.delete(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PriceAcquisitionViolations.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            GoodsReceiptItem existing = GoodsReceiptItemFixture.persistedEntity(goodsReceipt);
            when(goodsReceiptItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            doThrow(new DataIntegrityViolationException("fk")).when(goodsReceiptItemRepository).delete(existing);

            assertThatThrownBy(() -> goodsReceiptItemService.delete(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao excluir item do recebimento de mercadorias")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListByReceipt() {
            when(goodsReceiptItemRepository.findByGoodsReceiptId(goodsReceipt.getId()))
                    .thenReturn(List.of(GoodsReceiptItemFixture.persistedEntity(goodsReceipt)));

            assertThat(goodsReceiptItemService.findByGoodsReceipt(goodsReceipt.getId())).hasSize(1);
        }

        @Test
        void shouldListAll() {
            when(goodsReceiptItemRepository.findAll()).thenReturn(List.of(GoodsReceiptItemFixture.persistedEntity(goodsReceipt)));

            assertThat(goodsReceiptItemService.findAll()).hasSize(1);
        }

        @Test
        void shouldFindById() {
            GoodsReceiptItem existing = GoodsReceiptItemFixture.persistedEntity(goodsReceipt);
            when(goodsReceiptItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            assertThat(goodsReceiptItemService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> goodsReceiptItemService.find(id))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(id.toString());
        }
    }
}
