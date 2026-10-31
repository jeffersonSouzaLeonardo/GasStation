package com.br.manager.domain.operational.usecase;

import com.br.manager.domain.common.entity.Synchronize;
import com.br.manager.domain.common.entity.SynchronizeNameTableEnum;
import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.common.service.SynchronizeService;
import com.br.manager.domain.operational.dto.SupplierInputDTO;
import com.br.manager.domain.operational.dto.SupplierResponseDTO;
import com.br.manager.domain.operational.entity.Supplier;
import com.br.manager.domain.operational.fixture.SupplierFixture;
import com.br.manager.domain.operational.mapper.SupplierMapper;
import com.br.manager.domain.operational.repository.SupplierRepository;
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

import java.time.LocalDateTime;
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
class SupplierServiceTest {

    private static final String TABLE_NAME = SynchronizeNameTableEnum.SUPPLIER.name();
    private static final LocalDateTime DEFAULT_SINCE = LocalDateTime.of(2026, 1, 1, 0, 0, 0);

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SynchronizeService synchronizeService;

    @Spy
    private SupplierMapper supplierMapper = new SupplierMapper();

    @InjectMocks
    private SupplierService supplierService;

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnSupplierWithoutMasks() {
            when(supplierRepository.saveAndFlush(any(Supplier.class))).thenAnswer(invocation -> {
                Supplier supplier = invocation.getArgument(0);
                supplier.setId(UUID.randomUUID());
                return supplier;
            });

            SupplierResponseDTO response = supplierService.create(SupplierFixture.validInput());

            assertThat(response.getId()).isNotNull();
            assertThat(response.getCnpjCpf()).isEqualTo(SupplierFixture.CNPJ);
            assertThat(response.getPhone()).isEqualTo(SupplierFixture.PHONE);
        }

        @Test
        void shouldConvertConstraintViolationsIntoBusinessException() {
            when(supplierRepository.saveAndFlush(any(Supplier.class)))
                    .thenThrow(OperationalBViolations.violationsOf(new Supplier()));

            assertThatThrownBy(() -> supplierService.create(SupplierFixture.validInput()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("A razão social é obrigatória.");
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            when(supplierRepository.saveAndFlush(any(Supplier.class)))
                    .thenThrow(new DataIntegrityViolationException("too long"));

            assertThatThrownBy(() -> supplierService.create(SupplierFixture.validInput()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao salvar fornecedor")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingSupplier() {
            Supplier existing = SupplierFixture.persistedEntity();
            SupplierInputDTO input = SupplierFixture.otherInput();
            input.setId(existing.getId());
            when(supplierRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(supplierRepository.saveAndFlush(existing)).thenReturn(existing);

            SupplierResponseDTO response = supplierService.update(input);

            assertThat(response.getLegalName()).isEqualTo(SupplierFixture.OTHER_LEGAL_NAME);
            assertThat(response.getCnpjCpf()).isEqualTo(SupplierFixture.OTHER_CNPJ);
        }

        @Test
        void shouldThrowNotFoundWhenSupplierDoesNotExist() {
            SupplierInputDTO input = SupplierFixture.validInput();
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> supplierService.update(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(input.getId().toString());
            verify(supplierRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolationsIntoBusinessException() {
            Supplier existing = SupplierFixture.persistedEntity();
            SupplierInputDTO input = SupplierFixture.validInput();
            input.setId(existing.getId());
            when(supplierRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(supplierRepository.saveAndFlush(existing)).thenThrow(OperationalBViolations.violationsOf(new Supplier()));

            assertThatThrownBy(() -> supplierService.update(input))
                    .isExactlyInstanceOf(BusinessException.class)
                    .hasMessageContaining("A razão social é obrigatória.");
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            Supplier existing = SupplierFixture.persistedEntity();
            SupplierInputDTO input = SupplierFixture.validInput();
            input.setId(existing.getId());
            when(supplierRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(supplierRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("too long"));

            assertThatThrownBy(() -> supplierService.update(input))
                    .isExactlyInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao atualizar fornecedor")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListActiveSuppliers() {
            when(supplierRepository.findAllByActiveTrue()).thenReturn(List.of(SupplierFixture.persistedEntity()));

            assertThat(supplierService.findAll()).hasSize(1);
        }

        @Test
        void shouldSearchByLegalName() {
            when(supplierRepository.findByLegalNameContainingIgnoreCaseAndActiveTrue("petro"))
                    .thenReturn(List.of(SupplierFixture.persistedEntity()));

            assertThat(supplierService.findByDescription("petro"))
                    .extracting(SupplierResponseDTO::getLegalName)
                    .containsExactly(SupplierFixture.LEGAL_NAME);
        }

        @Test
        void shouldFallbackToFindAllWhenDescriptionIsBlank() {
            when(supplierRepository.findAllByActiveTrue()).thenReturn(List.of());

            assertThat(supplierService.findByDescription(" ")).isEmpty();
            verify(supplierRepository, never()).findByLegalNameContainingIgnoreCaseAndActiveTrue(any());
        }

        @Test
        void shouldFindById() {
            Supplier existing = SupplierFixture.persistedEntity();
            when(supplierRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);

            assertThat(supplierService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> supplierService.find(id))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(id.toString());
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateSupplierAndStampDeletedAt() {
            Supplier existing = SupplierFixture.persistedEntity();
            when(supplierRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            supplierService.delete(existing.getId());

            ArgumentCaptor<Supplier> captor = ArgumentCaptor.forClass(Supplier.class);
            verify(supplierRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getActive()).isFalse();
            assertThat(captor.getValue().getDeletedAt()).isNotNull();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownSupplier() {
            UUID id = UUID.randomUUID();
            when(supplierRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> supplierService.delete(id)).isInstanceOf(NotFoundBusinessException.class);
            verify(supplierRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    class ChangedSinceSynchronize {

        @Test
        void shouldReturnSuppliersChangedSinceLastSynchronize() {
            LocalDateTime lastSynchronize = LocalDateTime.of(2026, 10, 1, 8, 0);
            when(synchronizeService.findByTableName(TABLE_NAME)).thenReturn(synchronizeAt(lastSynchronize));
            when(supplierRepository.findAllChangedSince(lastSynchronize)).thenReturn(List.of(SupplierFixture.persistedEntity()));

            List<SupplierResponseDTO> changed = supplierService.findChangedSinceSynchronize(TABLE_NAME);

            assertThat(changed)
                    .extracting(SupplierResponseDTO::getLegalName)
                    .containsExactly(SupplierFixture.LEGAL_NAME);
        }

        @Test
        void shouldUseDefaultStartDateWhenNeverSynchronized() {
            when(synchronizeService.findByTableName(TABLE_NAME)).thenReturn(synchronizeAt(null));
            when(supplierRepository.findAllChangedSince(DEFAULT_SINCE)).thenReturn(List.of());

            assertThat(supplierService.findChangedSinceSynchronize(TABLE_NAME)).isEmpty();
            verify(supplierRepository).findAllChangedSince(DEFAULT_SINCE);
        }

        @Test
        void shouldPropagateNotFoundWhenSynchronizeRowIsMissing() {
            when(synchronizeService.findByTableName(TABLE_NAME)).thenThrow(new NotFoundBusinessException("missing"));

            assertThatThrownBy(() -> supplierService.findChangedSinceSynchronize(TABLE_NAME))
                    .isInstanceOf(NotFoundBusinessException.class);
            verify(supplierRepository, never()).findAllChangedSince(any());
        }

        private Synchronize synchronizeAt(LocalDateTime lastSynchronize) {
            Synchronize synchronize = new Synchronize();
            synchronize.setLastSynchronize(lastSynchronize);
            return synchronize;
        }
    }
}
