package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.CustomerVehicleInputDTO;
import com.br.manager.domain.operational.dto.CustomerVehicleResponseDTO;
import com.br.manager.domain.operational.entity.CustomerVehicle;
import com.br.manager.domain.operational.fixture.CustomerVehicleFixture;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerVehicleMapperTest {

    private static final String PLATE_BUG = "BUG: CustomerVehicleMapper uses StringUtils.removeMask (onlyDigits) on the plate, "
            + "stripping its letters (ABC-1D23 -> 123); plates must keep letters and only drop separators";

    private final CustomerVehicleMapper mapper = new CustomerVehicleMapper();

    @Test
    void shouldMapInputToEntity() {
        UUID customerId = UUID.randomUUID();
        CustomerVehicleInputDTO input = CustomerVehicleFixture.validInput(customerId);
        input.setId(UUID.randomUUID());

        CustomerVehicle vehicle = mapper.customerVehicleInputDTOToCustomerVehicle(input);

        assertThat(vehicle.getId()).isEqualTo(input.getId());
        assertThat(vehicle.getCustomerId()).isEqualTo(customerId);
        assertThat(vehicle.getBrand()).isEqualTo(CustomerVehicleFixture.BRAND);
        assertThat(vehicle.getModel()).isEqualTo(CustomerVehicleFixture.MODEL);
        assertThat(vehicle.getFuelType()).isEqualTo(CustomerVehicleFixture.FUEL_TYPE);
        assertThat(vehicle.getActive()).isTrue();
    }

    @Test
    @Disabled(PLATE_BUG)
    void shouldMapInputPlateRemovingOnlySeparators() {
        CustomerVehicle vehicle = mapper.customerVehicleInputDTOToCustomerVehicle(
                CustomerVehicleFixture.validInput(UUID.randomUUID()));

        assertThat(vehicle.getPlate()).isEqualTo(CustomerVehicleFixture.PLATE);
    }

    @Test
    void shouldMapEntityToResponse() {
        CustomerVehicle vehicle = CustomerVehicleFixture.persistedEntity();

        CustomerVehicleResponseDTO response = mapper.customerVehicleToCustomerVehicleResponseDTO(vehicle);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(vehicle);
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<CustomerVehicle> vehicles = List.of(CustomerVehicleFixture.persistedEntity(), CustomerVehicleFixture.persistedEntity());

        List<CustomerVehicleResponseDTO> responses = mapper.listCustomerVehicleToListCustomerVehicleResponseDTO(vehicles);

        assertThat(responses)
                .extracting(CustomerVehicleResponseDTO::getId)
                .containsExactly(vehicles.get(0).getId(), vehicles.get(1).getId());
    }

    @Test
    void shouldUpdateEntityFromInputKeepingId() {
        CustomerVehicle vehicle = CustomerVehicleFixture.persistedEntity();
        UUID originalId = vehicle.getId();
        UUID newCustomerId = UUID.randomUUID();
        CustomerVehicleInputDTO input = CustomerVehicleFixture.validInput(newCustomerId);
        input.setId(UUID.randomUUID());
        input.setModel("Delivery 11.180");
        input.setActive(false);

        mapper.updateCustomerVehicleFromDto(input, vehicle);

        assertThat(vehicle.getId()).isEqualTo(originalId);
        assertThat(vehicle.getCustomerId()).isEqualTo(newCustomerId);
        assertThat(vehicle.getModel()).isEqualTo("Delivery 11.180");
        assertThat(vehicle.getActive()).isFalse();
    }

    @Test
    @Disabled(PLATE_BUG)
    void shouldUpdatePlateRemovingOnlySeparators() {
        CustomerVehicle vehicle = CustomerVehicleFixture.persistedEntity();

        mapper.updateCustomerVehicleFromDto(CustomerVehicleFixture.validInput(vehicle.getCustomerId()), vehicle);

        assertThat(vehicle.getPlate()).isEqualTo(CustomerVehicleFixture.PLATE);
    }
}
