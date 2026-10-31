package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.FuelInputDTO;
import com.br.manager.domain.operational.dto.FuelResponseDTO;
import com.br.manager.domain.operational.entity.Fuel;
import com.br.manager.domain.operational.enums.StatusFuelEnum;
import com.br.manager.domain.operational.fixture.FuelFixture;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FuelMapperTest {

    private final FuelMapper mapper = new FuelMapper();

    @Test
    void shouldMapInputToEntity() {
        FuelInputDTO input = FuelFixture.validInput();
        input.setId(FuelFixture.ID);

        Fuel fuel = mapper.fuelInputToFuelEntity(input);

        assertThat(fuel)
                .usingRecursiveComparison()
                .ignoringFields("deleted")
                .isEqualTo(input);
        assertThat(fuel.getDeleted()).isNull();
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.fuelInputToFuelEntity(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        Fuel fuel = FuelFixture.persistedEntity();

        FuelResponseDTO response = mapper.fuelEntityToFuelResponseDTO(fuel);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(fuel);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.fuelEntityToFuelResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<Fuel> fuels = List.of(FuelFixture.persistedEntity());

        List<FuelResponseDTO> responses = mapper.listFuelEntityToListFuelResponseDTO(fuels);

        assertThat(responses)
                .extracting(FuelResponseDTO::getName)
                .containsExactly(FuelFixture.NAME);
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listFuelEntityToListFuelResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityFromInput() {
        Fuel fuel = FuelFixture.persistedEntity();
        FuelInputDTO input = FuelFixture.otherInput();
        input.setId(fuel.getId());
        input.setStatus(StatusFuelEnum.INATIVE);

        mapper.updateFuelFromDto(input, fuel);

        assertThat(fuel.getName()).isEqualTo(FuelFixture.OTHER_NAME);
        assertThat(fuel.getStatus()).isEqualTo(StatusFuelEnum.INATIVE);
        assertThat(fuel.getId()).isEqualTo(FuelFixture.ID);
    }

    @Test
    void shouldIgnoreUpdateWhenInputIsNull() {
        Fuel fuel = FuelFixture.persistedEntity();

        mapper.updateFuelFromDto(null, fuel);

        assertThat(fuel.getName()).isEqualTo(FuelFixture.NAME);
    }
}
