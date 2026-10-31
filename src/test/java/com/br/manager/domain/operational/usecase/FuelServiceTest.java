package com.br.manager.domain.operational.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.dto.FuelInputDTO;
import com.br.manager.domain.operational.dto.FuelResponseDTO;
import com.br.manager.domain.operational.entity.Fuel;
import com.br.manager.domain.operational.fixture.FuelFixture;
import com.br.manager.domain.operational.mapper.FuelMapper;
import com.br.manager.domain.operational.repository.FuelRepository;
import com.br.manager.domain.operational.support.OperationalBViolations;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FuelServiceTest {

    private static final Long UNKNOWN_ID = 999L;

    @Mock
    private FuelRepository fuelRepository;

    @Spy
    private FuelMapper fuelMapper = new FuelMapper();

    @InjectMocks
    private FuelService fuelService;

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnFuel() {
            when(fuelRepository.saveAndFlush(any(Fuel.class))).thenAnswer(invocation -> {
                Fuel fuel = invocation.getArgument(0);
                fuel.setId(FuelFixture.ID);
                return fuel;
            });

            FuelResponseDTO response = fuelService.create(FuelFixture.validInput());

            assertThat(response.getId()).isEqualTo(FuelFixture.ID);
            assertThat(response.getName()).isEqualTo(FuelFixture.NAME);
            assertThat(response.getUnit()).isEqualTo(FuelFixture.UNIT);
        }

        @Test
        void shouldConvertConstraintViolationsIntoBusinessException() {
            when(fuelRepository.saveAndFlush(any(Fuel.class))).thenThrow(OperationalBViolations.violationsOf(new Fuel()));

            assertThatThrownBy(() -> fuelService.create(FuelFixture.validInput()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("O nome do combustível é obrigatório.");
        }

        @Test
        void shouldWrapPersistenceErrorIncludingFuelName() {
            when(fuelRepository.saveAndFlush(any(Fuel.class))).thenThrow(new DataIntegrityViolationException("db"));

            assertThatThrownBy(() -> fuelService.create(FuelFixture.validInput()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao salvar combustível " + FuelFixture.NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        void shouldWrapPersistenceErrorWithoutNameWhenNameIsBlank() {
            FuelInputDTO input = FuelFixture.validInput();
            input.setName(" ");
            when(fuelRepository.saveAndFlush(any(Fuel.class))).thenThrow(new DataIntegrityViolationException("db"));

            assertThatThrownBy(() -> fuelService.create(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao salvar combustível ");
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingFuel() {
            Fuel existing = FuelFixture.persistedEntity();
            FuelInputDTO input = FuelFixture.otherInput();
            input.setId(existing.getId());
            when(fuelRepository.findByIdAndDeletedIsNull(existing.getId())).thenReturn(existing);
            when(fuelRepository.saveAndFlush(existing)).thenReturn(existing);

            FuelResponseDTO response = fuelService.update(input);

            assertThat(response.getName()).isEqualTo(FuelFixture.OTHER_NAME);
        }

        @Test
        void shouldThrowNotFoundWhenFuelDoesNotExist() {
            FuelInputDTO input = FuelFixture.validInput();
            input.setId(UNKNOWN_ID);

            assertThatThrownBy(() -> fuelService.update(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(UNKNOWN_ID.toString());
            verify(fuelRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationsIntoBusinessException() {
            Fuel existing = FuelFixture.persistedEntity();
            FuelInputDTO input = FuelFixture.validInput();
            input.setId(existing.getId());
            when(fuelRepository.findByIdAndDeletedIsNull(existing.getId())).thenReturn(existing);
            when(fuelRepository.saveAndFlush(existing)).thenThrow(OperationalBViolations.violationsOf(new Fuel()));

            assertThatThrownBy(() -> fuelService.update(input))
                    .isExactlyInstanceOf(BusinessException.class)
                    .hasMessageContaining("A unidade de medida é obrigatória.");
        }

        @Test
        void shouldWrapPersistenceErrorIncludingFuelName() {
            Fuel existing = FuelFixture.persistedEntity();
            FuelInputDTO input = FuelFixture.validInput();
            input.setId(existing.getId());
            when(fuelRepository.findByIdAndDeletedIsNull(existing.getId())).thenReturn(existing);
            when(fuelRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("db"));

            assertThatThrownBy(() -> fuelService.update(input))
                    .isExactlyInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao salvar combustível " + FuelFixture.NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListNotDeletedFuels() {
            when(fuelRepository.findAllByDeletedIsNull()).thenReturn(List.of(FuelFixture.persistedEntity()));

            assertThat(fuelService.findAll())
                    .extracting(FuelResponseDTO::getName)
                    .containsExactly(FuelFixture.NAME);
        }

        @Test
        void shouldSearchByName() {
            when(fuelRepository.findByNameContainingIgnoreCaseAndDeletedIsNull("gasolina"))
                    .thenReturn(List.of(FuelFixture.persistedEntity()));

            assertThat(fuelService.findByDescription("gasolina")).hasSize(1);
        }

        @Test
        void shouldFindById() {
            Fuel existing = FuelFixture.persistedEntity();
            when(fuelRepository.findByIdAndDeletedIsNull(existing.getId())).thenReturn(existing);

            assertThat(fuelService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            assertThatThrownBy(() -> fuelService.find(UNKNOWN_ID))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(UNKNOWN_ID.toString());
        }

        @Test
        void shouldReturnEntityById() {
            Fuel existing = FuelFixture.persistedEntity();
            when(fuelRepository.findByIdAndDeletedIsNull(existing.getId())).thenReturn(existing);

            assertThat(fuelService.getFuelEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenEntityIdDoesNotExist() {
            assertThatThrownBy(() -> fuelService.getFuelEntityById(UNKNOWN_ID))
                    .isInstanceOf(NotFoundBusinessException.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldSoftDeleteFuel() {
            Fuel existing = FuelFixture.persistedEntity();
            when(fuelRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            fuelService.delete(existing.getId());

            ArgumentCaptor<Fuel> captor = ArgumentCaptor.forClass(Fuel.class);
            verify(fuelRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getDeleted()).isNotNull();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownFuel() {
            when(fuelRepository.findById(UNKNOWN_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> fuelService.delete(UNKNOWN_ID)).isInstanceOf(NotFoundBusinessException.class);
            verify(fuelRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationsIntoBusinessException() {
            Fuel existing = FuelFixture.persistedEntity();
            when(fuelRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(fuelRepository.saveAndFlush(existing)).thenThrow(OperationalBViolations.violationsOf(new Fuel()));

            assertThatThrownBy(() -> fuelService.delete(existing.getId()))
                    .isExactlyInstanceOf(BusinessException.class)
                    .hasMessageContaining("O status do combustível é obrigatório.");
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            Fuel existing = FuelFixture.persistedEntity();
            when(fuelRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(fuelRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("db"));

            assertThatThrownBy(() -> fuelService.delete(existing.getId()))
                    .isExactlyInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao deletar combustível ")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }
}
