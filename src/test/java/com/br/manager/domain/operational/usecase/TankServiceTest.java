package com.br.manager.domain.operational.usecase;

import com.br.manager.domain.common.entity.SynchronizeNameTableEnum;
import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.common.service.SynchronizeService;
import com.br.manager.domain.operational.dto.TankInputDTO;
import com.br.manager.domain.operational.dto.TankResponseDTO;
import com.br.manager.domain.operational.entity.Product;
import com.br.manager.domain.operational.entity.Tank;
import com.br.manager.domain.operational.fixture.ProductFixture;
import com.br.manager.domain.operational.fixture.TankFixture;
import com.br.manager.domain.operational.mapper.TankMapper;
import com.br.manager.domain.operational.repository.TankRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
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
class TankServiceTest {

    private static final String TABLE = SynchronizeNameTableEnum.TANK.name();
    private static final LocalDateTime DEFAULT_SINCE = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
    private static final String ONLY_FUEL_MESSAGE = "Apenas produtos do tipo COMBUSTÍVEL podem ser atribuídos a um tanque.";
    private static final String CAPACITY_MESSAGE = "A capacidade deve ser maior que zero.";

    @Mock
    private TankRepository tankRepository;

    @Mock
    private ProductService productService;

    @Mock
    private SynchronizeService synchronizeService;

    @Spy
    private TankMapper tankMapper = new TankMapper();

    @InjectMocks
    private TankService tankService;

    private static Tank withZeroCapacity() {
        Tank invalid = TankFixture.persistedEntity();
        invalid.setCapacityLiters(BigDecimal.ZERO);
        return invalid;
    }

    @Nested
    class Create {

        @Test
        void shouldPersistTankForFuelProduct() {
            Product fuel = ProductFixture.persistedFuel();
            when(productService.getProductEntityById(fuel.getId())).thenReturn(fuel);
            when(tankRepository.saveAndFlush(any(Tank.class))).thenAnswer(invocation -> {
                Tank tank = invocation.getArgument(0);
                tank.setId(UUID.randomUUID());
                return tank;
            });

            TankResponseDTO response = tankService.create(TankFixture.validInput(fuel.getId()));

            assertThat(response.getId()).isNotNull();
            assertThat(response.getCode()).isEqualTo(TankFixture.CODE);
            assertThat(response.getProductId()).isEqualTo(fuel.getId());
            assertThat(response.getCapacityLiters()).isEqualTo(TankFixture.CAPACITY);
        }

        @Test
        void shouldRejectNonFuelProduct() {
            Product convenience = ProductFixture.persistedConvenience();
            when(productService.getProductEntityById(convenience.getId())).thenReturn(convenience);

            assertThatThrownBy(() -> tankService.create(TankFixture.validInput(convenience.getId())))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(TankFixture.CODE)
                    .cause()
                    .hasMessage(ONLY_FUEL_MESSAGE);
            verify(tankRepository, never()).saveAndFlush(any());
        }

        @Test
        @Disabled("BUG: TankService.create catches NotFoundBusinessException in the generic catch(Exception) and wraps it "
                + "in a plain BusinessException (HTTP 400), unlike TankService.update and NozzleService.create (HTTP 404)")
        void shouldPropagateNotFoundWhenProductDoesNotExist() {
            UUID productId = UUID.randomUUID();
            when(productService.getProductEntityById(productId)).thenThrow(new NotFoundBusinessException("not found"));

            assertThatThrownBy(() -> tankService.create(TankFixture.validInput(productId)))
                    .isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldTranslateConstraintViolationIntoBusinessException() {
            Product fuel = ProductFixture.persistedFuel();
            TankInputDTO input = TankFixture.validInput(fuel.getId());
            input.setCapacityLiters(BigDecimal.ZERO);
            when(productService.getProductEntityById(fuel.getId())).thenReturn(fuel);
            when(tankRepository.saveAndFlush(any(Tank.class))).thenThrow(constraintViolationOf(withZeroCapacity()));

            assertThatThrownBy(() -> tankService.create(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(CAPACITY_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            Product fuel = ProductFixture.persistedFuel();
            when(productService.getProductEntityById(fuel.getId())).thenReturn(fuel);
            when(tankRepository.saveAndFlush(any(Tank.class))).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> tankService.create(TankFixture.validInput(fuel.getId())))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(TankFixture.CODE)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingTank() {
            Tank existing = TankFixture.persistedEntity();
            Product fuel = ProductFixture.persistedFuel();
            TankInputDTO input = TankFixture.validInput(fuel.getId(), TankFixture.OTHER_CODE);
            input.setId(existing.getId());
            when(tankRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(productService.getProductEntityById(fuel.getId())).thenReturn(fuel);
            when(tankRepository.saveAndFlush(existing)).thenReturn(existing);

            TankResponseDTO response = tankService.update(input);

            assertThat(response.getCode()).isEqualTo(TankFixture.OTHER_CODE);
            assertThat(response.getProductId()).isEqualTo(fuel.getId());
        }

        @Test
        void shouldThrowNotFoundWhenTankDoesNotExist() {
            TankInputDTO input = TankFixture.validInput(UUID.randomUUID());
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> tankService.update(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(input.getId().toString());
            verify(tankRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldPropagateNotFoundWhenProductDoesNotExist() {
            Tank existing = TankFixture.persistedEntity();
            UUID productId = UUID.randomUUID();
            TankInputDTO input = TankFixture.validInput(productId);
            input.setId(existing.getId());
            when(tankRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(productService.getProductEntityById(productId)).thenThrow(new NotFoundBusinessException("not found"));

            assertThatThrownBy(() -> tankService.update(input)).isInstanceOf(NotFoundBusinessException.class);
            verify(tankRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldRejectNonFuelProduct() {
            Tank existing = TankFixture.persistedEntity();
            Product convenience = ProductFixture.persistedConvenience();
            TankInputDTO input = TankFixture.validInput(convenience.getId());
            input.setId(existing.getId());
            when(tankRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(productService.getProductEntityById(convenience.getId())).thenReturn(convenience);

            assertThatThrownBy(() -> tankService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .cause()
                    .hasMessage(ONLY_FUEL_MESSAGE);
            verify(tankRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldTranslateConstraintViolationIntoBusinessException() {
            Tank existing = TankFixture.persistedEntity();
            Product fuel = ProductFixture.persistedFuel();
            TankInputDTO input = TankFixture.validInput(fuel.getId());
            input.setId(existing.getId());
            input.setCapacityLiters(BigDecimal.ZERO);
            when(tankRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(productService.getProductEntityById(fuel.getId())).thenReturn(fuel);
            when(tankRepository.saveAndFlush(existing)).thenThrow(constraintViolationOf(withZeroCapacity()));

            assertThatThrownBy(() -> tankService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(CAPACITY_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            Tank existing = TankFixture.persistedEntity();
            Product fuel = ProductFixture.persistedFuel();
            TankInputDTO input = TankFixture.validInput(fuel.getId());
            input.setId(existing.getId());
            when(tankRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(productService.getProductEntityById(fuel.getId())).thenReturn(fuel);
            when(tankRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> tankService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(TankFixture.CODE)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListActiveTanks() {
            when(tankRepository.findAllByActiveTrue()).thenReturn(List.of(TankFixture.persistedEntity()));

            assertThat(tankService.findAll()).hasSize(1);
        }

        @Test
        void shouldSearchByCode() {
            when(tankRepository.findByCodeContainingIgnoreCaseAndActiveTrue("tq"))
                    .thenReturn(List.of(TankFixture.persistedEntity()));

            assertThat(tankService.findByDescription("tq")).hasSize(1);
        }

        @Test
        void shouldFallbackToFindAllWhenDescriptionIsBlank() {
            when(tankRepository.findAllByActiveTrue()).thenReturn(List.of());

            assertThat(tankService.findByDescription(null)).isEmpty();
            verify(tankRepository, never()).findByCodeContainingIgnoreCaseAndActiveTrue(any());
        }

        @Test
        void shouldFindById() {
            Tank existing = TankFixture.persistedEntity();
            when(tankRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);

            assertThat(tankService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> tankService.find(id)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldReturnEntityById() {
            Tank existing = TankFixture.persistedEntity();
            when(tankRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);

            assertThat(tankService.getTankEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenEntityDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> tankService.getTankEntityById(id)).isInstanceOf(NotFoundBusinessException.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateAndMarkDeletionDate() {
            Tank existing = TankFixture.persistedEntity();
            when(tankRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            tankService.delete(existing.getId());

            ArgumentCaptor<Tank> captor = ArgumentCaptor.forClass(Tank.class);
            verify(tankRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getActive()).isFalse();
            assertThat(captor.getValue().getDeletedAt()).isNotNull();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownTank() {
            UUID id = UUID.randomUUID();
            when(tankRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> tankService.delete(id)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            Tank existing = TankFixture.persistedEntity();
            when(tankRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(tankRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> tankService.delete(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class ChangedSinceSynchronize {

        @Test
        void shouldReturnRecordsChangedSinceLastSynchronize() {
            LocalDateTime lastSynchronize = LocalDateTime.of(2026, 5, 10, 8, 30);
            Tank changed = TankFixture.persistedEntity();
            when(synchronizeService.findByTableName(TABLE)).thenReturn(synchronizeAt(lastSynchronize));
            when(tankRepository.findAllChangedSince(lastSynchronize)).thenReturn(List.of(changed));

            assertThat(tankService.findChangedSinceSynchronize(TABLE))
                    .extracting(TankResponseDTO::getId)
                    .containsExactly(changed.getId());
        }

        @Test
        void shouldUseDefaultDateWhenTableWasNeverSynchronized() {
            when(synchronizeService.findByTableName(TABLE)).thenReturn(synchronizeAt(null));
            when(tankRepository.findAllChangedSince(DEFAULT_SINCE)).thenReturn(List.of());

            assertThat(tankService.findChangedSinceSynchronize(TABLE)).isEmpty();
            verify(tankRepository).findAllChangedSince(DEFAULT_SINCE);
        }
    }
}
