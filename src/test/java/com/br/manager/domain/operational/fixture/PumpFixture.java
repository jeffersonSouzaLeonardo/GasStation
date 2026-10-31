package com.br.manager.domain.operational.fixture;

import com.br.manager.domain.operational.dto.PumpInputDTO;
import com.br.manager.domain.operational.entity.Pump;

import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Bomba.
 */
public final class PumpFixture {

    public static final String CODE = "BOMBA-01";
    public static final String OTHER_CODE = "BOMBA-02";
    public static final String LOCATION = "Ilha 1";
    public static final String TOO_LONG_CODE = "B".repeat(51);

    private PumpFixture() {
    }

    public static PumpInputDTO validInput() {
        PumpInputDTO input = new PumpInputDTO();
        input.setCode(CODE);
        input.setLocation(LOCATION);
        input.setActive(true);
        return input;
    }

    public static PumpInputDTO validInput(String code) {
        PumpInputDTO input = validInput();
        input.setCode(code);
        return input;
    }

    public static Pump persistedEntity() {
        Pump pump = new Pump();
        pump.setId(UUID.randomUUID());
        pump.setCode(CODE);
        pump.setLocation(LOCATION);
        pump.setActive(true);
        return pump;
    }
}
