package com.br.manager.domain.operacional.usecase;

import com.br.manager.domain.comum.entity.NomeTabelaSincronizacaoEnum;
import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.comum.service.ServicoSincronizacao;
import com.br.manager.domain.operacional.dto.BicoInputDTO;
import com.br.manager.domain.operacional.dto.BicoResponseDTO;
import com.br.manager.domain.operacional.entity.Bico;
import com.br.manager.domain.operacional.entity.Produto;
import com.br.manager.domain.operacional.fixture.BicoFixture;
import com.br.manager.domain.operacional.fixture.ProdutoFixture;
import com.br.manager.domain.operacional.mapper.BicoMapper;
import com.br.manager.domain.operacional.repository.BicoRepository;
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
class BicoServiceTest {

    private static final String TABLE = NomeTabelaSincronizacaoEnum.BICO.name();
    private static final LocalDateTime DEFAULT_SINCE = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
    private static final String ONLY_COMBUSTIVEL_MESSAGE = "Apenas produtos do tipo COMBUSTÍVEL podem ser vendidos pela bomba.";
    private static final String CODE_OBRIGATORIO_MESSAGE = "Bico codigo é obrigatório.";

    @Mock
    private BicoRepository bicoRepository;

    @Mock
    private ProdutoService produtoService;

    @Mock
    private ServicoSincronizacao sincronizacaoService;

    @Spy
    private BicoMapper bicoMapper = new BicoMapper();

    @InjectMocks
    private BicoService bicoService;

    private static BicoInputDTO inputFor(Produto produto) {
        return BicoFixture.validoInput(UUID.randomUUID(), UUID.randomUUID(), produto.getId());
    }

    private static Bico withoutCode() {
        Bico invalido = BicoFixture.persistedEntity();
        invalido.setCodigo(" ");
        return invalido;
    }

    @Nested
    class Create {

        @Test
        void shouldPersistBicoForCombustivelProduto() {
            Produto combustivel = ProdutoFixture.persistedCombustivel();
            BicoInputDTO input = inputFor(combustivel);
            when(produtoService.getProdutoEntityById(combustivel.getId())).thenReturn(combustivel);
            when(bicoRepository.saveAndFlush(any(Bico.class))).thenAnswer(invocation -> {
                Bico bico = invocation.getArgument(0);
                bico.setId(UUID.randomUUID());
                return bico;
            });

            BicoResponseDTO response = bicoService.create(input);

            assertThat(response.getId()).isNotNull();
            assertThat(response.getCodigo()).isEqualTo(BicoFixture.CODE);
            assertThat(response.getBombaId()).isEqualTo(input.getBombaId());
            assertThat(response.getTanqueId()).isEqualTo(input.getTanqueId());
            assertThat(response.getNumeroMedidor()).isEqualTo(BicoFixture.MEDIDOR_NUMERO);
        }

        @Test
        void shouldRejectNonCombustivelProduto() {
            Produto conveniencia = ProdutoFixture.persistedConveniencia();
            when(produtoService.getProdutoEntityById(conveniencia.getId())).thenReturn(conveniencia);

            assertThatThrownBy(() -> bicoService.create(inputFor(conveniencia)))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage(ONLY_COMBUSTIVEL_MESSAGE);
            verify(bicoRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldPropagateNotFoundWhenProdutoDoesNotExist() {
            BicoInputDTO input = BicoFixture.validoInput();
            when(produtoService.getProdutoEntityById(input.getProdutoId())).thenThrow(new ExcecaoNegocioNaoEncontrado("not found"));

            assertThatThrownBy(() -> bicoService.create(input)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(bicoRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldTranslateConstraintViolationIntoExcecaoNegocio() {
            Produto combustivel = ProdutoFixture.persistedCombustivel();
            when(produtoService.getProdutoEntityById(combustivel.getId())).thenReturn(combustivel);
            when(bicoRepository.saveAndFlush(any(Bico.class))).thenThrow(constraintViolationOf(withoutCode()));

            assertThatThrownBy(() -> bicoService.create(inputFor(combustivel)))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(CODE_OBRIGATORIO_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            Produto combustivel = ProdutoFixture.persistedCombustivel();
            when(produtoService.getProdutoEntityById(combustivel.getId())).thenReturn(combustivel);
            when(bicoRepository.saveAndFlush(any(Bico.class))).thenThrow(new DataIntegrityViolationException("uk"));

            assertThatThrownBy(() -> bicoService.create(inputFor(combustivel)))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(BicoFixture.CODE)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingBico() {
            Bico existing = BicoFixture.persistedEntity();
            Produto combustivel = ProdutoFixture.persistedCombustivel();
            BicoInputDTO input = inputFor(combustivel);
            input.setId(existing.getId());
            input.setCodigo(BicoFixture.OUTROS_CODE);
            when(bicoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(produtoService.getProdutoEntityById(combustivel.getId())).thenReturn(combustivel);
            when(bicoRepository.saveAndFlush(existing)).thenReturn(existing);

            BicoResponseDTO response = bicoService.update(input);

            assertThat(response.getCodigo()).isEqualTo(BicoFixture.OUTROS_CODE);
            assertThat(response.getProdutoId()).isEqualTo(combustivel.getId());
        }

        @Test
        void shouldThrowNotFoundWhenBicoDoesNotExist() {
            BicoInputDTO input = BicoFixture.validoInput();
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> bicoService.update(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(input.getId().toString());
            verify(bicoRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldPropagateNotFoundWhenProdutoDoesNotExist() {
            Bico existing = BicoFixture.persistedEntity();
            BicoInputDTO input = BicoFixture.validoInput();
            input.setId(existing.getId());
            when(bicoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(produtoService.getProdutoEntityById(input.getProdutoId())).thenThrow(new ExcecaoNegocioNaoEncontrado("not found"));

            assertThatThrownBy(() -> bicoService.update(input)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(bicoRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldRejectNonCombustivelProduto() {
            Bico existing = BicoFixture.persistedEntity();
            Produto conveniencia = ProdutoFixture.persistedConveniencia();
            BicoInputDTO input = inputFor(conveniencia);
            input.setId(existing.getId());
            when(bicoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(produtoService.getProdutoEntityById(conveniencia.getId())).thenReturn(conveniencia);

            assertThatThrownBy(() -> bicoService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage(ONLY_COMBUSTIVEL_MESSAGE);
            verify(bicoRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldTranslateConstraintViolationIntoExcecaoNegocio() {
            Bico existing = BicoFixture.persistedEntity();
            Produto combustivel = ProdutoFixture.persistedCombustivel();
            BicoInputDTO input = inputFor(combustivel);
            input.setId(existing.getId());
            when(bicoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(produtoService.getProdutoEntityById(combustivel.getId())).thenReturn(combustivel);
            when(bicoRepository.saveAndFlush(existing)).thenThrow(constraintViolationOf(withoutCode()));

            assertThatThrownBy(() -> bicoService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(CODE_OBRIGATORIO_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            Bico existing = BicoFixture.persistedEntity();
            Produto combustivel = ProdutoFixture.persistedCombustivel();
            BicoInputDTO input = inputFor(combustivel);
            input.setId(existing.getId());
            when(bicoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(produtoService.getProdutoEntityById(combustivel.getId())).thenReturn(combustivel);
            when(bicoRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("uk"));

            assertThatThrownBy(() -> bicoService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListAtivoBicos() {
            when(bicoRepository.findAllByAtivoTrue()).thenReturn(List.of(BicoFixture.persistedEntity()));

            assertThat(bicoService.findAll()).hasSize(1);
        }

        @Test
        void shouldBuscaByCode() {
            when(bicoRepository.findByCodeContainingIgnoreCaseAndAtivoTrue("bico"))
                    .thenReturn(List.of(BicoFixture.persistedEntity()));

            assertThat(bicoService.findByDescricao("bico")).hasSize(1);
        }

        @Test
        void shouldFallbackToFindAllWhenDescricaoIsVazio() {
            when(bicoRepository.findAllByAtivoTrue()).thenReturn(List.of());

            assertThat(bicoService.findByDescricao("")).isEmpty();
            verify(bicoRepository, never()).findByCodeContainingIgnoreCaseAndAtivoTrue(any());
        }

        @Test
        void shouldFindById() {
            Bico existing = BicoFixture.persistedEntity();
            when(bicoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);

            assertThat(bicoService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> bicoService.find(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldReturnEntityById() {
            Bico existing = BicoFixture.persistedEntity();
            when(bicoRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);

            assertThat(bicoService.getBicoEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenEntityDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> bicoService.getBicoEntityById(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateAndMarkDeletionDate() {
            Bico existing = BicoFixture.persistedEntity();
            when(bicoRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            bicoService.delete(existing.getId());

            ArgumentCaptor<Bico> captor = ArgumentCaptor.forClass(Bico.class);
            verify(bicoRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getAtivo()).isFalse();
            assertThat(captor.getValue().getExcluidoEm()).isNotNull();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownBico() {
            UUID id = UUID.randomUUID();
            when(bicoRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bicoService.delete(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            Bico existing = BicoFixture.persistedEntity();
            when(bicoRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(bicoRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> bicoService.delete(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class ChangedSinceSincronizacao {

        @Test
        void shouldReturnRecordsChangedSinceUltimaSincronizacao() {
            LocalDateTime ultimaSincronizacao = LocalDateTime.of(2026, 5, 10, 8, 30);
            Bico changed = BicoFixture.persistedEntity();
            when(sincronizacaoService.findByTabelaName(TABLE)).thenReturn(sincronizacaoAt(ultimaSincronizacao));
            when(bicoRepository.findAllChangedSince(ultimaSincronizacao)).thenReturn(List.of(changed));

            assertThat(bicoService.buscarAlteradosDesdeSincronizacao(TABLE))
                    .extracting(BicoResponseDTO::getId)
                    .containsExactly(changed.getId());
        }

        @Test
        void shouldUseDefaultDateWhenTabelaWasNeverSincronizacaod() {
            when(sincronizacaoService.findByTabelaName(TABLE)).thenReturn(sincronizacaoAt(null));
            when(bicoRepository.findAllChangedSince(DEFAULT_SINCE)).thenReturn(List.of());

            assertThat(bicoService.buscarAlteradosDesdeSincronizacao(TABLE)).isEmpty();
            verify(bicoRepository).findAllChangedSince(DEFAULT_SINCE);
        }
    }
}
