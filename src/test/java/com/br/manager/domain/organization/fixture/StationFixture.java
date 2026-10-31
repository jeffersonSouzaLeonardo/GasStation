package com.br.manager.domain.organization.fixture;

import com.br.manager.domain.organization.dto.StationInputDTO;
import com.br.manager.domain.organization.entity.Station;

import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Posto.
 */
public final class StationFixture {

    public static final String NAME = "Posto Centro";
    public static final String MASKED_CNPJ = "12.345.678/0002-71";
    public static final String CNPJ = "12345678000271";
    public static final String MASKED_STATE_REGISTRATION = "987.654.321";
    public static final String STATE_REGISTRATION = "987654321";
    public static final String MASKED_ZIP_CODE = "01310-100";
    public static final String ZIP_CODE = "01310100";
    public static final String STREET = "Avenida Paulista";
    public static final String NUMBER = "1000";
    public static final String COMPLEMENT = "Loja 1";
    public static final String NEIGHBORHOOD = "Bela Vista";
    public static final String CITY = "Sao Paulo";
    public static final String STATE = "SP";
    public static final String TIMEZONE = "America/Sao_Paulo";
    public static final String OTHER_TIMEZONE = "America/Manaus";

    private StationFixture() {
    }

    public static StationInputDTO validInput(UUID companyId) {
        StationInputDTO input = new StationInputDTO();
        input.setCompanyId(companyId);
        input.setName(NAME);
        input.setCnpj(MASKED_CNPJ);
        input.setStateRegistration(MASKED_STATE_REGISTRATION);
        input.setAddressStreet(STREET);
        input.setAddressNumber(NUMBER);
        input.setAddressComplement(COMPLEMENT);
        input.setAddressNeighborhood(NEIGHBORHOOD);
        input.setAddressCity(CITY);
        input.setAddressState(STATE);
        input.setAddressZipCode(MASKED_ZIP_CODE);
        input.setTimezone(TIMEZONE);
        input.setActive(true);
        return input;
    }

    public static StationInputDTO validInput() {
        return validInput(UUID.randomUUID());
    }

    public static Station persistedEntity() {
        Station station = new Station();
        station.setId(UUID.randomUUID());
        station.setCompanyId(UUID.randomUUID());
        station.setName(NAME);
        station.setCnpj(CNPJ);
        station.setStateRegistration(STATE_REGISTRATION);
        station.setAddressStreet(STREET);
        station.setAddressNumber(NUMBER);
        station.setAddressComplement(COMPLEMENT);
        station.setAddressNeighborhood(NEIGHBORHOOD);
        station.setAddressCity(CITY);
        station.setAddressState(STATE);
        station.setAddressZipCode(ZIP_CODE);
        station.setTimezone(TIMEZONE);
        station.setActive(true);
        return station;
    }
}
