package com.br.manager.domain.turno_caixa.fixture;

import com.br.manager.domain.turno_caixa.dto.SessaoCaixaFechamentoInputDTO;
import com.br.manager.domain.turno_caixa.dto.SessaoCaixaInputDTO;
import com.br.manager.domain.turno_caixa.dto.SessaoCaixaSincronizacaoDTO;
import com.br.manager.domain.turno_caixa.entity.SessaoCaixa;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.SessaoCaixaSituacaoEnum;
import com.br.manager.domain.turno_caixa.enums.CargoCaixaEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Sessao de Caixa.
 */
public final class SessaoCaixaFixture {

    public static final CargoCaixaEnum CARGO = CargoCaixaEnum.CAIXA;
    public static final BigDecimal FUNDO_ABERTURA = new BigDecimal("100.00");
    public static final String OBSERVACOES = "Caixa principal da pista";
    public static final BigDecimal TOTAL_DINHEIRO = new BigDecimal("350.00");
    public static final BigDecimal TOTAL_CARTAO = new BigDecimal("420.50");
    public static final BigDecimal TOTAL_PIX = new BigDecimal("130.25");
    public static final BigDecimal TOTAL_SANGRIA = new BigDecimal("50.00");
    public static final String FECHAMENTO_OBSERVACOES = "Fechamento sem divergencias";
    public static final String SINCRONIZACAO_OBSERVACOES = "Sessao recebida do PDV";
    public static final LocalDateTime OPENED_AT = LocalDateTime.of(2026, 10, 31, 6, 0);
    public static final LocalDateTime FECHADO_AT = LocalDateTime.of(2026, 10, 31, 14, 0);
    public static final LocalDateTime CRIADO_EM = LocalDateTime.of(2026, 10, 31, 6, 1);

    private SessaoCaixaFixture() {
    }

    public static SessaoCaixaInputDTO validoInput(UUID turnoId, UUID caixeiroUsuarioId) {
        SessaoCaixaInputDTO input = new SessaoCaixaInputDTO();
        input.setTurnoId(turnoId);
        input.setCaixeiroUsuarioId(caixeiroUsuarioId);
        input.setCargo(CARGO);
        input.setFundoAbertura(FUNDO_ABERTURA);
        input.setObservacoes(OBSERVACOES);
        return input;
    }

    public static SessaoCaixaFechamentoInputDTO closeInput() {
        SessaoCaixaFechamentoInputDTO input = new SessaoCaixaFechamentoInputDTO();
        input.setTotalDinheiro(TOTAL_DINHEIRO);
        input.setTotalCartao(TOTAL_CARTAO);
        input.setTotalPix(TOTAL_PIX);
        input.setTotalSangria(TOTAL_SANGRIA);
        input.setObservacoes(FECHAMENTO_OBSERVACOES);
        return input;
    }

    public static SessaoCaixaSincronizacaoDTO fechadoSincronizacaoInput(UUID id, UUID turnoId, UUID caixeiroUsuarioId) {
        SessaoCaixaSincronizacaoDTO input = new SessaoCaixaSincronizacaoDTO();
        input.setId(id);
        input.setTurnoId(turnoId);
        input.setCaixeiroUsuarioId(caixeiroUsuarioId);
        input.setCargo(CargoCaixaEnum.FRENTISTA);
        input.setFundoAbertura(FUNDO_ABERTURA);
        input.setStatus(SessaoCaixaSituacaoEnum.FECHADO);
        input.setOpenedAt(OPENED_AT);
        input.setFechadoAt(FECHADO_AT);
        input.setTotalDinheiro(TOTAL_DINHEIRO);
        input.setTotalCartao(TOTAL_CARTAO);
        input.setTotalPix(TOTAL_PIX);
        input.setTotalSangria(TOTAL_SANGRIA);
        input.setObservacoes(SINCRONIZACAO_OBSERVACOES);
        return input;
    }

    public static SessaoCaixa abertoEntity(Turno turno) {
        SessaoCaixa sessao = new SessaoCaixa();
        sessao.setId(UUID.randomUUID());
        sessao.setTurno(turno);
        sessao.setCaixeiroUsuarioId(UUID.randomUUID());
        sessao.setCargo(CARGO);
        sessao.setFundoAbertura(FUNDO_ABERTURA);
        sessao.setStatus(SessaoCaixaSituacaoEnum.ABERTO);
        sessao.setOpenedAt(OPENED_AT);
        sessao.setObservacoes(OBSERVACOES);
        sessao.setCriadoEm(CRIADO_EM);
        return sessao;
    }

    public static SessaoCaixa fechadoEntity(Turno turno) {
        SessaoCaixa sessao = abertoEntity(turno);
        sessao.setStatus(SessaoCaixaSituacaoEnum.FECHADO);
        sessao.setFechadoAt(FECHADO_AT);
        sessao.setTotalDinheiro(TOTAL_DINHEIRO);
        sessao.setTotalCartao(TOTAL_CARTAO);
        sessao.setTotalPix(TOTAL_PIX);
        sessao.setTotalSangria(TOTAL_SANGRIA);
        return sessao;
    }
}
