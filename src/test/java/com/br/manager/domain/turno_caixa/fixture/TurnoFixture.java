package com.br.manager.domain.turno_caixa.fixture;

import com.br.manager.domain.turno_caixa.dto.TurnoFechamentoRequestDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoAberturaRequestDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoSincronizacaoDTO;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Turno.
 */
public final class TurnoFixture {

    public static final BigDecimal FUNDO_ABERTURA = new BigDecimal("150.00");
    public static final String OBSERVACOES = "Turno da manha";
    public static final String NAME = "Turno 1";
    public static final String SINCRONIZACAO_OBSERVACOES = "Turno recebido do PDV";
    public static final LocalDateTime OPENED_AT = LocalDateTime.of(2026, 10, 30, 6, 0, 0);
    public static final LocalDateTime FECHADO_AT = LocalDateTime.of(2026, 10, 30, 14, 0, 0);

    private TurnoFixture() {
    }

    public static TurnoAberturaRequestDTO abertoRequest(UUID estacaoId, UUID caixeiroUsuarioId, UUID openedBy) {
        TurnoAberturaRequestDTO request = new TurnoAberturaRequestDTO();
        request.setEstacaoId(estacaoId);
        request.setCaixeiroUsuarioId(caixeiroUsuarioId);
        request.setOpenedBy(openedBy);
        request.setFundoAbertura(FUNDO_ABERTURA);
        request.setObservacoes(OBSERVACOES);
        return request;
    }

    public static TurnoAberturaRequestDTO abertoRequest() {
        return abertoRequest(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    }

    public static TurnoFechamentoRequestDTO closeRequest(UUID fechadoBy) {
        TurnoFechamentoRequestDTO request = new TurnoFechamentoRequestDTO();
        request.setFechadoBy(fechadoBy);
        return request;
    }

    public static TurnoSincronizacaoDTO abertoSincronizacao(UUID id, UUID estacaoId, UUID openedBy) {
        TurnoSincronizacaoDTO dto = new TurnoSincronizacaoDTO();
        dto.setId(id);
        dto.setEstacaoId(estacaoId);
        dto.setNome(NAME);
        dto.setOpenedAt(OPENED_AT);
        dto.setStatus(TurnoSituacaoEnum.ABERTO);
        dto.setOpenedBy(openedBy);
        dto.setObservacoes(SINCRONIZACAO_OBSERVACOES);
        return dto;
    }

    public static TurnoSincronizacaoDTO fechadoSincronizacao(UUID id, UUID estacaoId, UUID usuarioId) {
        TurnoSincronizacaoDTO dto = abertoSincronizacao(id, estacaoId, usuarioId);
        dto.setStatus(TurnoSituacaoEnum.FECHADO);
        dto.setFechadoAt(FECHADO_AT);
        dto.setFechadoBy(usuarioId);
        return dto;
    }

    public static TurnoSincronizacaoDTO abertoSincronizacao() {
        return abertoSincronizacao(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    }

    public static Turno abertoTurno() {
        Turno turno = new Turno();
        turno.setId(UUID.randomUUID());
        turno.setEstacaoId(UUID.randomUUID());
        turno.setCaixeiroUsuarioId(UUID.randomUUID());
        turno.setOpenedBy(UUID.randomUUID());
        turno.setOpenedAt(OPENED_AT);
        turno.setFundoAbertura(FUNDO_ABERTURA);
        turno.setStatus(TurnoSituacaoEnum.ABERTO);
        turno.setNome(NAME);
        turno.setObservacoes(OBSERVACOES);
        return turno;
    }

    public static Turno fechadoTurno() {
        Turno turno = abertoTurno();
        turno.setStatus(TurnoSituacaoEnum.FECHADO);
        turno.setFechadoAt(FECHADO_AT);
        turno.setFechadoBy(turno.getOpenedBy());
        return turno;
    }
}
