package com.br.manager.domain.operational.usecase;

import com.br.manager.domain.common.entity.SynchronizeNameTableEnum;
import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.common.service.SynchronizeService;
import com.br.manager.domain.operational.dto.NozzleInputDTO;
import com.br.manager.domain.operational.dto.NozzleResponseDTO;
import com.br.manager.domain.operational.entity.Nozzle;
import com.br.manager.domain.operational.entity.Product;
import com.br.manager.domain.operational.fixture.NozzleFixture;
import com.br.manager.domain.operational.fixture.ProductFixture;
import com.br.manager.domain.operational.mapper.NozzleMapper;
import com.br.manager.domain.operational.repository.NozzleRepository;
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
class NozzleServiceTest {

    private static final String TABLE = SynchronizeNameTableEnum.NOZZLE.name();
    private static final LocalDateTime DEFAULT_SINCE = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
    private static final String ONLY_FUEL_MESSAGE = "Apenas produtos do tipo COMBUSTÍVEL podem ser vendidos pela bomba.";
    private static final String CODE_REQUIRED_MESSAGE = "Nozzle code is required.";

    @Mock
    private NozzleRepository nozzleRepository;

    @Mock
    private ProductService productService;

    @Mock
    private SynchronizeService synchronizeService;

    @Spy
    private NozzleMapper nozzleMapper = new NozzleMapper();

    @InjectMocks
    private NozzleService nozzleService;

    private static NozzleInputDTO inputFor(Product product) {
        return NozzleFixture.validInput(UUID.randomUUID(), UUID.randomUUID(), product.getId());
    }

    private static Nozzle withoutCode() {
        Nozzle invalid = NozzleFixture.persistedEntity();
        invalid.setCode(" ");
        return invalid;
    }

    @Nested
    class Create {

        @Test
        void shouldPersistNozzleForFuelProduct() {
            Product fuel = ProductFixture.persistedFuel();
            NozzleInputDTO input = inputFor(fuel);
            when(productService.getProductEntityById(fuel.getId())).thenReturn(fuel);
            when(nozzleRepository.saveAndFlush(any(Nozzle.class))).thenAnswer(invocation -> {
                Nozzle nozzle = invocation.getArgument(0);
                nozzle.setId(UUID.randomUUID());
                return nozzle;
            });

            NozzleResponseDTO response = nozzleService.create(input);

            assertThat(response.getId()).isNotNull();
            assertThat(response.getCode()).isEqualTo(NozzleFixture.CODE);
            assertThat(response.getPumpId()).isEqualTo(input.getPumpId());
            assertThat(response.getTankId()).isEqualTo(input.getTankId());
            assertThat(response.getMeterNumber()).isEqualTo(NozzleFixture.METER_NUMBER);
        }

        @Test
        void shouldRejectNonFuelProduct() {
            Product convenience = ProductFixture.persistedConvenience();
            when(productService.getProductEntityById(convenience.getId())).thenReturn(convenience);

            assertThatThrownBy(() -> nozzleService.create(inputFor(convenience)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ONLY_FUEL_MESSAGE);
            verify(nozzleRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldPropagateNotFoundWhenProductDoesNotExist() {
            NozzleInputDTO input = NozzleFixture.validInput();
            when(productService.getProductEntityById(input.getProductId())).thenThrow(new NotFoundBusinessException("not found"));

            assertThatThrownBy(() -> nozzleService.create(input)).isInstanceOf(NotFoundBusinessException.class);
            verify(nozzleRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldTranslateConstraintViolationIntoBusinessException() {
            Product fuel = ProductFixture.persistedFuel();
            when(productService.getProductEntityById(fuel.getId())).thenReturn(fuel);
            when(nozzleRepository.saveAndFlush(any(Nozzle.class))).thenThrow(constraintViolationOf(withoutCode()));

            assertThatThrownBy(() -> nozzleService.create(inputFor(fuel)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(CODE_REQUIRED_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            Product fuel = ProductFixture.persistedFuel();
            when(productService.getProductEntityById(fuel.getId())).thenReturn(fuel);
            when(nozzleRepository.saveAndFlush(any(Nozzle.class))).thenThrow(new DataIntegrityViolationException("uk"));

            assertThatThrownBy(() -> nozzleService.create(inputFor(fuel)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(NozzleFixture.CODE)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingNozzle() {
            Nozzle existing = NozzleFixture.persistedEntity();
            Product fuel = ProductFixture.persistedFuel();
            NozzleInputDTO input = inputFor(fuel);
            input.setId(existing.getId());
            input.setCode(NozzleFixture.OTHER_CODE);
            when(nozzleRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(productService.getProductEntityById(fuel.getId())).thenReturn(fuel);
            when(nozzleRepository.saveAndFlush(existing)).thenReturn(existing);

            NozzleResponseDTO response = nozzleService.update(input);

            assertThat(response.getCode()).isEqualTo(NozzleFixture.OTHER_CODE);
            assertThat(response.getProductId()).isEqualTo(fuel.getId());
        }

        @Test
        void shouldThrowNotFoundWhenNozzleDoesNotExist() {
            NozzleInputDTO input = NozzleFixture.validInput();
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> nozzleService.update(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(input.getId().toString());
            verify(nozzleRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldPropagateNotFoundWhenProductDoesNotExist() {
            Nozzle existing = NozzleFixture.persistedEntity();
            NozzleInputDTO input = NozzleFixture.validInput();
            input.setId(existing.getId());
            when(nozzleRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(productService.getProductEntityById(input.getProductId())).thenThrow(new NotFoundBusinessException("not found"));

            assertThatThrownBy(() -> nozzleService.update(input)).isInstanceOf(NotFoundBusinessException.class);
            verify(nozzleRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldRejectNonFuelProduct() {
            Nozzle existing = NozzleFixture.persistedEntity();
            Product convenience = ProductFixture.persistedConvenience();
            NozzleInputDTO input = inputFor(convenience);
            input.setId(existing.getId());
            when(nozzleRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(productService.getProductEntityById(convenience.getId())).thenReturn(convenience);

            assertThatThrownBy(() -> nozzleService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ONLY_FUEL_MESSAGE);
            verify(nozzleRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldTranslateConstraintViolationIntoBusinessException() {
            Nozzle existing = NozzleFixture.persistedEntity();
            Product fuel = ProductFixture.persistedFuel();
            NozzleInputDTO input = inputFor(fuel);
            input.setId(existing.getId());
            when(nozzleRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(productService.getProductEntityById(fuel.getId())).thenReturn(fuel);
            when(nozzleRepository.saveAndFlush(existing)).thenThrow(constraintViolationOf(withoutCode()));

            assertThatThrownBy(() -> nozzleService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(CODE_REQUIRED_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            Nozzle existing = NozzleFixture.persistedEntity();
            Product fuel = ProductFixture.persistedFuel();
            NozzleInputDTO input = inputFor(fuel);
            input.setId(existing.getId());
            when(nozzleRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(productService.getProductEntityById(fuel.getId())).thenReturn(fuel);
            when(nozzleRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("uk"));

            assertThatThrownBy(() -> nozzleService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListActiveNozzles() {
            when(nozzleRepository.findAllByActiveTrue()).thenReturn(List.of(NozzleFixture.persistedEntity()));

            assertThat(nozzleService.findAll()).hasSize(1);
        }

        @Test
        void shouldSearchByCode() {
            when(nozzleRepository.findByCodeContainingIgnoreCaseAndActiveTrue("bico"))
                    .thenReturn(List.of(NozzleFixture.persistedEntity()));

            assertThat(nozzleService.findByDescription("bico")).hasSize(1);
        }

        @Test
        void shouldFallbackToFindAllWhenDescriptionIsBlank() {
            when(nozzleRepository.findAllByActiveTrue()).thenReturn(List.of());

            assertThat(nozzleService.findByDescription("")).isEmpty();
            verify(nozzleRepository, never()).findByCodeContainingIgnoreCaseAndActiveTrue(any());
        }

        @Test
        void shouldFindById() {
            Nozzle existing = NozzleFixture.persistedEntity();
            when(nozzleRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);

            assertThat(nozzleService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> nozzleService.find(id)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldReturnEntityById() {
            Nozzle existing = NozzleFixture.persistedEntity();
            when(nozzleRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);

            assertThat(nozzleService.getNozzleEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenEntityDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> nozzleService.getNozzleEntityById(id)).isInstanceOf(NotFoundBusinessException.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateAndMarkDeletionDate() {
            Nozzle existing = NozzleFixture.persistedEntity();
            when(nozzleRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            nozzleService.delete(existing.getId());

            ArgumentCaptor<Nozzle> captor = ArgumentCaptor.forClass(Nozzle.class);
            verify(nozzleRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getActive()).isFalse();
            assertThat(captor.getValue().getDeletedAt()).isNotNull();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownNozzle() {
            UUID id = UUID.randomUUID();
            when(nozzleRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> nozzleService.delete(id)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            Nozzle existing = NozzleFixture.persistedEntity();
            when(nozzleRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(nozzleRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> nozzleService.delete(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class ChangedSinceSynchronize {

        @Test
        void shouldReturnRecordsChangedSinceLastSynchronize() {
            LocalDateTime lastSynchronize = LocalDateTime.of(2026, 5, 10, 8, 30);
            Nozzle changed = NozzleFixture.persistedEntity();
            when(synchronizeService.findByTableName(TABLE)).thenReturn(synchronizeAt(lastSynchronize));
            when(nozzleRepository.findAllChangedSince(lastSynchronize)).thenReturn(List.of(changed));

            assertThat(nozzleService.findChangedSinceSynchronize(TABLE))
                    .extracting(NozzleResponseDTO::getId)
                    .containsExactly(changed.getId());
        }

        @Test
        void shouldUseDefaultDateWhenTableWasNeverSynchronized() {
            when(synchronizeService.findByTableName(TABLE)).thenReturn(synchronizeAt(null));
            when(nozzleRepository.findAllChangedSince(DEFAULT_SINCE)).thenReturn(List.of());

            assertThat(nozzleService.findChangedSinceSynchronize(TABLE)).isEmpty();
            verify(nozzleRepository).findAllChangedSince(DEFAULT_SINCE);
        }
    }
}
