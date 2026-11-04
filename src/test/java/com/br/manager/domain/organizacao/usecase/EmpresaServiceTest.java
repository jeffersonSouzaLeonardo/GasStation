package com.br.manager.domain.organizacao.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.organizacao.dto.EmpresaInputDTO;
import com.br.manager.domain.organizacao.dto.EmpresaResponseDTO;
import com.br.manager.domain.organizacao.entity.Empresa;
import com.br.manager.domain.organizacao.fixture.EmpresaFixture;
import com.br.manager.domain.organizacao.mapper.EmpresaMapper;
import com.br.manager.domain.organizacao.repository.EmpresaRepository;
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
class EmpresaServiceTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @Spy
    private EmpresaMapper empresaMapper = new EmpresaMapper();

    @InjectMocks
    private EmpresaService empresaService;

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnEmpresa() {
            when(empresaRepository.saveAndFlush(any(Empresa.class))).thenAnswer(invocation -> {
                Empresa empresa = invocation.getArgument(0);
                empresa.setId(UUID.randomUUID());
                return empresa;
            });

            EmpresaResponseDTO response = empresaService.create(EmpresaFixture.validoInput());

            assertThat(response.getId()).isNotNull();
            assertThat(response.getCnpj()).isEqualTo(EmpresaFixture.CNPJ);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            when(empresaRepository.saveAndFlush(any(Empresa.class)))
                    .thenThrow(new DataIntegrityViolationException("duplicate cnpj"));

            assertThatThrownBy(() -> empresaService.create(EmpresaFixture.validoInput()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(EmpresaFixture.LEGAL_NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingEmpresa() {
            Empresa existing = EmpresaFixture.persistedEntity();
            EmpresaInputDTO input = EmpresaFixture.validoInput();
            input.setId(existing.getId());
            input.setLegalName("Razao Atualizada");
            when(empresaRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(empresaRepository.saveAndFlush(existing)).thenReturn(existing);

            EmpresaResponseDTO response = empresaService.update(input);

            assertThat(response.getLegalName()).isEqualTo("Razao Atualizada");
        }

        @Test
        void shouldThrowNotFoundWhenEmpresaDoesNotExist() {
            EmpresaInputDTO input = EmpresaFixture.validoInput();
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> empresaService.update(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(empresaRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListAtivoEmpresas() {
            when(empresaRepository.findAllByAtivoTrue()).thenReturn(List.of(EmpresaFixture.persistedEntity()));

            assertThat(empresaService.findAll()).hasSize(1);
        }

        @Test
        void shouldBuscaByLegalName() {
            when(empresaRepository.findByLegalNameContainingIgnoreCaseAndAtivoTrue("bom"))
                    .thenReturn(List.of(EmpresaFixture.persistedEntity()));

            assertThat(empresaService.findByName("bom")).hasSize(1);
        }

        @Test
        void shouldFallbackToFindAllWhenDescricaoIsVazio() {
            when(empresaRepository.findAllByAtivoTrue()).thenReturn(List.of());

            assertThat(empresaService.findByDescricao(" ")).isEmpty();
            verify(empresaRepository, never()).findByLegalNameContainingIgnoreCaseAndAtivoTrue(any());
        }

        @Test
        void shouldFindById() {
            Empresa existing = EmpresaFixture.persistedEntity();
            when(empresaRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);

            assertThat(empresaService.find(existing.getId()).getId()).isEqualTo(existing.getId());
            assertThat(empresaService.getEmpresaEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> empresaService.find(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            assertThatThrownBy(() -> empresaService.getEmpresaEntityById(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateEmpresa() {
            Empresa existing = EmpresaFixture.persistedEntity();
            when(empresaRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            empresaService.delete(existing.getId());

            ArgumentCaptor<Empresa> captor = ArgumentCaptor.forClass(Empresa.class);
            verify(empresaRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getAtivo()).isFalse();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownEmpresa() {
            UUID id = UUID.randomUUID();
            when(empresaRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> empresaService.delete(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }
    }
}
