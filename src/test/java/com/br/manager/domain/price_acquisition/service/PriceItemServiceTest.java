package com.br.manager.domain.price_acquisition.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.price_acquisition.dto.PriceItemInputDTO;
import com.br.manager.domain.price_acquisition.dto.PriceItemResponseDTO;
import com.br.manager.domain.price_acquisition.entity.PriceItem;
import com.br.manager.domain.price_acquisition.entity.PriceTable;
import com.br.manager.domain.price_acquisition.fixture.PriceAcquisitionReferenceFixture;
import com.br.manager.domain.price_acquisition.fixture.PriceItemFixture;
import com.br.manager.domain.price_acquisition.fixture.PriceTableFixture;
import com.br.manager.domain.price_acquisition.mapper.PriceItemMapper;
import com.br.manager.domain.price_acquisition.repository.PriceItemRepository;
import com.br.manager.domain.price_acquisition.repository.PriceTableRepository;
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
class PriceItemServiceTest {

    @Mock
    private PriceItemRepository priceItemRepository;

    @Mock
    private PriceTableRepository priceTableRepository;

    @Spy
    private PriceItemMapper priceItemMapper = new PriceItemMapper();

    @InjectMocks
    private PriceItemService priceItemService;

    private final PriceTable priceTable = PriceTableFixture.persistedEntity(PriceAcquisitionReferenceFixture.activeUser());

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<List<PriceItem>> listCaptor() {
        return ArgumentCaptor.forClass(List.class);
    }

    private void stubSaveAllReturningArgument() {
        when(priceItemRepository.saveAllAndFlush(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Nested
    class ReplaceByPriceTable {

        @Test
        void shouldDeleteExistingItemsAndInsertNewOnes() {
            PriceItemInputDTO withId = PriceItemFixture.validInput(priceTable.getId(), UUID.randomUUID());
            withId.setId(UUID.randomUUID());
            PriceItemInputDTO withoutActive = PriceItemFixture.validInput(priceTable.getId(), UUID.randomUUID());
            withoutActive.setActive(null);
            PriceItemInputDTO inactive = PriceItemFixture.validInput(priceTable.getId(), UUID.randomUUID());
            inactive.setActive(false);
            ArgumentCaptor<List<PriceItem>> captor = listCaptor();
            when(priceItemRepository.saveAllAndFlush(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

            List<PriceItemResponseDTO> response = priceItemService.replaceByPriceTable(priceTable, List.of(withId, withoutActive, inactive));

            verify(priceItemRepository).deleteByPriceTable(priceTable);
            List<PriceItem> saved = captor.getValue();
            assertThat(saved).hasSize(3).allSatisfy(item -> {
                assertThat(item.getId()).isNull();
                assertThat(item.getPriceTable()).isSameAs(priceTable);
            });
            assertThat(saved).extracting(PriceItem::getActive).containsExactly(true, true, false);
            assertThat(response).hasSize(3);
            assertThat(response.get(0).getPriceTableId()).isEqualTo(priceTable.getId());
            assertThat(response.get(0).getUnitPrice()).isEqualByComparingTo(PriceItemFixture.UNIT_PRICE);
        }

        @Test
        void shouldOnlyDeleteWhenNewListIsEmpty() {
            assertThat(priceItemService.replaceByPriceTable(priceTable, List.of())).isEmpty();

            verify(priceItemRepository).deleteByPriceTable(priceTable);
            verify(priceItemRepository, never()).saveAllAndFlush(anyList());
        }

        @Test
        void shouldOnlyDeleteWhenNewListIsNull() {
            assertThat(priceItemService.replaceByPriceTable(priceTable, null)).isEmpty();

            verify(priceItemRepository).deleteByPriceTable(priceTable);
        }

        @Test
        void shouldConvertConstraintViolationIntoBusinessException() {
            when(priceItemRepository.saveAllAndFlush(anyList())).thenThrow(PriceAcquisitionViolations.constraintViolationException());
            List<PriceItemInputDTO> input = List.of(PriceItemFixture.validInput(priceTable.getId(), UUID.randomUUID()));

            assertThatThrownBy(() -> priceItemService.replaceByPriceTable(priceTable, input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PriceAcquisitionViolations.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            doThrow(new DataIntegrityViolationException("fk")).when(priceItemRepository).deleteByPriceTable(priceTable);

            assertThatThrownBy(() -> priceItemService.replaceByPriceTable(priceTable, List.of()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(priceTable.getName())
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class SaveAllByPriceTable {

        @Test
        void shouldAttachItemsToTableAndSave() {
            when(priceTableRepository.findById(priceTable.getId())).thenReturn(Optional.of(priceTable));
            ArgumentCaptor<List<PriceItem>> captor = listCaptor();
            when(priceItemRepository.saveAllAndFlush(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
            List<PriceItemInputDTO> input = List.of(
                    PriceItemFixture.validInput(null, UUID.randomUUID()),
                    PriceItemFixture.validInput(null, UUID.randomUUID()));

            List<PriceItemResponseDTO> response = priceItemService.saveAllByPriceTable(priceTable.getId(), input);

            assertThat(captor.getValue()).hasSize(2).allSatisfy(item -> assertThat(item.getPriceTable()).isSameAs(priceTable));
            assertThat(response).extracting(PriceItemResponseDTO::getPriceTableId).containsOnly(priceTable.getId());
        }

        @Test
        void shouldSaveEmptyListWhenInputIsNull() {
            when(priceTableRepository.findById(priceTable.getId())).thenReturn(Optional.of(priceTable));
            stubSaveAllReturningArgument();

            assertThat(priceItemService.savePriceItems(priceTable.getId(), null)).isEmpty();
            verify(priceItemRepository).saveAllAndFlush(List.of());
        }

        @Test
        void shouldThrowNotFoundWhenTableDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(priceTableRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> priceItemService.saveAllByPriceTable(id, List.of()))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(id.toString());
            verify(priceItemRepository, never()).saveAllAndFlush(anyList());
        }

        @Test
        void shouldConvertConstraintViolationIntoBusinessException() {
            when(priceTableRepository.findById(priceTable.getId())).thenReturn(Optional.of(priceTable));
            when(priceItemRepository.saveAllAndFlush(anyList())).thenThrow(PriceAcquisitionViolations.constraintViolationException());

            assertThatThrownBy(() -> priceItemService.saveAllByPriceTable(priceTable.getId(), List.of()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PriceAcquisitionViolations.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            when(priceTableRepository.findById(priceTable.getId())).thenReturn(Optional.of(priceTable));
            when(priceItemRepository.saveAllAndFlush(anyList())).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> priceItemService.saveAllByPriceTable(priceTable.getId(), List.of()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(priceTable.getId().toString())
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class SavePriceItem {

        @Test
        void shouldSaveSingleItemLinkedToTable() {
            PriceItemInputDTO input = PriceItemFixture.validInput(priceTable.getId(), UUID.randomUUID());
            when(priceTableRepository.findById(priceTable.getId())).thenReturn(Optional.of(priceTable));
            when(priceItemRepository.saveAndFlush(any(PriceItem.class))).thenAnswer(invocation -> {
                PriceItem item = invocation.getArgument(0);
                item.setId(UUID.randomUUID());
                return item;
            });

            PriceItemResponseDTO response = priceItemService.savePriceItem(input);

            assertThat(response.getId()).isNotNull();
            assertThat(response.getPriceTableId()).isEqualTo(priceTable.getId());
            assertThat(response.getProductId()).isEqualTo(input.getProductId());
            assertThat(response.getUnitPrice()).isEqualByComparingTo(PriceItemFixture.UNIT_PRICE);
        }

        @Test
        void shouldThrowNotFoundWhenTableDoesNotExist() {
            PriceItemInputDTO input = PriceItemFixture.validInput(UUID.randomUUID(), UUID.randomUUID());
            when(priceTableRepository.findById(input.getPriceTableId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> priceItemService.savePriceItem(input))
                    .isInstanceOf(NotFoundBusinessException.class);
            verify(priceItemRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoBusinessException() {
            PriceItemInputDTO input = PriceItemFixture.validInput(priceTable.getId(), UUID.randomUUID());
            when(priceTableRepository.findById(priceTable.getId())).thenReturn(Optional.of(priceTable));
            when(priceItemRepository.saveAndFlush(any(PriceItem.class))).thenThrow(PriceAcquisitionViolations.constraintViolationException());

            assertThatThrownBy(() -> priceItemService.savePriceItem(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PriceAcquisitionViolations.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            PriceItemInputDTO input = PriceItemFixture.validInput(priceTable.getId(), UUID.randomUUID());
            when(priceTableRepository.findById(priceTable.getId())).thenReturn(Optional.of(priceTable));
            when(priceItemRepository.saveAndFlush(any(PriceItem.class))).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> priceItemService.savePriceItem(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(priceTable.getId().toString())
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateActiveItem() {
            PriceItem existing = PriceItemFixture.persistedEntity(priceTable);
            PriceItemInputDTO input = new PriceItemInputDTO();
            input.setId(existing.getId());
            input.setUnitPrice(PriceItemFixture.UPDATED_UNIT_PRICE);
            when(priceItemRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(priceItemRepository.saveAndFlush(existing)).thenReturn(existing);

            PriceItemResponseDTO response = priceItemService.update(input);

            assertThat(response.getUnitPrice()).isEqualByComparingTo(PriceItemFixture.UPDATED_UNIT_PRICE);
            assertThat(response.getPriceTableId()).isEqualTo(priceTable.getId());
            verify(priceTableRepository, never()).findById(any());
        }

        @Test
        void shouldMoveItemToAnotherTableWhenInformed() {
            PriceItem existing = PriceItemFixture.persistedEntity(priceTable);
            PriceTable otherTable = PriceTableFixture.persistedEntity(PriceAcquisitionReferenceFixture.activeUser());
            PriceItemInputDTO input = new PriceItemInputDTO();
            input.setId(existing.getId());
            input.setPriceTableId(otherTable.getId());
            when(priceItemRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(priceTableRepository.findById(otherTable.getId())).thenReturn(Optional.of(otherTable));
            when(priceItemRepository.saveAndFlush(existing)).thenReturn(existing);

            assertThat(priceItemService.update(input).getPriceTableId()).isEqualTo(otherTable.getId());
        }

        @Test
        void shouldThrowNotFoundWhenItemIsMissingOrInactive() {
            PriceItemInputDTO input = new PriceItemInputDTO();
            input.setId(UUID.randomUUID());
            when(priceItemRepository.findByIdAndActiveTrue(input.getId())).thenReturn(null);

            assertThatThrownBy(() -> priceItemService.update(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(input.getId().toString());
        }

        @Test
        void shouldThrowNotFoundWhenNewTableDoesNotExist() {
            PriceItem existing = PriceItemFixture.persistedEntity(priceTable);
            PriceItemInputDTO input = new PriceItemInputDTO();
            input.setId(existing.getId());
            input.setPriceTableId(UUID.randomUUID());
            when(priceItemRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(priceTableRepository.findById(input.getPriceTableId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> priceItemService.update(input))
                    .isInstanceOf(NotFoundBusinessException.class);
            verify(priceItemRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoBusinessException() {
            PriceItem existing = PriceItemFixture.persistedEntity(priceTable);
            PriceItemInputDTO input = new PriceItemInputDTO();
            input.setId(existing.getId());
            when(priceItemRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(priceItemRepository.saveAndFlush(existing)).thenThrow(PriceAcquisitionViolations.constraintViolationException());

            assertThatThrownBy(() -> priceItemService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PriceAcquisitionViolations.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            PriceItem existing = PriceItemFixture.persistedEntity(priceTable);
            PriceItemInputDTO input = new PriceItemInputDTO();
            input.setId(existing.getId());
            when(priceItemRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(priceItemRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("db"));

            assertThatThrownBy(() -> priceItemService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao atualizar item de preço")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldDeleteExistingItem() {
            PriceItem existing = PriceItemFixture.persistedEntity(priceTable);
            when(priceItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            priceItemService.delete(existing.getId());

            verify(priceItemRepository).delete(existing);
        }

        @Test
        void shouldThrowNotFoundWhenItemDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(priceItemRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> priceItemService.delete(id)).isInstanceOf(NotFoundBusinessException.class);
            verify(priceItemRepository, never()).delete(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoBusinessException() {
            PriceItem existing = PriceItemFixture.persistedEntity(priceTable);
            when(priceItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            doThrow(PriceAcquisitionViolations.constraintViolationException()).when(priceItemRepository).delete(existing);

            assertThatThrownBy(() -> priceItemService.delete(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PriceAcquisitionViolations.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            PriceItem existing = PriceItemFixture.persistedEntity(priceTable);
            when(priceItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            doThrow(new DataIntegrityViolationException("fk")).when(priceItemRepository).delete(existing);

            assertThatThrownBy(() -> priceItemService.delete(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao excluir item de preço")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListActiveItemsOfTable() {
            when(priceItemRepository.findByPriceTableIdAndActiveTrue(priceTable.getId()))
                    .thenReturn(List.of(PriceItemFixture.persistedEntity(priceTable)));

            assertThat(priceItemService.findByPriceTable(priceTable.getId())).hasSize(1);
            assertThat(priceItemService.findAllByPriceTable(priceTable.getId())).hasSize(1);
        }

        @Test
        void shouldListAllActiveItems() {
            when(priceItemRepository.findAllByActiveTrue()).thenReturn(List.of(PriceItemFixture.persistedEntity(priceTable)));

            assertThat(priceItemService.findAll()).hasSize(1);
        }

        @Test
        void shouldFindActiveItemById() {
            PriceItem existing = PriceItemFixture.persistedEntity(priceTable);
            when(priceItemRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);

            assertThat(priceItemService.find(existing.getId()).getId()).isEqualTo(existing.getId());
            assertThat(priceItemService.getPriceItemEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenActiveItemDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> priceItemService.find(id)).isInstanceOf(NotFoundBusinessException.class);
            assertThatThrownBy(() -> priceItemService.getPriceItemEntityById(id)).isInstanceOf(NotFoundBusinessException.class);
        }
    }
}
