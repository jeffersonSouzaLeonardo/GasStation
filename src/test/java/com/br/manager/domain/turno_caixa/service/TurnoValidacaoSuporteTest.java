package com.br.manager.domain.turno_caixa.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.operacional.entity.Bico;
import com.br.manager.domain.operacional.entity.Bomba;
import com.br.manager.domain.operacional.repository.BicoRepository;
import com.br.manager.domain.operacional.repository.BombaRepository;
import com.br.manager.domain.organizacao.entity.Estacao;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.organizacao.repository.EstacaoRepository;
import com.br.manager.domain.organizacao.repository.UsuarioRepository;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.fixture.TurnoFixture;
import com.br.manager.domain.turno_caixa.repository.TurnoRepository;
import com.br.manager.domain.turno_caixa.support.TurnoATestData;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class TurnoValidacaoSuporteTest {

    private static final String FIELD = "caixeiroUsuarioId";

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private EstacaoRepository estacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private BombaRepository bombaRepository;

    @Mock
    private BicoRepository bicoRepository;

    @InjectMocks
    private SuporteValidacaoTurno validacaoSuporte;

    @Nested
    class GetObrigatorioTurno {

        @Test
        void shouldReturnTurnoWhenFound() {
            Turno turno = TurnoFixture.abertoTurno();
            when(turnoRepository.findById(turno.getId())).thenReturn(Optional.of(turno));

            assertThat(validacaoSuporte.getObrigatorioTurno(turno.getId())).isSameAs(turno);
        }

        @Test
        void shouldThrowNotFoundWhenTurnoDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(turnoRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> validacaoSuporte.getObrigatorioTurno(id))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(id.toString());
        }
    }

    @Nested
    class EnsureTurnoIsAberto {

        @Test
        void shouldAcceptAbertoTurno() {
            assertThatCode(() -> validacaoSuporte.ensureTurnoIsAberto(TurnoFixture.abertoTurno()))
                    .doesNotThrowAnyException();
        }

        @Test
        void shouldRejectFechadoTurno() {
            Turno turno = TurnoFixture.fechadoTurno();

            assertThatThrownBy(() -> validacaoSuporte.ensureTurnoIsAberto(turno))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .isNotInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(turno.getId().toString());
        }
    }

    @Nested
    class GetObrigatorioEstacao {

        @Test
        void shouldReturnAtivoEstacao() {
            Estacao estacao = TurnoATestData.estacao(UUID.randomUUID(), true);
            when(estacaoRepository.findById(estacao.getId())).thenReturn(Optional.of(estacao));

            assertThat(validacaoSuporte.getObrigatorioEstacao(estacao.getId())).isSameAs(estacao);
        }

        @Test
        void shouldThrowNotFoundWhenEstacaoDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(estacaoRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> validacaoSuporte.getObrigatorioEstacao(id))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(id.toString());
        }

        @Test
        void shouldThrowNotFoundWhenEstacaoIsInativo() {
            Estacao estacao = TurnoATestData.estacao(UUID.randomUUID(), false);
            when(estacaoRepository.findById(estacao.getId())).thenReturn(Optional.of(estacao));

            assertThatThrownBy(() -> validacaoSuporte.getObrigatorioEstacao(estacao.getId()))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }
    }

    @Nested
    class GetObrigatorioUsuario {

        @Test
        void shouldReturnAtivoUsuario() {
            Usuario usuario = TurnoATestData.usuario(UUID.randomUUID(), true);
            when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));

            assertThat(validacaoSuporte.getObrigatorioUsuario(usuario.getId(), FIELD)).isSameAs(usuario);
        }

        @Test
        void shouldThrowNotFoundMentioningFieldWhenUsuarioDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> validacaoSuporte.getObrigatorioUsuario(id, FIELD))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(id.toString())
                    .hasMessageContaining(FIELD);
        }

        @Test
        void shouldUseDefaultFieldNameWhenFieldIsVazio() {
            UUID id = UUID.randomUUID();
            when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> validacaoSuporte.getObrigatorioUsuario(id, " "))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageEndingWith("no campo usuário");
        }

        @Test
        void shouldUseDefaultFieldNameWhenFieldIsNull() {
            UUID id = UUID.randomUUID();
            when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> validacaoSuporte.getObrigatorioUsuario(id, null))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageEndingWith("no campo usuário");
        }

        @Test
        void shouldThrowNotFoundWhenUsuarioIsInativo() {
            Usuario usuario = TurnoATestData.usuario(UUID.randomUUID(), false);
            when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));

            assertThatThrownBy(() -> validacaoSuporte.getObrigatorioUsuario(usuario.getId(), FIELD))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(FIELD);
        }
    }

    @Nested
    class FindBicosByEstacaoId {

        @Test
        void shouldReturnEmptyWhenThereAreNoAtivoBombas() {
            when(bombaRepository.findAllByAtivoTrue()).thenReturn(List.of());

            assertThat(validacaoSuporte.findBicosByEstacaoId(UUID.randomUUID())).isEmpty();
            verify(bicoRepository, never()).findByBombaIdAndAtivoTrue(any());
        }

        @Test
        void shouldCollectAtivoBicosOfAllAtivoBombas() {
            Bomba firstBomba = TurnoATestData.bomba();
            Bomba secondBomba = TurnoATestData.bomba();
            Bico firstBico = TurnoATestData.bico(firstBomba.getId(), TurnoATestData.MEDIDOR_BICO);
            Bico secondBico = TurnoATestData.bico(secondBomba.getId(), TurnoATestData.MEDIDOR_BICO);
            when(bombaRepository.findAllByAtivoTrue()).thenReturn(List.of(firstBomba, secondBomba));
            when(bicoRepository.findByBombaIdAndAtivoTrue(firstBomba.getId())).thenReturn(List.of(firstBico));
            when(bicoRepository.findByBombaIdAndAtivoTrue(secondBomba.getId())).thenReturn(List.of(secondBico));

            assertThat(validacaoSuporte.findBicosByEstacaoId(UUID.randomUUID()))
                    .containsExactly(firstBico, secondBico);
        }

        @Test
        void shouldNotRepeatBicoReturnedForMoreThanOneBomba() {
            Bomba firstBomba = TurnoATestData.bomba();
            Bomba secondBomba = TurnoATestData.bomba();
            Bico bico = TurnoATestData.bico(firstBomba.getId(), TurnoATestData.MEDIDOR_BICO);
            when(bombaRepository.findAllByAtivoTrue()).thenReturn(List.of(firstBomba, secondBomba));
            when(bicoRepository.findByBombaIdAndAtivoTrue(firstBomba.getId())).thenReturn(List.of(bico));
            when(bicoRepository.findByBombaIdAndAtivoTrue(secondBomba.getId())).thenReturn(List.of(bico));

            assertThat(validacaoSuporte.findBicosByEstacaoId(UUID.randomUUID())).containsExactly(bico);
        }
    }

    @Nested
    class ValidateBicoBelongsToTurnoEstacao {

        @Test
        void shouldAcceptBicoFoundForEstacao() {
            Bomba bomba = TurnoATestData.bomba();
            Bico bico = TurnoATestData.bico(bomba.getId(), TurnoATestData.MEDIDOR_BICO);
            when(bombaRepository.findAllByAtivoTrue()).thenReturn(List.of(bomba));
            when(bicoRepository.findByBombaIdAndAtivoTrue(bomba.getId())).thenReturn(List.of(bico));

            assertThatCode(() -> validacaoSuporte.validateBicoBelongsToTurnoEstacao(UUID.randomUUID(), bico.getId()))
                    .doesNotThrowAnyException();
        }

        @Test
        void shouldRejectBicoNotFoundForEstacao() {
            UUID estacaoId = UUID.randomUUID();
            UUID bicoId = UUID.randomUUID();
            when(bombaRepository.findAllByAtivoTrue()).thenReturn(List.of());

            assertThatThrownBy(() -> validacaoSuporte.validateBicoBelongsToTurnoEstacao(estacaoId, bicoId))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(bicoId.toString())
                    .hasMessageContaining(estacaoId.toString());
        }
    }
}
