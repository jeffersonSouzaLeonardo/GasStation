package com.br.manager.domain.organization.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.organization.dto.StationInputDTO;
import com.br.manager.domain.organization.dto.StationResponseDTO;
import com.br.manager.domain.organization.entity.Station;
import com.br.manager.domain.organization.fixture.ConstraintViolationFixture;
import com.br.manager.domain.organization.fixture.StationFixture;
import com.br.manager.domain.organization.mapper.StationMapper;
import com.br.manager.domain.organization.repository.StationRepository;
import jakarta.validation.ConstraintViolationException;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    private static final String NAME_REQUIRED = "O nome do posto é obrigatório.";

    @Mock
    private StationRepository stationRepository;

    @Spy
    private StationMapper stationMapper = new StationMapper();

    @InjectMocks
    private StationService stationService;

    private static ConstraintViolationException nameViolation() {
        Station invalid = StationFixture.persistedEntity();
        invalid.setName(null);
        return ConstraintViolationFixture.violationsOf(invalid);
    }

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnStation() {
            when(stationRepository.saveAndFlush(any(Station.class))).thenAnswer(invocation -> {
                Station station = invocation.getArgument(0);
                station.setId(UUID.randomUUID());
                return station;
            });

            StationResponseDTO response = stationService.create(StationFixture.validInput());

            assertThat(response.getId()).isNotNull();
            assertThat(response.getCnpj()).isEqualTo(StationFixture.CNPJ);
            assertThat(response.getTimezone()).isEqualTo(StationFixture.TIMEZONE);
        }

        @Test
        void shouldApplyDefaultTimezoneWhenNotInformed() {
            StationInputDTO input = StationFixture.validInput();
            input.setTimezone(" ");
            when(stationRepository.saveAndFlush(any(Station.class))).thenAnswer(invocation -> invocation.getArgument(0));

            stationService.create(input);

            ArgumentCaptor<Station> captor = ArgumentCaptor.forClass(Station.class);
            verify(stationRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getTimezone()).isEqualTo(StationFixture.TIMEZONE);
        }

        @Test
        void shouldKeepInformedTimezone() {
            StationInputDTO input = StationFixture.validInput();
            input.setTimezone(StationFixture.OTHER_TIMEZONE);
            when(stationRepository.saveAndFlush(any(Station.class))).thenAnswer(invocation -> invocation.getArgument(0));

            assertThat(stationService.create(input).getTimezone()).isEqualTo(StationFixture.OTHER_TIMEZONE);
        }

        @Test
        void shouldTranslateConstraintViolationIntoBusinessException() {
            when(stationRepository.saveAndFlush(any(Station.class))).thenThrow(nameViolation());

            assertThatThrownBy(() -> stationService.create(StationFixture.validInput()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(NAME_REQUIRED);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            when(stationRepository.saveAndFlush(any(Station.class)))
                    .thenThrow(new DataIntegrityViolationException("fk company"));

            assertThatThrownBy(() -> stationService.create(StationFixture.validInput()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(StationFixture.NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        void shouldWrapErrorWhenInputIsNull() {
            assertThatThrownBy(() -> stationService.create(null))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao salvar posto ");
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingStation() {
            Station existing = StationFixture.persistedEntity();
            StationInputDTO input = StationFixture.validInput(existing.getCompanyId());
            input.setId(existing.getId());
            input.setName("Posto Atualizado");
            when(stationRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(stationRepository.saveAndFlush(existing)).thenReturn(existing);

            StationResponseDTO response = stationService.update(input);

            assertThat(response.getId()).isEqualTo(existing.getId());
            assertThat(response.getName()).isEqualTo("Posto Atualizado");
        }

        @Test
        void shouldThrowNotFoundWhenStationDoesNotExist() {
            StationInputDTO input = StationFixture.validInput();
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> stationService.update(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(input.getId().toString());
            verify(stationRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldTranslateConstraintViolationIntoBusinessException() {
            Station existing = StationFixture.persistedEntity();
            StationInputDTO input = StationFixture.validInput();
            input.setId(existing.getId());
            when(stationRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(stationRepository.saveAndFlush(existing)).thenThrow(nameViolation());

            assertThatThrownBy(() -> stationService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(NAME_REQUIRED);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            Station existing = StationFixture.persistedEntity();
            StationInputDTO input = StationFixture.validInput();
            input.setId(existing.getId());
            when(stationRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(stationRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> stationService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .isNotInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(StationFixture.NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListActiveStations() {
            when(stationRepository.findAllByActiveTrue()).thenReturn(List.of(StationFixture.persistedEntity()));

            assertThat(stationService.findAll()).hasSize(1);
        }

        @Test
        void shouldSearchByName() {
            when(stationRepository.findByNameContainingIgnoreCaseAndActiveTrue("centro"))
                    .thenReturn(List.of(StationFixture.persistedEntity()));

            assertThat(stationService.findByName("centro")).hasSize(1);
        }

        @Test
        void shouldFallbackToFindAllWhenDescriptionIsBlank() {
            when(stationRepository.findAllByActiveTrue()).thenReturn(List.of());

            assertThat(stationService.findByDescription(" ")).isEmpty();
            verify(stationRepository, never()).findByNameContainingIgnoreCaseAndActiveTrue(any());
        }

        @Test
        void shouldFindById() {
            Station existing = StationFixture.persistedEntity();
            when(stationRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);

            assertThat(stationService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldReturnEntityById() {
            Station existing = StationFixture.persistedEntity();
            when(stationRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);

            assertThat(stationService.getStationEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> stationService.find(id)).isInstanceOf(NotFoundBusinessException.class);
            assertThatThrownBy(() -> stationService.getStationEntityById(id)).isInstanceOf(NotFoundBusinessException.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateStation() {
            Station existing = StationFixture.persistedEntity();
            when(stationRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            stationService.delete(existing.getId());

            ArgumentCaptor<Station> captor = ArgumentCaptor.forClass(Station.class);
            verify(stationRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getActive()).isFalse();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownStation() {
            UUID id = UUID.randomUUID();
            when(stationRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> stationService.delete(id)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldTranslateConstraintViolationIntoBusinessException() {
            Station existing = StationFixture.persistedEntity();
            when(stationRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(stationRepository.saveAndFlush(existing)).thenThrow(nameViolation());

            assertThatThrownBy(() -> stationService.delete(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(NAME_REQUIRED);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            Station existing = StationFixture.persistedEntity();
            when(stationRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(stationRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> stationService.delete(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao excluir posto")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }
}
