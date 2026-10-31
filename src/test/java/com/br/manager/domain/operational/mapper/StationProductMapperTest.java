package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.StationProductInputDTO;
import com.br.manager.domain.operational.dto.StationProductResponseDTO;
import com.br.manager.domain.operational.entity.StationProduct;
import com.br.manager.domain.operational.fixture.StationProductFixture;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class StationProductMapperTest {

    private final StationProductMapper mapper = new StationProductMapper();

    @Test
    void shouldMapInputToEntity() {
        StationProductInputDTO input = StationProductFixture.validInput(UUID.randomUUID());
        input.setId(UUID.randomUUID());

        StationProduct entity = mapper.stationProductInputDTOToStationProduct(input);

        assertThat(entity).usingRecursiveComparison().isEqualTo(input);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.stationProductInputDTOToStationProduct(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        StationProduct entity = StationProductFixture.persistedEntity();

        StationProductResponseDTO response = mapper.stationProductToStationProductResponseDTO(entity);

        assertThat(response).usingRecursiveComparison().isEqualTo(entity);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.stationProductToStationProductResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<StationProduct> entities = List.of(StationProductFixture.persistedEntity(), StationProductFixture.persistedEntity());

        List<StationProductResponseDTO> responses = mapper.listStationProductToListStationProductResponseDTO(entities);

        assertThat(responses)
                .extracting(StationProductResponseDTO::getId)
                .containsExactly(entities.get(0).getId(), entities.get(1).getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listStationProductToListStationProductResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityFromInput() {
        StationProduct entity = StationProductFixture.persistedEntity();
        StationProductInputDTO input = StationProductFixture.validInput(entity.getProductId());
        input.setId(entity.getId());
        input.setMinStock(BigDecimal.TEN);

        mapper.updateStationProductFromDto(input, entity);

        assertThat(entity.getId()).isEqualTo(input.getId());
        assertThat(entity.getMinStock()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void shouldIgnoreUpdateWhenEntityIsNull() {
        StationProductInputDTO input = StationProductFixture.validInput(UUID.randomUUID());

        assertThatCode(() -> mapper.updateStationProductFromDto(input, null)).doesNotThrowAnyException();
    }
}
