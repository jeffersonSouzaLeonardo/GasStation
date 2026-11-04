package com.br.manager.domain.turno_caixa.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.turno_caixa.dto.SessaoCaixaInputDTO;
import com.br.manager.domain.turno_caixa.dto.SessaoCaixaResponseDTO;
import com.br.manager.domain.turno_caixa.dto.SessaoCaixaSincronizacaoDTO;
import com.br.manager.domain.turno_caixa.entity.SessaoCaixa;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.SessaoCaixaSituacaoEnum;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import com.br.manager.domain.turno_caixa.fixture.SessaoCaixaFixture;
import com.br.manager.domain.turno_caixa.mapper.SessaoCaixaMapper;
import com.br.manager.domain.turno_caixa.repository.SessaoCaixaRepository;
import com.br.manager.domain.turno_caixa.repository.TurnoRepository;
import com.br.manager.domain.turno_caixa.support.TurnoBTestData;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessaoCaixaServiceTest {

    private static final String VIOLATION_MESSAGE = "O fundo de troco é obrigatório.";

    @Mock
    private SessaoCaixaRepository dinheiroSessaoRepository;

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private SuporteValidacaoTurno validacaoSuporte;

    @Spy
    private SessaoCaixaMapper dinheiroSessaoMapper = new SessaoCaixaMapper();

    @InjectMocks
    private SessaoCaixaService dinheiroSessaoService;

    private final Turno abertoTurno = TurnoBTestData.turnoEntity(TurnoSituacaoEnum.ABERTO);

    private void returnSavedSessao() {
        when(dinheiroSessaoRepository.saveAndFlush(any(SessaoCaixa.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private SessaoCaixa captureSavedSessao() {
        ArgumentCaptor<SessaoCaixa> captor = ArgumentCaptor.forClass(SessaoCaixa.class);
        verify(dinheiroSessaoRepository).saveAndFlush(captor.capture());
        return captor.getValue();
    }

    @Nested
    class Aberto {

        private final SessaoCaixaInputDTO input = SessaoCaixaFixture.validoInput(abertoTurno.getId(), UUID.randomUUID());

        @Test
        void shouldAbertoSessaoWithGeneratedIdAbertoSituacaoAndAberturaDate() {
            when(turnoRepository.findByIdAndStatus(abertoTurno.getId(), TurnoSituacaoEnum.ABERTO)).thenReturn(Optional.of(abertoTurno));
            returnSavedSessao();

            SessaoCaixaResponseDTO response = dinheiroSessaoService.open(input);

            SessaoCaixa saved = captureSavedSessao();
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getStatus()).isEqualTo(SessaoCaixaSituacaoEnum.ABERTO);
            assertThat(saved.getOpenedAt()).isNotNull();
            assertThat(saved.getTurno()).isSameAs(abertoTurno);
            assertThat(response.getId()).isEqualTo(saved.getId());
            assertThat(response.getTurnoId()).isEqualTo(abertoTurno.getId());
            assertThat(response.getFundoAbertura()).isEqualTo(SessaoCaixaFixture.FUNDO_ABERTURA);
        }

        @Test
        void shouldValidateCaixeiroUsuario() {
            when(turnoRepository.findByIdAndStatus(abertoTurno.getId(), TurnoSituacaoEnum.ABERTO)).thenReturn(Optional.of(abertoTurno));
            returnSavedSessao();

            dinheiroSessaoService.open(input);

            verify(validacaoSuporte).getObrigatorioUsuario(input.getCaixeiroUsuarioId(), "caixeiroUsuarioId");
        }

        @Test
        void shouldThrowExcecaoNegocioWhenPayloadIsNull() {
            assertThatThrownBy(() -> dinheiroSessaoService.open(null))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining("obrigatório");
            verifyNoInteractions(turnoRepository, dinheiroSessaoRepository);
        }

        @Test
        void shouldThrowExcecaoNegocioWhenTurnoIsNotAberto() {
            when(turnoRepository.findByIdAndStatus(abertoTurno.getId(), TurnoSituacaoEnum.ABERTO)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> dinheiroSessaoService.open(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(abertoTurno.getId().toString());
            verify(dinheiroSessaoRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldPropagateNotFoundWhenCaixeiroUsuarioDoesNotExist() {
            when(turnoRepository.findByIdAndStatus(abertoTurno.getId(), TurnoSituacaoEnum.ABERTO)).thenReturn(Optional.of(abertoTurno));
            when(validacaoSuporte.getObrigatorioUsuario(input.getCaixeiroUsuarioId(), "caixeiroUsuarioId"))
                    .thenThrow(new ExcecaoNegocioNaoEncontrado("usuario"));

            assertThatThrownBy(() -> dinheiroSessaoService.open(input)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(dinheiroSessaoRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldThrowExcecaoNegocioWhenCaixeiroAlreadyHasAbertoSessao() {
            when(turnoRepository.findByIdAndStatus(abertoTurno.getId(), TurnoSituacaoEnum.ABERTO)).thenReturn(Optional.of(abertoTurno));
            when(dinheiroSessaoRepository.existsByCaixeiroUsuarioIdAndStatus(input.getCaixeiroUsuarioId(), SessaoCaixaSituacaoEnum.ABERTO))
                    .thenReturn(true);

            assertThatThrownBy(() -> dinheiroSessaoService.open(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(input.getCaixeiroUsuarioId().toString());
            verify(dinheiroSessaoRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    class Close {

        private final SessaoCaixa abertoSessao = SessaoCaixaFixture.abertoEntity(abertoTurno);

        @Test
        void shouldCloseSessaoApplyingTotals() {
            when(dinheiroSessaoRepository.findByIdAndStatus(abertoSessao.getId(), SessaoCaixaSituacaoEnum.ABERTO)).thenReturn(Optional.of(abertoSessao));
            returnSavedSessao();

            SessaoCaixaResponseDTO response = dinheiroSessaoService.close(abertoSessao.getId(), SessaoCaixaFixture.closeInput());

            SessaoCaixa saved = captureSavedSessao();
            assertThat(saved.getStatus()).isEqualTo(SessaoCaixaSituacaoEnum.FECHADO);
            assertThat(saved.getFechadoAt()).isNotNull();
            assertThat(saved.getTotalDinheiro()).isEqualTo(SessaoCaixaFixture.TOTAL_DINHEIRO);
            assertThat(saved.getTotalCartao()).isEqualTo(SessaoCaixaFixture.TOTAL_CARTAO);
            assertThat(saved.getTotalPix()).isEqualTo(SessaoCaixaFixture.TOTAL_PIX);
            assertThat(saved.getTotalSangria()).isEqualTo(SessaoCaixaFixture.TOTAL_SANGRIA);
            assertThat(saved.getObservacoes()).isEqualTo(SessaoCaixaFixture.FECHAMENTO_OBSERVACOES);
            assertThat(response.getStatus()).isEqualTo(SessaoCaixaSituacaoEnum.FECHADO);
        }

        @Test
        void shouldKeepOriginalObservacoesWhenCloseObservacoesAreNull() {
            when(dinheiroSessaoRepository.findByIdAndStatus(abertoSessao.getId(), SessaoCaixaSituacaoEnum.ABERTO)).thenReturn(Optional.of(abertoSessao));
            returnSavedSessao();
            var closeInput = SessaoCaixaFixture.closeInput();
            closeInput.setObservacoes(null);

            dinheiroSessaoService.close(abertoSessao.getId(), closeInput);

            assertThat(captureSavedSessao().getObservacoes()).isEqualTo(SessaoCaixaFixture.OBSERVACOES);
        }

        @Test
        void shouldCloseSessaoWithoutTotalsWhenPayloadIsNull() {
            when(dinheiroSessaoRepository.findByIdAndStatus(abertoSessao.getId(), SessaoCaixaSituacaoEnum.ABERTO)).thenReturn(Optional.of(abertoSessao));
            returnSavedSessao();

            dinheiroSessaoService.close(abertoSessao.getId(), null);

            SessaoCaixa saved = captureSavedSessao();
            assertThat(saved.getStatus()).isEqualTo(SessaoCaixaSituacaoEnum.FECHADO);
            assertThat(saved.getFechadoAt()).isNotNull();
            assertThat(saved.getTotalDinheiro()).isNull();
        }

        @Test
        void shouldThrowNotFoundWhenAbertoSessaoDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(dinheiroSessaoRepository.findByIdAndStatus(id, SessaoCaixaSituacaoEnum.ABERTO)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> dinheiroSessaoService.close(id, SessaoCaixaFixture.closeInput()))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(id.toString());
            verify(dinheiroSessaoRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldFindSessaoById() {
            SessaoCaixa sessao = SessaoCaixaFixture.abertoEntity(abertoTurno);
            when(dinheiroSessaoRepository.findById(sessao.getId())).thenReturn(Optional.of(sessao));

            SessaoCaixaResponseDTO response = dinheiroSessaoService.findById(sessao.getId());

            assertThat(response.getId()).isEqualTo(sessao.getId());
            assertThat(response.getTurnoId()).isEqualTo(abertoTurno.getId());
        }

        @Test
        void shouldThrowNotFoundWhenSessaoIdDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(dinheiroSessaoRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> dinheiroSessaoService.findById(id))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(id.toString());
        }

        @Test
        void shouldListSessoesOfTurno() {
            List<SessaoCaixa> sessoes = List.of(SessaoCaixaFixture.abertoEntity(abertoTurno), SessaoCaixaFixture.fechadoEntity(abertoTurno));
            when(dinheiroSessaoRepository.findByTurno_IdOrderByOpenedAt(abertoTurno.getId())).thenReturn(sessoes);

            List<SessaoCaixaResponseDTO> responses = dinheiroSessaoService.findByTurno(abertoTurno.getId());

            assertThat(responses)
                    .extracting(SessaoCaixaResponseDTO::getId)
                    .containsExactly(sessoes.get(0).getId(), sessoes.get(1).getId());
        }
    }

    @Nested
    class SaveAll {

        private final SessaoCaixaSincronizacaoDTO sincronizacao = SessaoCaixaFixture.fechadoSincronizacaoInput(UUID.randomUUID(), abertoTurno.getId(), UUID.randomUUID());

        @Test
        void shouldIgnoreNullList() {
            dinheiroSessaoService.saveAll(null);

            verifyNoInteractions(dinheiroSessaoRepository, turnoRepository);
        }

        @Test
        void shouldIgnoreEmptyList() {
            dinheiroSessaoService.saveAll(List.of());

            verifyNoInteractions(dinheiroSessaoRepository, turnoRepository);
        }

        @Test
        void shouldSkipNullItens() {
            dinheiroSessaoService.saveAll(Collections.singletonList(null));

            verifyNoInteractions(dinheiroSessaoRepository, turnoRepository);
        }

        @Test
        void shouldInsertNewSessaoLinkedToTurno() {
            when(dinheiroSessaoRepository.findById(sincronizacao.getId())).thenReturn(Optional.empty());
            when(turnoRepository.findById(abertoTurno.getId())).thenReturn(Optional.of(abertoTurno));

            dinheiroSessaoService.saveAll(List.of(sincronizacao));

            SessaoCaixa saved = captureSavedSessao();
            assertThat(saved.getId()).isEqualTo(sincronizacao.getId());
            assertThat(saved.getTurno()).isSameAs(abertoTurno);
            assertThat(saved.getStatus()).isEqualTo(SessaoCaixaSituacaoEnum.FECHADO);
        }

        @Test
        void shouldUpdateExistingSessaoIdempotently() {
            SessaoCaixa existing = SessaoCaixaFixture.abertoEntity(abertoTurno);
            existing.setId(sincronizacao.getId());
            when(dinheiroSessaoRepository.findById(sincronizacao.getId())).thenReturn(Optional.of(existing));

            dinheiroSessaoService.saveAll(List.of(sincronizacao));

            SessaoCaixa saved = captureSavedSessao();
            assertThat(saved).isSameAs(existing);
            assertThat(saved.getStatus()).isEqualTo(SessaoCaixaSituacaoEnum.FECHADO);
            assertThat(saved.getTotalDinheiro()).isEqualTo(SessaoCaixaFixture.TOTAL_DINHEIRO);
            verifyNoInteractions(turnoRepository);
        }

        @Test
        void shouldThrowExcecaoNegocioWhenTurnoOfNewSessaoDoesNotExist() {
            when(dinheiroSessaoRepository.findById(sincronizacao.getId())).thenReturn(Optional.empty());
            when(turnoRepository.findById(abertoTurno.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> dinheiroSessaoService.saveAll(List.of(sincronizacao)))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(abertoTurno.getId().toString())
                    .hasMessageContaining(sincronizacao.getId().toString());
            verify(dinheiroSessaoRepository, never()).saveAndFlush(any());
        }

        @Test
        @SuppressWarnings("unchecked")
        void shouldWrapConstraintViolationInExcecaoNegocio() {
            ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn(VIOLATION_MESSAGE);
            ConstraintViolationException violationException = new ConstraintViolationException(Set.of(violation));
            when(dinheiroSessaoRepository.findById(sincronizacao.getId())).thenReturn(Optional.empty());
            when(turnoRepository.findById(abertoTurno.getId())).thenReturn(Optional.of(abertoTurno));
            when(dinheiroSessaoRepository.saveAndFlush(any(SessaoCaixa.class))).thenThrow(violationException);

            assertThatThrownBy(() -> dinheiroSessaoService.saveAll(List.of(sincronizacao)))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(VIOLATION_MESSAGE);
        }
    }
}
