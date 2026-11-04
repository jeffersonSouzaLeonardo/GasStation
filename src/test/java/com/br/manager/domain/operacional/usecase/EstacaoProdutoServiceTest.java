package com.br.manager.domain.operacional.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.operacional.dto.EstacaoProdutoInputDTO;
import com.br.manager.domain.operacional.dto.EstacaoProdutoResponseDTO;
import com.br.manager.domain.operacional.entity.EstacaoProduto;
import com.br.manager.domain.operacional.fixture.EstacaoProdutoFixture;
import com.br.manager.domain.operacional.mapper.EstacaoProdutoMapper;
import com.br.manager.domain.operacional.repository.EstacaoProdutoRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.br.manager.domain.operacional.support.OperacionalADadosTeste.constraintViolationOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstacaoProdutoServiceTest {

    private static final String PRODUTO_OBRIGATORIO = "O ID do produto é obrigatório.";

    @Mock
    private EstacaoProdutoRepository estacaoProdutoRepository;

    @Spy
    private EstacaoProdutoMapper estacaoProdutoMapper = new EstacaoProdutoMapper();

    @InjectMocks
    private EstacaoProdutoService estacaoProdutoService;

    private static EstacaoProduto withoutProduto() {
        EstacaoProduto invalido = EstacaoProdutoFixture.persistedEntity();
        invalido.setProdutoId(null);
        return invalido;
    }

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnEstacaoProduto() {
            UUID produtoId = UUID.randomUUID();
            when(estacaoProdutoRepository.saveAndFlush(any(EstacaoProduto.class))).thenAnswer(invocation -> {
                EstacaoProduto entity = invocation.getArgument(0);
                entity.setId(UUID.randomUUID());
                return entity;
            });

            EstacaoProdutoResponseDTO response = estacaoProdutoService.create(EstacaoProdutoFixture.validoInput(produtoId));

            assertThat(response.getId()).isNotNull();
            assertThat(response.getProdutoId()).isEqualTo(produtoId);
            assertThat(response.getEstoqueMinimo()).isEqualTo(EstacaoProdutoFixture.MINIMO_ESTOQUE);
            assertThat(response.getPontoReposicao()).isEqualTo(EstacaoProdutoFixture.PONTO_REPOSICAO);
        }

        @Test
        void shouldTranslateConstraintViolationIntoExcecaoNegocio() {
            when(estacaoProdutoRepository.saveAndFlush(any(EstacaoProduto.class))).thenThrow(constraintViolationOf(withoutProduto()));

            assertThatThrownBy(() -> estacaoProdutoService.create(EstacaoProdutoFixture.validoInput(null)))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(PRODUTO_OBRIGATORIO);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            UUID produtoId = UUID.randomUUID();
            when(estacaoProdutoRepository.saveAndFlush(any(EstacaoProduto.class)))
                    .thenThrow(new DataIntegrityViolationException("fk produto"));

            assertThatThrownBy(() -> estacaoProdutoService.create(EstacaoProdutoFixture.validoInput(produtoId)))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(produtoId.toString())
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingEstacaoProduto() {
            EstacaoProduto existing = EstacaoProdutoFixture.persistedEntity();
            EstacaoProdutoInputDTO input = EstacaoProdutoFixture.validoInput(existing.getProdutoId());
            input.setId(existing.getId());
            input.setPontoReposicao(BigDecimal.TEN);
            when(estacaoProdutoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(estacaoProdutoRepository.saveAndFlush(existing)).thenReturn(existing);

            EstacaoProdutoResponseDTO response = estacaoProdutoService.update(input);

            assertThat(response.getPontoReposicao()).isEqualTo(BigDecimal.TEN);
        }

        @Test
        void shouldThrowNotFoundWhenEstacaoProdutoDoesNotExist() {
            EstacaoProdutoInputDTO input = EstacaoProdutoFixture.validoInput(UUID.randomUUID());
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> estacaoProdutoService.update(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(input.getId().toString());
            verify(estacaoProdutoRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldTranslateConstraintViolationIntoExcecaoNegocio() {
            EstacaoProduto existing = EstacaoProdutoFixture.persistedEntity();
            EstacaoProdutoInputDTO input = EstacaoProdutoFixture.validoInput(null);
            input.setId(existing.getId());
            when(estacaoProdutoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(estacaoProdutoRepository.saveAndFlush(existing)).thenThrow(constraintViolationOf(withoutProduto()));

            assertThatThrownBy(() -> estacaoProdutoService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(PRODUTO_OBRIGATORIO);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            EstacaoProduto existing = EstacaoProdutoFixture.persistedEntity();
            EstacaoProdutoInputDTO input = EstacaoProdutoFixture.validoInput(existing.getProdutoId());
            input.setId(existing.getId());
            when(estacaoProdutoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(estacaoProdutoRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> estacaoProdutoService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListAtivoEstacaoProdutos() {
            when(estacaoProdutoRepository.findAllByAtivoTrue()).thenReturn(List.of(EstacaoProdutoFixture.persistedEntity()));

            assertThat(estacaoProdutoService.findAll()).hasSize(1);
        }

        @Test
        void shouldBuscaByProdutoIdWhenDescricaoIsUuid() {
            EstacaoProduto existing = EstacaoProdutoFixture.persistedEntity();
            when(estacaoProdutoRepository.findByProdutoIdAndAtivoTrue(existing.getProdutoId())).thenReturn(List.of(existing));

            assertThat(estacaoProdutoService.findByDescricao(existing.getProdutoId().toString()))
                    .extracting(EstacaoProdutoResponseDTO::getId)
                    .containsExactly(existing.getId());
        }

        @Test
        void shouldReturnEmptyWhenDescricaoIsNotUuid() {
            assertThat(estacaoProdutoService.findByDescricao("gasolina")).isEmpty();
            verify(estacaoProdutoRepository, never()).findByProdutoIdAndAtivoTrue(any());
        }

        @Test
        void shouldFallbackToFindAllWhenDescricaoIsVazio() {
            when(estacaoProdutoRepository.findAllByAtivoTrue()).thenReturn(List.of());

            assertThat(estacaoProdutoService.findByDescricao("")).isEmpty();
            verify(estacaoProdutoRepository).findAllByAtivoTrue();
        }

        @Test
        void shouldFindById() {
            EstacaoProduto existing = EstacaoProdutoFixture.persistedEntity();
            when(estacaoProdutoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);

            assertThat(estacaoProdutoService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> estacaoProdutoService.find(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldReturnEntityById() {
            EstacaoProduto existing = EstacaoProdutoFixture.persistedEntity();
            when(estacaoProdutoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);

            assertThat(estacaoProdutoService.getEstacaoProdutoEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenEntityDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> estacaoProdutoService.getEstacaoProdutoEntityById(id))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateEstacaoProduto() {
            EstacaoProduto existing = EstacaoProdutoFixture.persistedEntity();
            when(estacaoProdutoRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            estacaoProdutoService.delete(existing.getId());

            ArgumentCaptor<EstacaoProduto> captor = ArgumentCaptor.forClass(EstacaoProduto.class);
            verify(estacaoProdutoRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getAtivo()).isFalse();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownEstacaoProduto() {
            UUID id = UUID.randomUUID();
            when(estacaoProdutoRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> estacaoProdutoService.delete(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            EstacaoProduto existing = EstacaoProdutoFixture.persistedEntity();
            when(estacaoProdutoRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(estacaoProdutoRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> estacaoProdutoService.delete(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }
}
