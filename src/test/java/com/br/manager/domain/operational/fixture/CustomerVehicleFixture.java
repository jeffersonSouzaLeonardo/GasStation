package com.br.manager.domain.operational.fixture;

import com.br.manager.domain.operational.dto.CustomerVehicleInputDTO;
import com.br.manager.domain.operational.entity.CustomerVehicle;

import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Veiculo do Cliente.
 */
public final class CustomerVehicleFixture {

    public static final String MASKED_PLATE = "ABC-1D23";
    public static final String PLATE = "ABC1D23";
    public static final String OTHER_PLATE = "XYZ9K87";
    public static final String BRAND = "Volkswagen";
    public static final String MODEL = "Constellation 24.280";
    public static final String FUEL_TYPE = "DIESEL";

    private CustomerVehicleFixture() {
    }

    public static CustomerVehicleInputDTO validInput(UUID customerId) {
        CustomerVehicleInputDTO input = new CustomerVehicleInputDTO();
        input.setCustomerId(customerId);
        input.setPlate(MASKED_PLATE);
        input.setBrand(BRAND);
        input.setModel(MODEL);
        input.setFuelType(FUEL_TYPE);
        input.setActive(true);
        return input;
    }

    public static CustomerVehicle persistedEntity() {
        CustomerVehicle vehicle = new CustomerVehicle();
        vehicle.setId(UUID.randomUUID());
        vehicle.setCustomerId(UUID.randomUUID());
        vehicle.setPlate(PLATE);
        vehicle.setBrand(BRAND);
        vehicle.setModel(MODEL);
        vehicle.setFuelType(FUEL_TYPE);
        vehicle.setActive(true);
        return vehicle;
    }
}
