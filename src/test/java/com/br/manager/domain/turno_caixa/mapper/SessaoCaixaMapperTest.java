package com.br.manager.domain.turno_caixa.mapper;

import com.br.manager.domain.turno_caixa.dto.SessaoCaixaInputDTO;
import com.br.manager.domain.turno_caixa.dto.SessaoCaixaResponseDTO;
import com.br.manager.domain.turno_caixa.dto.SessaoCaixaSincronizacaoDTO;
import com.br.manager.domain.turno_caixa.entity.SessaoCaixa;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.SessaoCaixaSituacaoEnum;
import com.br.manager.domain.turno_caixa.enums.CargoCaixaEnum;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import com.br.manager.domain.turno_caixa.fixture.SessaoCaixaFixture;
import com.br.manager.domain.turno_caixa.support.TurnoBTestData;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SessaoCaixaMapperTest {

    private final SessaoCaixaMapper mapper = new SessaoCaixaMapper();
    private final Turno turno = TurnoBTestData.turnoEntity(TurnoSituacaoEnum.ABERTO);

    @Nested
    class InputToEntity {

        @Test
        void shouldMapInputToEntityLinkingTurno() {
            SessaoCaixaInputDTO input = SessaoCaixaFixture.validoInput(turno.getId(), UUID.randomUUID());

            SessaoCaixa sessao = mapper.toEntity(input, turno);

            assertThat(sessao.getTurno()).isSameAs(turno);
            assertThat(sessao.getCaixeiroUsuarioId()).isEqualTo(input.getCaixeiroUsuarioId());
            assertThat(sessao.getCargo()).isEqualTo(SessaoCaixaFixture.CARGO);
            assertThat(sessao.getFundoAbertura()).isEqualTo(SessaoCaixaFixture.FUNDO_ABERTURA);
            assertThat(sessao.getObservacoes()).isEqualTo(SessaoCaixaFixture.OBSERVACOES);
        }

        @Test
        void shouldLeaveIdSituacaoAndOpenedAtUnsetWhenMappingInput() {
            SessaoCaixa sessao = mapper.toEntity(SessaoCaixaFixture.validoInput(turno.getId(), UUID.randomUUID()), turno);

            assertThat(sessao.getId()).isNull();
            assertThat(sessao.getStatus()).isNull();
            assertThat(sessao.getOpenedAt()).isNull();
        }

        @Test
        void shouldReturnNullWhenInputIsNull() {
            assertThat(mapper.toEntity((SessaoCaixaInputDTO) null, turno)).isNull();
        }
    }

    @Nested
    class SincronizacaoToEntity {

        @Test
        void shouldMapSincronizacaoDtoToEntityKeepingPdvId() {
            SessaoCaixaSincronizacaoDTO sincronizacao = SessaoCaixaFixture.fechadoSincronizacaoInput(UUID.randomUUID(), turno.getId(), UUID.randomUUID());

            SessaoCaixa sessao = mapper.toEntity(sincronizacao, turno);

            assertThat(sessao.getTurno()).isSameAs(turno);
            assertThat(sessao)
                    .usingRecursiveComparison()
                    .ignoringFields("turno", "criadoEm", "atualizadoEm", "excluidoEm")
                    .isEqualTo(sincronizacao);
        }

        @Test
        void shouldReturnNullWhenSincronizacaoDtoIsNull() {
            assertThat(mapper.toEntity((SessaoCaixaSincronizacaoDTO) null, turno)).isNull();
        }
    }

    @Nested
    class UpdateFromSincronizacao {

        @Test
        void shouldOverwriteMutableFieldsKeepingIdAndTurno() {
            SessaoCaixa sessao = SessaoCaixaFixture.abertoEntity(turno);
            UUID originalId = sessao.getId();
            SessaoCaixaSincronizacaoDTO sincronizacao = SessaoCaixaFixture.fechadoSincronizacaoInput(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

            mapper.updateFromSincronizacao(sincronizacao, sessao);

            assertThat(sessao.getId()).isEqualTo(originalId);
            assertThat(sessao.getTurno()).isSameAs(turno);
            assertThat(sessao.getCaixeiroUsuarioId()).isEqualTo(sincronizacao.getCaixeiroUsuarioId());
            assertThat(sessao.getCargo()).isEqualTo(CargoCaixaEnum.FRENTISTA);
            assertThat(sessao.getStatus()).isEqualTo(SessaoCaixaSituacaoEnum.FECHADO);
            assertThat(sessao.getOpenedAt()).isEqualTo(SessaoCaixaFixture.OPENED_AT);
            assertThat(sessao.getFechadoAt()).isEqualTo(SessaoCaixaFixture.FECHADO_AT);
            assertThat(sessao.getTotalDinheiro()).isEqualTo(SessaoCaixaFixture.TOTAL_DINHEIRO);
            assertThat(sessao.getTotalCartao()).isEqualTo(SessaoCaixaFixture.TOTAL_CARTAO);
            assertThat(sessao.getTotalPix()).isEqualTo(SessaoCaixaFixture.TOTAL_PIX);
            assertThat(sessao.getTotalSangria()).isEqualTo(SessaoCaixaFixture.TOTAL_SANGRIA);
            assertThat(sessao.getObservacoes()).isEqualTo(SessaoCaixaFixture.SINCRONIZACAO_OBSERVACOES);
        }

        @Test
        void shouldIgnoreNullSincronizacaoDto() {
            SessaoCaixa sessao = SessaoCaixaFixture.abertoEntity(turno);

            mapper.updateFromSincronizacao(null, sessao);

            assertThat(sessao.getStatus()).isEqualTo(SessaoCaixaSituacaoEnum.ABERTO);
            assertThat(sessao.getObservacoes()).isEqualTo(SessaoCaixaFixture.OBSERVACOES);
        }

        @Test
        void shouldIgnoreNullSessao() {
            SessaoCaixaSincronizacaoDTO sincronizacao = SessaoCaixaFixture.fechadoSincronizacaoInput(UUID.randomUUID(), turno.getId(), UUID.randomUUID());

            mapper.updateFromSincronizacao(sincronizacao, null);

            assertThat(sincronizacao.getStatus()).isEqualTo(SessaoCaixaSituacaoEnum.FECHADO);
        }
    }

    @Nested
    class EntityToResponse {

        @Test
        void shouldMapEntityToResponseWithTurnoId() {
            SessaoCaixa sessao = SessaoCaixaFixture.fechadoEntity(turno);

            SessaoCaixaResponseDTO response = mapper.toResponse(sessao);

            assertThat(response.getTurnoId()).isEqualTo(turno.getId());
            assertThat(response)
                    .usingRecursiveComparison()
                    .ignoringFields("turnoId")
                    .isEqualTo(sessao);
        }

        @Test
        void shouldMapNullTurnoIdWhenSessaoHasNoTurno() {
            SessaoCaixa sessao = SessaoCaixaFixture.abertoEntity(null);

            assertThat(mapper.toResponse(sessao).getTurnoId()).isNull();
        }

        @Test
        void shouldReturnNullWhenEntityIsNull() {
            assertThat(mapper.toResponse(null)).isNull();
        }

        @Test
        void shouldMapEntityListPreservingOrder() {
            List<SessaoCaixa> sessoes = List.of(SessaoCaixaFixture.abertoEntity(turno), SessaoCaixaFixture.fechadoEntity(turno));

            List<SessaoCaixaResponseDTO> responses = mapper.toResponseList(sessoes);

            assertThat(responses)
                    .extracting(SessaoCaixaResponseDTO::getId)
                    .containsExactly(sessoes.get(0).getId(), sessoes.get(1).getId());
        }

        @Test
        void shouldReturnEmptyListWhenEntityListIsNull() {
            assertThat(mapper.toResponseList(null)).isEmpty();
        }
    }
}
