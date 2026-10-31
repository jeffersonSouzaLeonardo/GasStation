package com.br.manager.domain.operational.fixture;

import com.br.manager.domain.operational.dto.FuelInputDTO;
import com.br.manager.domain.operational.entity.Fuel;
import com.br.manager.domain.operational.enums.StatusFuelEnum;
import com.br.manager.domain.operational.enums.UnitFuelEnum;

/**
 * Massa de dados padrao para cenarios de Combustivel.
 */
public final class FuelFixture {

    public static final Long ID = 10L;
    public static final String NAME = "Gasolina Comum";
    public static final String OTHER_NAME = "Etanol Hidratado";
    public static final UnitFuelEnum UNIT = UnitFuelEnum.LITERS;
    public static final StatusFuelEnum STATUS = StatusFuelEnum.ACTIVE;
    public static final String ID_ANP = "320102001";

    private FuelFixture() {
    }

    public static FuelInputDTO validInput() {
        FuelInputDTO input = new FuelInputDTO();
        input.setName(NAME);
        input.setUnit(UNIT);
        input.setStatus(STATUS);
        input.setIdAnp(ID_ANP);
        return input;
    }

    public static FuelInputDTO otherInput() {
        FuelInputDTO input = validInput();
        input.setName(OTHER_NAME);
        return input;
    }

    public static Fuel persistedEntity() {
        Fuel fuel = new Fuel();
        fuel.setId(ID);
        fuel.setName(NAME);
        fuel.setUnit(UNIT);
        fuel.setStatus(STATUS);
        fuel.setIdAnp(ID_ANP);
        return fuel;
    }
}
