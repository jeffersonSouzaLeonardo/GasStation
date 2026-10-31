package com.br.manager.domain.price_acquisition.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.organization.repository.UserRepository;
import com.br.manager.domain.price_acquisition.dto.PriceItemInputDTO;
import com.br.manager.domain.price_acquisition.dto.PriceItemResponseDTO;
import com.br.manager.domain.price_acquisition.dto.PriceTableInputDTO;
import com.br.manager.domain.price_acquisition.dto.PriceTableResponseDTO;
import com.br.manager.domain.price_acquisition.entity.PriceTable;
import com.br.manager.domain.price_acquisition.fixture.PriceAcquisitionReferenceFixture;
import com.br.manager.domain.price_acquisition.fixture.PriceItemFixture;
import com.br.manager.domain.price_acquisition.fixture.PriceTableFixture;
import com.br.manager.domain.price_acquisition.mapper.PriceItemMapper;
import com.br.manager.domain.price_acquisition.mapper.PriceTableMapper;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceTableServiceTest {

    @Mock
    private PriceTableRepository priceTableRepository;

    @Mock
    private PriceItemService priceItemService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private PriceTableMapper priceTableMapper = new PriceTableMapper();

    @Spy
    private PriceItemMapper priceItemMapper = new PriceItemMapper();

    @InjectMocks
    private PriceTableService priceTableService;

    private final User creator = PriceAcquisitionReferenceFixture.activeUser();

    private PriceTableInputDTO validInput() {
        return PriceTableFixture.validInput(creator.getId(), List.of(PriceItemFixture.validInput(null, UUID.randomUUID())));
    }

    @Nested
    class Create {

        @Test
        void shouldRejectTableWithoutItems() {
            PriceTableInputDTO input = PriceTableFixture.validInput(creator.getId(), List.of());

            assertThatThrownBy(() -> priceTableService.create(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("pelo menos um item");
            verify(priceTableRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldRejectTableWithNullItems() {
            PriceTableInputDTO input = PriceTableFixture.validInput(creator.getId(), null);

            assertThatThrownBy(() -> priceTableService.create(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("pelo menos um item");
        }

        @Test
        void shouldPersistTableWithCreatorAndSaveItems() {
            PriceTableInputDTO input = validInput();
            input.setId(UUID.randomUUID());
            PriceItemResponseDTO itemResponse = new PriceItemResponseDTO();
            when(userRepository.findById(creator.getId())).thenReturn(Optional.of(creator));
            ArgumentCaptor<PriceTable> captor = ArgumentCaptor.forClass(PriceTable.class);
            when(priceTableRepository.saveAndFlush(captor.capture())).thenAnswer(invocation -> {
                PriceTable priceTable = invocation.getArgument(0);
                assertThat(priceTable.getId()).isNull();
                priceTable.setId(UUID.randomUUID());
                return priceTable;
            });
            when(priceItemService.findByPriceTable(any())).thenReturn(List.of(itemResponse));

            PriceTableResponseDTO response = priceTableService.create(input);

            assertThat(captor.getValue().getCreatedBy()).isSameAs(creator);
            assertThat(response.getId()).isEqualTo(captor.getValue().getId());
            assertThat(response.getName()).isEqualTo(PriceTableFixture.NAME);
            assertThat(response.getCreatedBy()).isEqualTo(creator.getId());
            assertThat(response.getPriceItems()).containsExactly(itemResponse);
            verify(priceItemService).savePriceItems(response.getId(), input.getPriceItems());
        }

        @Test
        void shouldThrowNotFoundWhenCreatorDoesNotExist() {
            PriceTableInputDTO input = validInput();
            when(userRepository.findById(creator.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> priceTableService.create(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(creator.getId().toString());
            verify(priceTableRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldThrowNotFoundWhenCreatorIsInactive() {
            User inactive = PriceAcquisitionReferenceFixture.inactiveUser();
            PriceTableInputDTO input = PriceTableFixture.validInput(inactive.getId(), validInput().getPriceItems());
            when(userRepository.findById(inactive.getId())).thenReturn(Optional.of(inactive));

            assertThatThrownBy(() -> priceTableService.create(input))
                    .isInstanceOf(NotFoundBusinessException.class);
            verify(priceTableRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoBusinessException() {
            when(userRepository.findById(creator.getId())).thenReturn(Optional.of(creator));
            when(priceTableRepository.saveAndFlush(any(PriceTable.class)))
                    .thenThrow(PriceAcquisitionViolations.constraintViolationException());

            assertThatThrownBy(() -> priceTableService.create(validInput()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PriceAcquisitionViolations.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            when(userRepository.findById(creator.getId())).thenReturn(Optional.of(creator));
            when(priceTableRepository.saveAndFlush(any(PriceTable.class)))
                    .thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> priceTableService.create(validInput()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PriceTableFixture.NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateFieldsAndReplaceItemsWhenItemsAreInformed() {
            PriceTable existing = PriceTableFixture.persistedEntity(creator);
            PriceTableInputDTO input = new PriceTableInputDTO();
            input.setId(existing.getId());
            input.setName(PriceTableFixture.OTHER_NAME);
            List<PriceItemInputDTO> items = List.of(PriceItemFixture.validInput(existing.getId(), UUID.randomUUID()));
            input.setPriceItems(items);
            when(priceTableRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(priceTableRepository.saveAndFlush(existing)).thenReturn(existing);

            PriceTableResponseDTO response = priceTableService.update(input);

            assertThat(response.getName()).isEqualTo(PriceTableFixture.OTHER_NAME);
            assertThat(response.getCreatedBy()).isEqualTo(creator.getId());
            verify(priceItemService).replaceByPriceTable(existing, items);
            verify(priceItemService).findByPriceTable(existing.getId());
        }

        @Test
        void shouldKeepItemsWhenItemsAreNotInformed() {
            PriceTable existing = PriceTableFixture.persistedEntity(creator);
            PriceTableInputDTO input = new PriceTableInputDTO();
            input.setId(existing.getId());
            when(priceTableRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(priceTableRepository.saveAndFlush(existing)).thenReturn(existing);

            priceTableService.update(input);

            verify(priceItemService, never()).replaceByPriceTable(any(), any());
        }

        @Test
        void shouldChangeCreatorWhenInformed() {
            PriceTable existing = PriceTableFixture.persistedEntity(creator);
            User newCreator = PriceAcquisitionReferenceFixture.activeUser();
            PriceTableInputDTO input = new PriceTableInputDTO();
            input.setId(existing.getId());
            input.setCreatedBy(newCreator.getId());
            when(priceTableRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(userRepository.findById(newCreator.getId())).thenReturn(Optional.of(newCreator));
            when(priceTableRepository.saveAndFlush(existing)).thenReturn(existing);

            PriceTableResponseDTO response = priceTableService.update(input);

            assertThat(response.getCreatedBy()).isEqualTo(newCreator.getId());
        }

        @Test
        void shouldThrowNotFoundWhenTableDoesNotExist() {
            PriceTableInputDTO input = validInput();
            input.setId(UUID.randomUUID());
            when(priceTableRepository.findById(input.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> priceTableService.update(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(input.getId().toString());
            verify(priceTableRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldThrowNotFoundWhenNewCreatorDoesNotExist() {
            PriceTable existing = PriceTableFixture.persistedEntity(creator);
            PriceTableInputDTO input = new PriceTableInputDTO();
            input.setId(existing.getId());
            input.setCreatedBy(UUID.randomUUID());
            when(priceTableRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(userRepository.findById(input.getCreatedBy())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> priceTableService.update(input))
                    .isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldThrowNotFoundWhenNewCreatorIsInactive() {
            PriceTable existing = PriceTableFixture.persistedEntity(creator);
            User inactive = PriceAcquisitionReferenceFixture.inactiveUser();
            PriceTableInputDTO input = new PriceTableInputDTO();
            input.setId(existing.getId());
            input.setCreatedBy(inactive.getId());
            when(priceTableRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(userRepository.findById(inactive.getId())).thenReturn(Optional.of(inactive));

            assertThatThrownBy(() -> priceTableService.update(input))
                    .isInstanceOf(NotFoundBusinessException.class);
            verify(priceTableRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoBusinessException() {
            PriceTable existing = PriceTableFixture.persistedEntity(creator);
            PriceTableInputDTO input = new PriceTableInputDTO();
            input.setId(existing.getId());
            when(priceTableRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(priceTableRepository.saveAndFlush(existing)).thenThrow(PriceAcquisitionViolations.constraintViolationException());

            assertThatThrownBy(() -> priceTableService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PriceAcquisitionViolations.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            PriceTable existing = PriceTableFixture.persistedEntity(creator);
            PriceTableInputDTO input = new PriceTableInputDTO();
            input.setId(existing.getId());
            input.setName(PriceTableFixture.OTHER_NAME);
            when(priceTableRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(priceTableRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("db"));

            assertThatThrownBy(() -> priceTableService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PriceTableFixture.OTHER_NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListAllTables() {
            when(priceTableRepository.findAll()).thenReturn(List.of(PriceTableFixture.persistedEntity(creator)));

            assertThat(priceTableService.findAll()).hasSize(1);
        }

        @Test
        void shouldSearchByDescription() {
            when(priceTableRepository.findByNameContainingIgnoreCase("janeiro"))
                    .thenReturn(List.of(PriceTableFixture.persistedEntity(creator)));

            assertThat(priceTableService.findByDescription("janeiro")).hasSize(1);
        }

        @Test
        void shouldFallbackToFindAllWhenDescriptionIsBlank() {
            when(priceTableRepository.findAll()).thenReturn(List.of());

            assertThat(priceTableService.findByDescription(" ")).isEmpty();
            verify(priceTableRepository, never()).findByNameContainingIgnoreCase(any());
        }

        @Test
        void shouldSearchByNameUsingDescriptionSearch() {
            when(priceTableRepository.findByNameContainingIgnoreCase("tabela")).thenReturn(List.of());

            assertThat(priceTableService.findByName("tabela")).isEmpty();
            verify(priceTableRepository).findByNameContainingIgnoreCase("tabela");
        }

        @Test
        void shouldFindById() {
            PriceTable existing = PriceTableFixture.persistedEntity(creator);
            when(priceTableRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            assertThat(priceTableService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldReturnEntityById() {
            PriceTable existing = PriceTableFixture.persistedEntity(creator);
            when(priceTableRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            assertThat(priceTableService.getPriceTableEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(priceTableRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> priceTableService.find(id)).isInstanceOf(NotFoundBusinessException.class);
            assertThatThrownBy(() -> priceTableService.getPriceTableEntityById(id)).isInstanceOf(NotFoundBusinessException.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldDeleteExistingTable() {
            PriceTable existing = PriceTableFixture.persistedEntity(creator);
            when(priceTableRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            priceTableService.delete(existing.getId());

            verify(priceTableRepository).delete(existing);
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownTable() {
            UUID id = UUID.randomUUID();
            when(priceTableRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> priceTableService.delete(id)).isInstanceOf(NotFoundBusinessException.class);
            verify(priceTableRepository, never()).delete(any());
        }

        @Test
        void shouldConvertConstraintViolationIntoBusinessException() {
            PriceTable existing = PriceTableFixture.persistedEntity(creator);
            when(priceTableRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            doThrow(PriceAcquisitionViolations.constraintViolationException()).when(priceTableRepository).delete(existing);

            assertThatThrownBy(() -> priceTableService.delete(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PriceAcquisitionViolations.VIOLATION_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            PriceTable existing = PriceTableFixture.persistedEntity(creator);
            when(priceTableRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            doThrow(new DataIntegrityViolationException("fk")).when(priceTableRepository).delete(existing);

            assertThatThrownBy(() -> priceTableService.delete(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Erro ao excluir tabela de preços")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }
}
