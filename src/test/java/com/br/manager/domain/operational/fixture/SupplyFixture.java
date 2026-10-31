package com.br.manager.domain.operational.fixture;

import com.br.manager.domain.operational.dto.SupplyRequestDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Massa de dados padrao para abastecimentos recebidos do PDV.
 */
public final class SupplyFixture {

    public static final LocalDateTime DATA_TIME = LocalDateTime.of(2026, 10, 30, 14, 35, 10);
    public static final BigDecimal VOLUME = new BigDecimal("40.0000");
    public static final BigDecimal TOTAL_VALUE = new BigDecimal("239.6000");
    public static final BigDecimal UPDATED_TOTAL_VALUE = new BigDecimal("250.0000");

    private SupplyFixture() {
    }

    public static SupplyRequestDTO validRequest() {
        return validRequest(UUID.randomUUID(), UUID.randomUUID());
    }

    public static SupplyRequestDTO validRequest(UUID tankId, UUID pumpId) {
        SupplyRequestDTO request = new SupplyRequestDTO();
        request.setId(UUID.randomUUID());
        request.setDataTime(DATA_TIME);
        request.setTankId(tankId);
        request.setPumpId(pumpId);
        request.setVolume(VOLUME);
        request.setTotalValue(TOTAL_VALUE);
        return request;
    }
}
