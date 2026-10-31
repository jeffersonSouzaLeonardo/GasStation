package com.br.manager.domain.operational.fixture;

import com.br.manager.domain.operational.dto.NozzleInputDTO;
import com.br.manager.domain.operational.entity.Nozzle;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Bico.
 */
public final class NozzleFixture {

    public static final String CODE = "BICO-01";
    public static final String OTHER_CODE = "BICO-02";
    public static final BigDecimal METER_NUMBER = new BigDecimal("123456.5");

    private NozzleFixture() {
    }

    public static NozzleInputDTO validInput(UUID pumpId, UUID tankId, UUID productId) {
        NozzleInputDTO input = new NozzleInputDTO();
        input.setPumpId(pumpId);
        input.setTankId(tankId);
        input.setProductId(productId);
        input.setCode(CODE);
        input.setMeterNumber(METER_NUMBER);
        input.setActive(true);
        return input;
    }

    public static NozzleInputDTO validInput() {
        return validInput(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    }

    public static Nozzle persistedEntity() {
        Nozzle nozzle = new Nozzle();
        nozzle.setId(UUID.randomUUID());
        nozzle.setPumpId(UUID.randomUUID());
        nozzle.setTankId(UUID.randomUUID());
        nozzle.setProductId(UUID.randomUUID());
        nozzle.setCode(CODE);
        nozzle.setMeterNumber(METER_NUMBER);
        nozzle.setActive(true);
        return nozzle;
    }
}
