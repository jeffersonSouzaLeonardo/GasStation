package com.br.manager.domain.operacional.fixture;

import com.br.manager.domain.operacional.dto.AbastecimentoRequestDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Massa de dados padrao para abastecimentos recebidos do PDV.
 */
public final class AbastecimentoFixture {

    public static final LocalDateTime DATA_TIME = LocalDateTime.of(2026, 10, 30, 14, 35, 10);
    public static final BigDecimal VOLUME = new BigDecimal("40.0000");
    public static final BigDecimal TOTAL_VALOR = new BigDecimal("239.6000");
    public static final BigDecimal ATUALIZADO_TOTAL_VALOR = new BigDecimal("250.0000");

    private AbastecimentoFixture() {
    }

    public static AbastecimentoRequestDTO validoRequest() {
        return validoRequest(UUID.randomUUID(), UUID.randomUUID());
    }

    public static AbastecimentoRequestDTO validoRequest(UUID tanqueId, UUID bombaId) {
        AbastecimentoRequestDTO request = new AbastecimentoRequestDTO();
        request.setId(UUID.randomUUID());
        request.setDataHora(DATA_TIME);
        request.setTanqueId(tanqueId);
        request.setBombaId(bombaId);
        request.setVolume(VOLUME);
        request.setValorTotal(TOTAL_VALOR);
        return request;
    }
}
