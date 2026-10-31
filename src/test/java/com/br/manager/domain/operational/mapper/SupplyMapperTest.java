package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.SupplyRequestDTO;
import com.br.manager.domain.operational.entity.Supply;
import com.br.manager.domain.operational.fixture.SupplyFixture;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SupplyMapperTest {

    private final SupplyMapper mapper = new SupplyMapper();

    @Test
    void shouldMapRequestToEntityKeepingPdvId() {
        SupplyRequestDTO request = SupplyFixture.validRequest();

        Supply supply = mapper.supplyRequestDTOToSupply(request);

        assertThat(supply)
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(request);
        assertThat(supply.getCreatedAt()).isNull();
    }

    @Test
    void shouldReturnNullWhenRequestIsNull() {
        assertThat(mapper.supplyRequestDTOToSupply(null)).isNull();
    }

    @Test
    void shouldMapRequestListToEntityList() {
        List<SupplyRequestDTO> requests = List.of(SupplyFixture.validRequest(), SupplyFixture.validRequest());

        List<Supply> supplies = mapper.listSupplyRequestDTOToListSupply(requests);

        assertThat(supplies)
                .extracting(Supply::getId)
                .containsExactly(requests.get(0).getId(), requests.get(1).getId());
    }

    @Test
    void shouldReturnNullWhenRequestListIsNull() {
        assertThat(mapper.listSupplyRequestDTOToListSupply(null)).isNull();
    }
}
