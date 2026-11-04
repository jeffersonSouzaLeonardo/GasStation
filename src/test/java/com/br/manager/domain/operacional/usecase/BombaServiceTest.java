package com.br.manager.domain.operacional.usecase;

import com.br.manager.domain.comum.entity.NomeTabelaSincronizacaoEnum;
import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.comum.service.ServicoSincronizacao;
import com.br.manager.domain.operacional.dto.BombaInputDTO;
import com.br.manager.domain.operacional.dto.BombaResponseDTO;
import com.br.manager.domain.operacional.entity.Bomba;
import com.br.manager.domain.operacional.fixture.BombaFixture;
import com.br.manager.domain.operacional.mapper.BombaMapper;
import com.br.manager.domain.operacional.repository.BombaRepository;
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
class BombaServiceTest {

    private static final String TABLE = NomeTabelaSincronizacaoEnum.BOMBA.name();
    private static final LocalDateTime DEFAULT_SINCE = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
    private static final String CODE_SIZE_MESSAGE = "O código da bomba deve ter entre 1 e 50 caracteres.";

    @Mock
    private BombaRepository bombaRepository;

    @Mock
    private ServicoSincronizacao sincronizacaoService;

    @Spy
    private BombaMapper bombaMapper = new BombaMapper();

    @InjectMocks
    private BombaService bombaService;

    private static Bomba withTooLongCode() {
        Bomba invalido = BombaFixture.persistedEntity();
        invalido.setCodigo(BombaFixture.TOO_LONG_CODE);
        return invalido;
    }

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnBomba() {
            when(bombaRepository.saveAndFlush(any(Bomba.class))).thenAnswer(invocation -> {
                Bomba bomba = invocation.getArgument(0);
                bomba.setId(UUID.randomUUID());
                return bomba;
            });

            BombaResponseDTO response = bombaService.create(BombaFixture.validoInput());

            assertThat(response.getId()).isNotNull();
            assertThat(response.getCodigo()).isEqualTo(BombaFixture.CODE);
            assertThat(response.getLocalizacao()).isEqualTo(BombaFixture.LOCATION);
        }

        @Test
        void shouldTranslateConstraintViolationIntoExcecaoNegocio() {
            when(bombaRepository.saveAndFlush(any(Bomba.class))).thenThrow(constraintViolationOf(withTooLongCode()));

            assertThatThrownBy(() -> bombaService.create(BombaFixture.validoInput(BombaFixture.TOO_LONG_CODE)))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(CODE_SIZE_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            when(bombaRepository.saveAndFlush(any(Bomba.class))).thenThrow(new DataIntegrityViolationException("db"));

            assertThatThrownBy(() -> bombaService.create(BombaFixture.validoInput()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(BombaFixture.CODE)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingBomba() {
            Bomba existing = BombaFixture.persistedEntity();
            BombaInputDTO input = BombaFixture.validoInput(BombaFixture.OUTROS_CODE);
            input.setId(existing.getId());
            when(bombaRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(bombaRepository.saveAndFlush(existing)).thenReturn(existing);

            BombaResponseDTO response = bombaService.update(input);

            assertThat(response.getCodigo()).isEqualTo(BombaFixture.OUTROS_CODE);
        }

        @Test
        void shouldThrowNotFoundWhenBombaDoesNotExist() {
            BombaInputDTO input = BombaFixture.validoInput();
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> bombaService.update(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(input.getId().toString());
            verify(bombaRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldTranslateConstraintViolationIntoExcecaoNegocio() {
            Bomba existing = BombaFixture.persistedEntity();
            BombaInputDTO input = BombaFixture.validoInput(BombaFixture.TOO_LONG_CODE);
            input.setId(existing.getId());
            when(bombaRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(bombaRepository.saveAndFlush(existing)).thenThrow(constraintViolationOf(withTooLongCode()));

            assertThatThrownBy(() -> bombaService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(CODE_SIZE_MESSAGE);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            Bomba existing = BombaFixture.persistedEntity();
            BombaInputDTO input = BombaFixture.validoInput();
            input.setId(existing.getId());
            when(bombaRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(bombaRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("db"));

            assertThatThrownBy(() -> bombaService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(BombaFixture.CODE)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListAtivoBombas() {
            when(bombaRepository.findAllByAtivoTrue()).thenReturn(List.of(BombaFixture.persistedEntity()));

            assertThat(bombaService.findAll()).hasSize(1);
        }

        @Test
        void shouldBuscaByCode() {
            when(bombaRepository.findByCodeContainingIgnoreCaseAndAtivoTrue("bomba"))
                    .thenReturn(List.of(BombaFixture.persistedEntity()));

            assertThat(bombaService.findByDescricao("bomba")).hasSize(1);
        }

        @Test
        void shouldFallbackToFindAllWhenDescricaoIsVazio() {
            when(bombaRepository.findAllByAtivoTrue()).thenReturn(List.of());

            assertThat(bombaService.findByDescricao(" ")).isEmpty();
            verify(bombaRepository, never()).findByCodeContainingIgnoreCaseAndAtivoTrue(any());
        }

        @Test
        void shouldFindById() {
            Bomba existing = BombaFixture.persistedEntity();
            when(bombaRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);

            assertThat(bombaService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> bombaService.find(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldReturnEntityById() {
            Bomba existing = BombaFixture.persistedEntity();
            when(bombaRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);

            assertThat(bombaService.getBombaEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenEntityDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> bombaService.getBombaEntityById(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateAndMarkDeletionDate() {
            Bomba existing = BombaFixture.persistedEntity();
            when(bombaRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            bombaService.delete(existing.getId());

            ArgumentCaptor<Bomba> captor = ArgumentCaptor.forClass(Bomba.class);
            verify(bombaRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getAtivo()).isFalse();
            assertThat(captor.getValue().getExcluidoEm()).isNotNull();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownBomba() {
            UUID id = UUID.randomUUID();
            when(bombaRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bombaService.delete(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            Bomba existing = BombaFixture.persistedEntity();
            when(bombaRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(bombaRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> bombaService.delete(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class ChangedSinceSincronizacao {

        @Test
        void shouldReturnRecordsChangedSinceUltimaSincronizacao() {
            LocalDateTime ultimaSincronizacao = LocalDateTime.of(2026, 5, 10, 8, 30);
            Bomba changed = BombaFixture.persistedEntity();
            when(sincronizacaoService.findByTabelaName(TABLE)).thenReturn(sincronizacaoAt(ultimaSincronizacao));
            when(bombaRepository.findAllChangedSince(ultimaSincronizacao)).thenReturn(List.of(changed));

            assertThat(bombaService.buscarAlteradosDesdeSincronizacao(TABLE))
                    .extracting(BombaResponseDTO::getId)
                    .containsExactly(changed.getId());
        }

        @Test
        void shouldUseDefaultDateWhenTabelaWasNeverSincronizacaod() {
            when(sincronizacaoService.findByTabelaName(TABLE)).thenReturn(sincronizacaoAt(null));
            when(bombaRepository.findAllChangedSince(DEFAULT_SINCE)).thenReturn(List.of());

            assertThat(bombaService.buscarAlteradosDesdeSincronizacao(TABLE)).isEmpty();
            verify(bombaRepository).findAllChangedSince(DEFAULT_SINCE);
        }
    }
}
