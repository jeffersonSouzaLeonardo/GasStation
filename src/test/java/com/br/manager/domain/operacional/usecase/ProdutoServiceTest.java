package com.br.manager.domain.operacional.usecase;

import com.br.manager.domain.comum.entity.NomeTabelaSincronizacaoEnum;
import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.comum.service.ServicoSincronizacao;
import com.br.manager.domain.operacional.dto.ProdutoInputDTO;
import com.br.manager.domain.operacional.dto.ProdutoResponseDTO;
import com.br.manager.domain.operacional.entity.Produto;
import com.br.manager.domain.operacional.enums.ProdutoTypeEnum;
import com.br.manager.domain.operacional.fixture.ProdutoFixture;
import com.br.manager.domain.operacional.mapper.ProdutoMapper;
import com.br.manager.domain.operacional.repository.ProdutoRepository;
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

import static com.br.manager.domain.operacional.support.OperacionalADadosTeste.constraintViolationOf;
import static com.br.manager.domain.operacional.support.OperacionalADadosTeste.sincronizacaoAt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    private static final String TABLE = NomeTabelaSincronizacaoEnum.PRODUTO.name();
    private static final LocalDateTime DEFAULT_SINCE = LocalDateTime.of(2026, 1, 1, 0, 0, 0);

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ServicoSincronizacao sincronizacaoService;

    @Spy
    private ProdutoMapper produtoMapper = new ProdutoMapper();

    @InjectMocks
    private ProdutoService produtoService;

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnProduto() {
            when(produtoRepository.saveAndFlush(any(Produto.class))).thenAnswer(invocation -> {
                Produto produto = invocation.getArgument(0);
                produto.setId(UUID.randomUUID());
                return produto;
            });

            ProdutoResponseDTO response = produtoService.create(ProdutoFixture.combustivelInput());

            assertThat(response.getId()).isNotNull();
            assertThat(response.getNome()).isEqualTo(ProdutoFixture.COMBUSTIVEL_NAME);
            assertThat(response.getProdutoType()).isEqualTo(ProdutoTypeEnum.COMBUSTIVEL);
        }

        @Test
        void shouldTranslateConstraintViolationIntoExcecaoNegocio() {
            Produto invalido = ProdutoFixture.persistedCombustivel();
            invalido.setNome(null);
            when(produtoRepository.saveAndFlush(any(Produto.class))).thenThrow(constraintViolationOf(invalido));

            assertThatThrownBy(() -> produtoService.create(ProdutoFixture.inputWithoutName()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining("Produto nome é obrigatório.");
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            when(produtoRepository.saveAndFlush(any(Produto.class)))
                    .thenThrow(new DataIntegrityViolationException("db error"));

            assertThatThrownBy(() -> produtoService.create(ProdutoFixture.combustivelInput()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(ProdutoFixture.COMBUSTIVEL_NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingProduto() {
            Produto existing = ProdutoFixture.persistedCombustivel();
            ProdutoInputDTO input = ProdutoFixture.convenienciaInput();
            input.setId(existing.getId());
            when(produtoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(produtoRepository.saveAndFlush(existing)).thenReturn(existing);

            ProdutoResponseDTO response = produtoService.update(input);

            assertThat(response.getId()).isEqualTo(existing.getId());
            assertThat(response.getNome()).isEqualTo(ProdutoFixture.CONVENIENCIA_NAME);
            assertThat(response.getProdutoType()).isEqualTo(ProdutoTypeEnum.CONVENIENCIA);
        }

        @Test
        void shouldThrowNotFoundWhenProdutoDoesNotExist() {
            ProdutoInputDTO input = ProdutoFixture.combustivelInput();
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> produtoService.update(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(input.getId().toString());
            verify(produtoRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldTranslateConstraintViolationIntoExcecaoNegocio() {
            Produto existing = ProdutoFixture.persistedCombustivel();
            ProdutoInputDTO input = ProdutoFixture.inputWithoutName();
            input.setId(existing.getId());
            Produto invalido = ProdutoFixture.persistedCombustivel();
            invalido.setNome(null);
            when(produtoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(produtoRepository.saveAndFlush(existing)).thenThrow(constraintViolationOf(invalido));

            assertThatThrownBy(() -> produtoService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining("Produto nome é obrigatório.");
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            Produto existing = ProdutoFixture.persistedCombustivel();
            ProdutoInputDTO input = ProdutoFixture.combustivelInput();
            input.setId(existing.getId());
            when(produtoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(produtoRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("db error"));

            assertThatThrownBy(() -> produtoService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .isNotInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(ProdutoFixture.COMBUSTIVEL_NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListAtivoProdutos() {
            when(produtoRepository.findAllByAtivoTrue()).thenReturn(List.of(ProdutoFixture.persistedCombustivel()));

            assertThat(produtoService.findAll()).hasSize(1);
        }

        @Test
        void shouldListOnlyCombustivelProdutos() {
            when(produtoRepository.findByProdutoTypeAndAtivoTrue(ProdutoTypeEnum.COMBUSTIVEL))
                    .thenReturn(List.of(ProdutoFixture.persistedCombustivel()));

            assertThat(produtoService.findProdutoTypeCombustivel())
                    .extracting(ProdutoResponseDTO::getProdutoType)
                    .containsExactly(ProdutoTypeEnum.COMBUSTIVEL);
        }

        @Test
        void shouldBuscaByName() {
            when(produtoRepository.findByNameContainingIgnoreCaseAndAtivoTrue("gasolina"))
                    .thenReturn(List.of(ProdutoFixture.persistedCombustivel()));

            assertThat(produtoService.findByDescricao("gasolina")).hasSize(1);
        }

        @Test
        void shouldFallbackToFindAllWhenDescricaoIsVazio() {
            when(produtoRepository.findAllByAtivoTrue()).thenReturn(List.of());

            assertThat(produtoService.findByDescricao(" ")).isEmpty();
            verify(produtoRepository, never()).findByNameContainingIgnoreCaseAndAtivoTrue(any());
        }

        @Test
        void shouldFindById() {
            Produto existing = ProdutoFixture.persistedCombustivel();
            when(produtoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);

            assertThat(produtoService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> produtoService.find(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldReturnEntityById() {
            Produto existing = ProdutoFixture.persistedCombustivel();
            when(produtoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);

            assertThat(produtoService.getProdutoEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenEntityDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> produtoService.getProdutoEntityById(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateAndMarkDeletionDate() {
            Produto existing = ProdutoFixture.persistedCombustivel();
            when(produtoRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            produtoService.delete(existing.getId());

            ArgumentCaptor<Produto> captor = ArgumentCaptor.forClass(Produto.class);
            verify(produtoRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getAtivo()).isFalse();
            assertThat(captor.getValue().getExcluidoEm()).isNotNull();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownProduto() {
            UUID id = UUID.randomUUID();
            when(produtoRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> produtoService.delete(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            Produto existing = ProdutoFixture.persistedCombustivel();
            when(produtoRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(produtoRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> produtoService.delete(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class ChangedSinceSincronizacao {

        @Test
        void shouldReturnRecordsChangedSinceUltimaSincronizacao() {
            LocalDateTime ultimaSincronizacao = LocalDateTime.of(2026, 5, 10, 8, 30);
            Produto changed = ProdutoFixture.persistedCombustivel();
            when(sincronizacaoService.findByTabelaName(TABLE)).thenReturn(sincronizacaoAt(ultimaSincronizacao));
            when(produtoRepository.findAllChangedSince(ultimaSincronizacao)).thenReturn(List.of(changed));

            List<ProdutoResponseDTO> result = produtoService.buscarAlteradosDesdeSincronizacao(TABLE);

            assertThat(result).extracting(ProdutoResponseDTO::getId).containsExactly(changed.getId());
        }

        @Test
        void shouldUseDefaultDateWhenTabelaWasNeverSincronizacaod() {
            when(sincronizacaoService.findByTabelaName(TABLE)).thenReturn(sincronizacaoAt(null));
            when(produtoRepository.findAllChangedSince(DEFAULT_SINCE)).thenReturn(List.of());

            assertThat(produtoService.buscarAlteradosDesdeSincronizacao(TABLE)).isEmpty();
            verify(produtoRepository).findAllChangedSince(DEFAULT_SINCE);
        }
    }
}
