package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.PumpInputDTO;
import com.br.manager.domain.operational.dto.PumpResponseDTO;
import com.br.manager.domain.operational.entity.Pump;
import com.br.manager.domain.operational.fixture.PumpFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PumpMapperTest {

    private final PumpMapper mapper = new PumpMapper();

    @Test
    void shouldMapInputToEntity() {
        PumpInputDTO input = PumpFixture.validInput();
        input.setId(UUID.randomUUID());

        Pump pump = mapper.pumpInputDTOToPump(input);

        assertThat(pump)
                .usingRecursiveComparison()
                .comparingOnlyFields("id", "code", "location", "active")
                .isEqualTo(input);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.pumpInputDTOToPump(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        Pump pump = PumpFixture.persistedEntity();

        PumpResponseDTO response = mapper.pumpToPumpResponseDTO(pump);

        assertThat(response).usingRecursiveComparison().isEqualTo(pump);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.pumpToPumpResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<Pump> pumps = List.of(PumpFixture.persistedEntity(), PumpFixture.persistedEntity());

        List<PumpResponseDTO> responses = mapper.listPumpToListPumpResponseDTO(pumps);

        assertThat(responses)
                .extracting(PumpResponseDTO::getId)
                .containsExactly(pumps.get(0).getId(), pumps.get(1).getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listPumpToListPumpResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityFromInput() {
        Pump pump = PumpFixture.persistedEntity();
        PumpInputDTO input = PumpFixture.validInput(PumpFixture.OTHER_CODE);
        input.setId(pump.getId());

        mapper.updatePumpFromDto(input, pump);

        assertThat(pump.getId()).isEqualTo(input.getId());
        assertThat(pump.getCode()).isEqualTo(PumpFixture.OTHER_CODE);
    }

    @Test
    void shouldIgnoreUpdateWhenInputIsNull() {
        Pump pump = PumpFixture.persistedEntity();

        mapper.updatePumpFromDto(null, pump);

        assertThat(pump.getCode()).isEqualTo(PumpFixture.CODE);
    }
}
