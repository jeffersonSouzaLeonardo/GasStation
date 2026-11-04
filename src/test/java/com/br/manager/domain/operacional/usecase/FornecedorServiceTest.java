package com.br.manager.domain.operacional.usecase;

import com.br.manager.domain.comum.entity.Sincronizacao;
import com.br.manager.domain.comum.entity.NomeTabelaSincronizacaoEnum;
import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.comum.service.ServicoSincronizacao;
import com.br.manager.domain.operacional.dto.FornecedorInputDTO;
import com.br.manager.domain.operacional.dto.FornecedorResponseDTO;
import com.br.manager.domain.operacional.entity.Fornecedor;
import com.br.manager.domain.operacional.fixture.FornecedorFixture;
import com.br.manager.domain.operacional.mapper.FornecedorMapper;
import com.br.manager.domain.operacional.repository.FornecedorRepository;
import com.br.manager.domain.operacional.support.OperacionalBViolacoes;
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
class FornecedorServiceTest {

    private static final String TABLE_NAME = NomeTabelaSincronizacaoEnum.FORNECEDOR.name();
    private static final LocalDateTime DEFAULT_SINCE = LocalDateTime.of(2026, 1, 1, 0, 0, 0);

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private ServicoSincronizacao sincronizacaoService;

    @Spy
    private FornecedorMapper fornecedorMapper = new FornecedorMapper();

    @InjectMocks
    private FornecedorService fornecedorService;

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnFornecedorWithoutMasks() {
            when(fornecedorRepository.saveAndFlush(any(Fornecedor.class))).thenAnswer(invocation -> {
                Fornecedor fornecedor = invocation.getArgument(0);
                fornecedor.setId(UUID.randomUUID());
                return fornecedor;
            });

            FornecedorResponseDTO response = fornecedorService.create(FornecedorFixture.validoInput());

            assertThat(response.getId()).isNotNull();
            assertThat(response.getCnpjCpf()).isEqualTo(FornecedorFixture.CNPJ);
            assertThat(response.getTelefone()).isEqualTo(FornecedorFixture.PHONE);
        }

        @Test
        void shouldConvertConstraintViolacoesIntoExcecaoNegocio() {
            when(fornecedorRepository.saveAndFlush(any(Fornecedor.class)))
                    .thenThrow(OperacionalBViolacoes.violacoesOf(new Fornecedor()));

            assertThatThrownBy(() -> fornecedorService.create(FornecedorFixture.validoInput()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining("A razão social é obrigatória.");
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            when(fornecedorRepository.saveAndFlush(any(Fornecedor.class)))
                    .thenThrow(new DataIntegrityViolationException("too long"));

            assertThatThrownBy(() -> fornecedorService.create(FornecedorFixture.validoInput()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao salvar fornecedor")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingFornecedor() {
            Fornecedor existing = FornecedorFixture.persistedEntity();
            FornecedorInputDTO input = FornecedorFixture.outrosInput();
            input.setId(existing.getId());
            when(fornecedorRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(fornecedorRepository.saveAndFlush(existing)).thenReturn(existing);

            FornecedorResponseDTO response = fornecedorService.update(input);

            assertThat(response.getLegalName()).isEqualTo(FornecedorFixture.OUTROS_LEGAL_NAME);
            assertThat(response.getCnpjCpf()).isEqualTo(FornecedorFixture.OUTROS_CNPJ);
        }

        @Test
        void shouldThrowNotFoundWhenFornecedorDoesNotExist() {
            FornecedorInputDTO input = FornecedorFixture.validoInput();
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> fornecedorService.update(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(input.getId().toString());
            verify(fornecedorRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolacoesIntoExcecaoNegocio() {
            Fornecedor existing = FornecedorFixture.persistedEntity();
            FornecedorInputDTO input = FornecedorFixture.validoInput();
            input.setId(existing.getId());
            when(fornecedorRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(fornecedorRepository.saveAndFlush(existing)).thenThrow(OperacionalBViolacoes.violacoesOf(new Fornecedor()));

            assertThatThrownBy(() -> fornecedorService.update(input))
                    .isExactlyInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining("A razão social é obrigatória.");
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            Fornecedor existing = FornecedorFixture.persistedEntity();
            FornecedorInputDTO input = FornecedorFixture.validoInput();
            input.setId(existing.getId());
            when(fornecedorRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(fornecedorRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("too long"));

            assertThatThrownBy(() -> fornecedorService.update(input))
                    .isExactlyInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao atualizar fornecedor")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListAtivoFornecedors() {
            when(fornecedorRepository.findAllByAtivoTrue()).thenReturn(List.of(FornecedorFixture.persistedEntity()));

            assertThat(fornecedorService.findAll()).hasSize(1);
        }

        @Test
        void shouldBuscaByLegalName() {
            when(fornecedorRepository.findByLegalNameContainingIgnoreCaseAndAtivoTrue("petro"))
                    .thenReturn(List.of(FornecedorFixture.persistedEntity()));

            assertThat(fornecedorService.findByDescricao("petro"))
                    .extracting(FornecedorResponseDTO::getLegalName)
                    .containsExactly(FornecedorFixture.LEGAL_NAME);
        }

        @Test
        void shouldFallbackToFindAllWhenDescricaoIsVazio() {
            when(fornecedorRepository.findAllByAtivoTrue()).thenReturn(List.of());

            assertThat(fornecedorService.findByDescricao(" ")).isEmpty();
            verify(fornecedorRepository, never()).findByLegalNameContainingIgnoreCaseAndAtivoTrue(any());
        }

        @Test
        void shouldFindById() {
            Fornecedor existing = FornecedorFixture.persistedEntity();
            when(fornecedorRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);

            assertThat(fornecedorService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> fornecedorService.find(id))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(id.toString());
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateFornecedorAndStampExcluidoEm() {
            Fornecedor existing = FornecedorFixture.persistedEntity();
            when(fornecedorRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            fornecedorService.delete(existing.getId());

            ArgumentCaptor<Fornecedor> captor = ArgumentCaptor.forClass(Fornecedor.class);
            verify(fornecedorRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getAtivo()).isFalse();
            assertThat(captor.getValue().getExcluidoEm()).isNotNull();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownFornecedor() {
            UUID id = UUID.randomUUID();
            when(fornecedorRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> fornecedorService.delete(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(fornecedorRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    class ChangedSinceSincronizacao {

        @Test
        void shouldReturnFornecedorsChangedSinceUltimaSincronizacao() {
            LocalDateTime ultimaSincronizacao = LocalDateTime.of(2026, 10, 1, 8, 0);
            when(sincronizacaoService.findByTabelaName(TABLE_NAME)).thenReturn(sincronizacaoAt(ultimaSincronizacao));
            when(fornecedorRepository.findAllChangedSince(ultimaSincronizacao)).thenReturn(List.of(FornecedorFixture.persistedEntity()));

            List<FornecedorResponseDTO> changed = fornecedorService.buscarAlteradosDesdeSincronizacao(TABLE_NAME);

            assertThat(changed)
                    .extracting(FornecedorResponseDTO::getLegalName)
                    .containsExactly(FornecedorFixture.LEGAL_NAME);
        }

        @Test
        void shouldUseDefaultStartDateWhenNeverSincronizacaod() {
            when(sincronizacaoService.findByTabelaName(TABLE_NAME)).thenReturn(sincronizacaoAt(null));
            when(fornecedorRepository.findAllChangedSince(DEFAULT_SINCE)).thenReturn(List.of());

            assertThat(fornecedorService.buscarAlteradosDesdeSincronizacao(TABLE_NAME)).isEmpty();
            verify(fornecedorRepository).findAllChangedSince(DEFAULT_SINCE);
        }

        @Test
        void shouldPropagateNotFoundWhenSincronizacaoRowIsMissing() {
            when(sincronizacaoService.findByTabelaName(TABLE_NAME)).thenThrow(new ExcecaoNegocioNaoEncontrado("missing"));

            assertThatThrownBy(() -> fornecedorService.buscarAlteradosDesdeSincronizacao(TABLE_NAME))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(fornecedorRepository, never()).findAllChangedSince(any());
        }

        private Sincronizacao sincronizacaoAt(LocalDateTime ultimaSincronizacao) {
            Sincronizacao sincronizacao = new Sincronizacao();
            sincronizacao.setUltimaSincronizacao(ultimaSincronizacao);
            return sincronizacao;
        }
    }
}
