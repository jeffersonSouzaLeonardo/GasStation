package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.NozzleInputDTO;
import com.br.manager.domain.operational.dto.NozzleResponseDTO;
import com.br.manager.domain.operational.entity.Nozzle;
import com.br.manager.domain.operational.fixture.NozzleFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NozzleMapperTest {

    private final NozzleMapper mapper = new NozzleMapper();

    @Test
    void shouldMapInputToEntity() {
        NozzleInputDTO input = NozzleFixture.validInput();
        input.setId(UUID.randomUUID());

        Nozzle nozzle = mapper.nozzleInputDTOToNozzle(input);

        assertThat(nozzle)
                .usingRecursiveComparison()
                .comparingOnlyFields("id", "pumpId", "tankId", "productId", "code", "meterNumber", "active")
                .isEqualTo(input);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.nozzleInputDTOToNozzle(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        Nozzle nozzle = NozzleFixture.persistedEntity();

        NozzleResponseDTO response = mapper.nozzleToNozzleResponseDTO(nozzle);

        assertThat(response).usingRecursiveComparison().isEqualTo(nozzle);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.nozzleToNozzleResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<Nozzle> nozzles = List.of(NozzleFixture.persistedEntity(), NozzleFixture.persistedEntity());

        List<NozzleResponseDTO> responses = mapper.listNozzleToListNozzleResponseDTO(nozzles);

        assertThat(responses)
                .extracting(NozzleResponseDTO::getId)
                .containsExactly(nozzles.get(0).getId(), nozzles.get(1).getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listNozzleToListNozzleResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityFromInput() {
        Nozzle nozzle = NozzleFixture.persistedEntity();
        NozzleInputDTO input = NozzleFixture.validInput();
        input.setId(nozzle.getId());
        input.setCode(NozzleFixture.OTHER_CODE);

        mapper.updateNozzleFromDto(input, nozzle);

        assertThat(nozzle.getId()).isEqualTo(input.getId());
        assertThat(nozzle.getCode()).isEqualTo(NozzleFixture.OTHER_CODE);
        assertThat(nozzle.getPumpId()).isEqualTo(input.getPumpId());
    }

    @Test
    void shouldIgnoreUpdateWhenInputIsNull() {
        Nozzle nozzle = NozzleFixture.persistedEntity();

        mapper.updateNozzleFromDto(null, nozzle);

        assertThat(nozzle.getCode()).isEqualTo(NozzleFixture.CODE);
    }
}
