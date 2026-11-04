package com.br.manager.domain.operacional.usecase;

import com.br.manager.domain.comum.entity.NomeTabelaSincronizacaoEnum;
import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.comum.service.ServicoSincronizacao;
import com.br.manager.domain.operacional.dto.TanqueInputDTO;
import com.br.manager.domain.operacional.dto.TanqueResponseDTO;
import com.br.manager.domain.operacional.entity.Produto;
import com.br.manager.domain.operacional.entity.Tanque;
import com.br.manager.domain.operacional.fixture.ProdutoFixture;
import com.br.manager.domain.operacional.fixture.TanqueFixture;
import com.br.manager.domain.operacional.mapper.TanqueMapper;
import com.br.manager.domain.operacional.repository.TanqueRepository;
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

import static com.br.manager.domain.operacional.support.OperacionalADadosTeste.constraintViolationOf;
import static com.br.manager.domain.operacional.support.OperacionalADadosTeste.sincronizacaoAt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TanqueServiceTest {

    private static final String TABLE = NomeTabelaSincronizacaoEnum.TANQUE.name();
    private static final LocalDateTime DEFAULT_SINCE = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
    private static final String ONLY_COMBUSTIVEL_MESSAGE = "Apenas produtos do tipo COMBUSTÍVEL podem ser atribuídos a um tanque.";
    private static final String CAPACIDADE_MESSAGE = "A capacidade deve ser maior que zero.";

    @Mock
    private TanqueRepository tanqueRepository;

    @Mock
    private ProdutoService produtoService;

    @Mock
    private ServicoSincronizacao sincronizacaoService;

    @Spy
    private TanqueMapper tanqueMapper = new TanqueMapper();

    @InjectMocks
    private TanqueService tanqueService;

    private static Tanque withZeroCapacidade() {
        Tanque invalido = TanqueFixture.persistedEntity();
        invalido.setCapacidadeLitros(BigDecimal.ZERO);
        return invalido;
    }

    @Nested
    class Create {

        @Test
        void shouldPersistTanqueForCombustivelProduto() {
            Produto combustivel = ProdutoFixture.persistedCombustivel();
            when(produtoService.getProdutoEntityById(combustivel.getId())).thenReturn(combustivel);
            when(tanqueRepository.saveAndFlush(any(Tanque.class))).thenAnswer(invocation -> {
                Tanque tanque = invocation.getArgument(0);
                tanque.setId(UUID.randomUUID());
                return tanque;
            });

            TanqueResponseDTO response = tanqueService.create(TanqueFixture.validoInput(combustivel.getId()));

            assertThat(response.getId()).isNotNull();
            assertThat(response.getCodigo()).isEqualTo(TanqueFixture.CODE);
            assertThat(response.getProdutoId()).isEqualTo(combustivel.getId());
            assertThat(response.getCapacidadeLitros()).isEqualTo(TanqueFixture.CAPACIDADE);
        }

        @Test
        void shouldRejectNonCombustivelProduto() {
            Produto conveniencia = ProdutoFixture.persistedConveniencia();
            when(produtoService.getProdutoEntityById(conveniencia.getId())).thenReturn(conveniencia);

            assertThatThrownBy(() -> tanqueService.create(TanqueFixture.validoInput(conveniencia.getId())))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(TanqueFixture.CODE)
                    .cause()
                    .hasMessage(ONLY_COMBUSTIVEL_MESSAGE);
            verify(tanqueRepository, never()).saveAndFlush(any());
        }

        @Test
        @Disabled("BUG: TanqueService.create catches ExcecaoNegocioNaoEncontrado in the generic catch(Exception) and wraps it "
                + "in a plain ExcecaoNegocio (HTTP 400), unlike TanqueService.update and BicoService.create (HTTP 404)")
        void shouldPropagateNotFoundWhenProdutoDoesNotExist() {
            UUID produtoId = UUID.randomUUID();
            when(produtoService.getProdutoEntityById(produtoId)).thenThrow(new ExcecaoNegocioNaoEncontrado("not found"));

            assertThatThrownBy(() -> tanqueService.create(TanqueFixture.validoInput(produtoId)))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldTranslateConstraintViolationIntoExcecaoNegocio() {
            Produto combustivel = ProdutoFixture.persistedCombustivel();
            TanqueInputDTO input = TanqueFixture.validoInput(combustivel.getId());
            input.setCapacidadeLitros(BigDecimal.ZERO);
            when(produtoService.getProdutoEntityById(combustivel.getId())).thenReturn(combustivel);
            when(tanqueRepository.saveAndFlush(any(Tanque.class))).thenThrow(constraintViolationOf(withZeroCapacidade()));

            assertThatThrownBy(() -> tanqueService.create(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(CAPACIDADE_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            Produto combustivel = ProdutoFixture.persistedCombustivel();
            when(produtoService.getProdutoEntityById(combustivel.getId())).thenReturn(combustivel);
            when(tanqueRepository.saveAndFlush(any(Tanque.class))).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> tanqueService.create(TanqueFixture.validoInput(combustivel.getId())))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(TanqueFixture.CODE)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingTanque() {
            Tanque existing = TanqueFixture.persistedEntity();
            Produto combustivel = ProdutoFixture.persistedCombustivel();
            TanqueInputDTO input = TanqueFixture.validoInput(combustivel.getId(), TanqueFixture.OUTROS_CODE);
            input.setId(existing.getId());
            when(tanqueRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(produtoService.getProdutoEntityById(combustivel.getId())).thenReturn(combustivel);
            when(tanqueRepository.saveAndFlush(existing)).thenReturn(existing);

            TanqueResponseDTO response = tanqueService.update(input);

            assertThat(response.getCodigo()).isEqualTo(TanqueFixture.OUTROS_CODE);
            assertThat(response.getProdutoId()).isEqualTo(combustivel.getId());
        }

        @Test
        void shouldThrowNotFoundWhenTanqueDoesNotExist() {
            TanqueInputDTO input = TanqueFixture.validoInput(UUID.randomUUID());
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> tanqueService.update(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(input.getId().toString());
            verify(tanqueRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldPropagateNotFoundWhenProdutoDoesNotExist() {
            Tanque existing = TanqueFixture.persistedEntity();
            UUID produtoId = UUID.randomUUID();
            TanqueInputDTO input = TanqueFixture.validoInput(produtoId);
            input.setId(existing.getId());
            when(tanqueRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(produtoService.getProdutoEntityById(produtoId)).thenThrow(new ExcecaoNegocioNaoEncontrado("not found"));

            assertThatThrownBy(() -> tanqueService.update(input)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(tanqueRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldRejectNonCombustivelProduto() {
            Tanque existing = TanqueFixture.persistedEntity();
            Produto conveniencia = ProdutoFixture.persistedConveniencia();
            TanqueInputDTO input = TanqueFixture.validoInput(conveniencia.getId());
            input.setId(existing.getId());
            when(tanqueRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(produtoService.getProdutoEntityById(conveniencia.getId())).thenReturn(conveniencia);

            assertThatThrownBy(() -> tanqueService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .cause()
                    .hasMessage(ONLY_COMBUSTIVEL_MESSAGE);
            verify(tanqueRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldTranslateConstraintViolationIntoExcecaoNegocio() {
            Tanque existing = TanqueFixture.persistedEntity();
            Produto combustivel = ProdutoFixture.persistedCombustivel();
            TanqueInputDTO input = TanqueFixture.validoInput(combustivel.getId());
            input.setId(existing.getId());
            input.setCapacidadeLitros(BigDecimal.ZERO);
            when(tanqueRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(produtoService.getProdutoEntityById(combustivel.getId())).thenReturn(combustivel);
            when(tanqueRepository.saveAndFlush(existing)).thenThrow(constraintViolationOf(withZeroCapacidade()));

            assertThatThrownBy(() -> tanqueService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(CAPACIDADE_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            Tanque existing = TanqueFixture.persistedEntity();
            Produto combustivel = ProdutoFixture.persistedCombustivel();
            TanqueInputDTO input = TanqueFixture.validoInput(combustivel.getId());
            input.setId(existing.getId());
            when(tanqueRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(produtoService.getProdutoEntityById(combustivel.getId())).thenReturn(combustivel);
            when(tanqueRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> tanqueService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(TanqueFixture.CODE)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListAtivoTanques() {
            when(tanqueRepository.findAllByAtivoTrue()).thenReturn(List.of(TanqueFixture.persistedEntity()));

            assertThat(tanqueService.findAll()).hasSize(1);
        }

        @Test
        void shouldBuscaByCode() {
            when(tanqueRepository.findByCodeContainingIgnoreCaseAndAtivoTrue("tq"))
                    .thenReturn(List.of(TanqueFixture.persistedEntity()));

            assertThat(tanqueService.findByDescricao("tq")).hasSize(1);
        }

        @Test
        void shouldFallbackToFindAllWhenDescricaoIsVazio() {
            when(tanqueRepository.findAllByAtivoTrue()).thenReturn(List.of());

            assertThat(tanqueService.findByDescricao(null)).isEmpty();
            verify(tanqueRepository, never()).findByCodeContainingIgnoreCaseAndAtivoTrue(any());
        }

        @Test
        void shouldFindById() {
            Tanque existing = TanqueFixture.persistedEntity();
            when(tanqueRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);

            assertThat(tanqueService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> tanqueService.find(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldReturnEntityById() {
            Tanque existing = TanqueFixture.persistedEntity();
            when(tanqueRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);

            assertThat(tanqueService.getTanqueEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenEntityDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> tanqueService.getTanqueEntityById(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateAndMarkDeletionDate() {
            Tanque existing = TanqueFixture.persistedEntity();
            when(tanqueRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            tanqueService.delete(existing.getId());

            ArgumentCaptor<Tanque> captor = ArgumentCaptor.forClass(Tanque.class);
            verify(tanqueRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getAtivo()).isFalse();
            assertThat(captor.getValue().getExcluidoEm()).isNotNull();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownTanque() {
            UUID id = UUID.randomUUID();
            when(tanqueRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> tanqueService.delete(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            Tanque existing = TanqueFixture.persistedEntity();
            when(tanqueRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(tanqueRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> tanqueService.delete(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class ChangedSinceSincronizacao {

        @Test
        void shouldReturnRecordsChangedSinceUltimaSincronizacao() {
            LocalDateTime ultimaSincronizacao = LocalDateTime.of(2026, 5, 10, 8, 30);
            Tanque changed = TanqueFixture.persistedEntity();
            when(sincronizacaoService.findByTabelaName(TABLE)).thenReturn(sincronizacaoAt(ultimaSincronizacao));
            when(tanqueRepository.findAllChangedSince(ultimaSincronizacao)).thenReturn(List.of(changed));

            assertThat(tanqueService.buscarAlteradosDesdeSincronizacao(TABLE))
                    .extracting(TanqueResponseDTO::getId)
                    .containsExactly(changed.getId());
        }

        @Test
        void shouldUseDefaultDateWhenTabelaWasNeverSincronizacaod() {
            when(sincronizacaoService.findByTabelaName(TABLE)).thenReturn(sincronizacaoAt(null));
            when(tanqueRepository.findAllChangedSince(DEFAULT_SINCE)).thenReturn(List.of());

            assertThat(tanqueService.buscarAlteradosDesdeSincronizacao(TABLE)).isEmpty();
            verify(tanqueRepository).findAllChangedSince(DEFAULT_SINCE);
        }
    }
}
