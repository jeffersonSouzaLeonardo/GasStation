package com.br.manager.domain.operational.usecase;

import com.br.manager.domain.common.entity.SynchronizeNameTableEnum;
import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.common.service.SynchronizeService;
import com.br.manager.domain.operational.dto.PumpInputDTO;
import com.br.manager.domain.operational.dto.PumpResponseDTO;
import com.br.manager.domain.operational.entity.Pump;
import com.br.manager.domain.operational.fixture.PumpFixture;
import com.br.manager.domain.operational.mapper.PumpMapper;
import com.br.manager.domain.operational.repository.PumpRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static com.br.manager.domain.operational.support.OperationalATestData.constraintViolationOf;
import static com.br.manager.domain.operational.support.OperationalATestData.synchronizeAt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PumpServiceTest {

    private static final String TABLE = SynchronizeNameTableEnum.PUMP.name();
    private static final LocalDateTime DEFAULT_SINCE = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
    private static final String CODE_SIZE_MESSAGE = "O código da bomba deve ter entre 1 e 50 caracteres.";

    @Mock
    private PumpRepository pumpRepository;

    @Mock
    private SynchronizeService synchronizeService;

    @Spy
    private PumpMapper pumpMapper = new PumpMapper();

    @InjectMocks
    private PumpService pumpService;

    private static Pump withTooLongCode() {
        Pump invalid = PumpFixture.persistedEntity();
        invalid.setCode(PumpFixture.TOO_LONG_CODE);
        return invalid;
    }

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnPump() {
            when(pumpRepository.saveAndFlush(any(Pump.class))).thenAnswer(invocation -> {
                Pump pump = invocation.getArgument(0);
                pump.setId(UUID.randomUUID());
                return pump;
            });

            PumpResponseDTO response = pumpService.create(PumpFixture.validInput());

            assertThat(response.getId()).isNotNull();
            assertThat(response.getCode()).isEqualTo(PumpFixture.CODE);
            assertThat(response.getLocation()).isEqualTo(PumpFixture.LOCATION);
        }

        @Test
        void shouldTranslateConstraintViolationIntoBusinessException() {
            when(pumpRepository.saveAndFlush(any(Pump.class))).thenThrow(constraintViolationOf(withTooLongCode()));

            assertThatThrownBy(() -> pumpService.create(PumpFixture.validInput(PumpFixture.TOO_LONG_CODE)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(CODE_SIZE_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            when(pumpRepository.saveAndFlush(any(Pump.class))).thenThrow(new DataIntegrityViolationException("db"));

            assertThatThrownBy(() -> pumpService.create(PumpFixture.validInput()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PumpFixture.CODE)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingPump() {
            Pump existing = PumpFixture.persistedEntity();
            PumpInputDTO input = PumpFixture.validInput(PumpFixture.OTHER_CODE);
            input.setId(existing.getId());
            when(pumpRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(pumpRepository.saveAndFlush(existing)).thenReturn(existing);

            PumpResponseDTO response = pumpService.update(input);

            assertThat(response.getCode()).isEqualTo(PumpFixture.OTHER_CODE);
        }

        @Test
        void shouldThrowNotFoundWhenPumpDoesNotExist() {
            PumpInputDTO input = PumpFixture.validInput();
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> pumpService.update(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(input.getId().toString());
            verify(pumpRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldTranslateConstraintViolationIntoBusinessException() {
            Pump existing = PumpFixture.persistedEntity();
            PumpInputDTO input = PumpFixture.validInput(PumpFixture.TOO_LONG_CODE);
            input.setId(existing.getId());
            when(pumpRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(pumpRepository.saveAndFlush(existing)).thenThrow(constraintViolationOf(withTooLongCode()));

            assertThatThrownBy(() -> pumpService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(CODE_SIZE_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            Pump existing = PumpFixture.persistedEntity();
            PumpInputDTO input = PumpFixture.validInput();
            input.setId(existing.getId());
            when(pumpRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(pumpRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("db"));

            assertThatThrownBy(() -> pumpService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(PumpFixture.CODE)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListActivePumps() {
            when(pumpRepository.findAllByActiveTrue()).thenReturn(List.of(PumpFixture.persistedEntity()));

            assertThat(pumpService.findAll()).hasSize(1);
        }

        @Test
        void shouldSearchByCode() {
            when(pumpRepository.findByCodeContainingIgnoreCaseAndActiveTrue("bomba"))
                    .thenReturn(List.of(PumpFixture.persistedEntity()));

            assertThat(pumpService.findByDescription("bomba")).hasSize(1);
        }

        @Test
        void shouldFallbackToFindAllWhenDescriptionIsBlank() {
            when(pumpRepository.findAllByActiveTrue()).thenReturn(List.of());

            assertThat(pumpService.findByDescription(" ")).isEmpty();
            verify(pumpRepository, never()).findByCodeContainingIgnoreCaseAndActiveTrue(any());
        }

        @Test
        void shouldFindById() {
            Pump existing = PumpFixture.persistedEntity();
            when(pumpRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);

            assertThat(pumpService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> pumpService.find(id)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldReturnEntityById() {
            Pump existing = PumpFixture.persistedEntity();
            when(pumpRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);

            assertThat(pumpService.getPumpEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenEntityDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> pumpService.getPumpEntityById(id)).isInstanceOf(NotFoundBusinessException.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateAndMarkDeletionDate() {
            Pump existing = PumpFixture.persistedEntity();
            when(pumpRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            pumpService.delete(existing.getId());

            ArgumentCaptor<Pump> captor = ArgumentCaptor.forClass(Pump.class);
            verify(pumpRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getActive()).isFalse();
            assertThat(captor.getValue().getDeletedAt()).isNotNull();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownPump() {
            UUID id = UUID.randomUUID();
            when(pumpRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pumpService.delete(id)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            Pump existing = PumpFixture.persistedEntity();
            when(pumpRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(pumpRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> pumpService.delete(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class ChangedSinceSynchronize {

        @Test
        void shouldReturnRecordsChangedSinceLastSynchronize() {
            LocalDateTime lastSynchronize = LocalDateTime.of(2026, 5, 10, 8, 30);
            Pump changed = PumpFixture.persistedEntity();
            when(synchronizeService.findByTableName(TABLE)).thenReturn(synchronizeAt(lastSynchronize));
            when(pumpRepository.findAllChangedSince(lastSynchronize)).thenReturn(List.of(changed));

            assertThat(pumpService.findChangedSinceSynchronize(TABLE))
                    .extracting(PumpResponseDTO::getId)
                    .containsExactly(changed.getId());
        }

        @Test
        void shouldUseDefaultDateWhenTableWasNeverSynchronized() {
            when(synchronizeService.findByTableName(TABLE)).thenReturn(synchronizeAt(null));
            when(pumpRepository.findAllChangedSince(DEFAULT_SINCE)).thenReturn(List.of());

            assertThat(pumpService.findChangedSinceSynchronize(TABLE)).isEmpty();
            verify(pumpRepository).findAllChangedSince(DEFAULT_SINCE);
        }
    }
}
