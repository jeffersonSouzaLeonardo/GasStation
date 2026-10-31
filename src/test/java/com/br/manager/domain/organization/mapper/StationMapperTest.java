package com.br.manager.domain.organization.mapper;

import com.br.manager.domain.organization.dto.StationInputDTO;
import com.br.manager.domain.organization.dto.StationResponseDTO;
import com.br.manager.domain.organization.entity.Station;
import com.br.manager.domain.organization.fixture.StationFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StationMapperTest {

    private final StationMapper mapper = new StationMapper();

    @Test
    void shouldMapInputToEntityRemovingMasks() {
        StationInputDTO input = StationFixture.validInput();
        input.setId(UUID.randomUUID());

        Station station = mapper.stationInputDTOToStation(input);

        assertThat(station.getId()).isEqualTo(input.getId());
        assertThat(station.getCompanyId()).isEqualTo(input.getCompanyId());
        assertThat(station.getName()).isEqualTo(StationFixture.NAME);
        assertThat(station.getCnpj()).isEqualTo(StationFixture.CNPJ);
        assertThat(station.getStateRegistration()).isEqualTo(StationFixture.STATE_REGISTRATION);
        assertThat(station.getAddressZipCode()).isEqualTo(StationFixture.ZIP_CODE);
        assertThat(station.getAddressStreet()).isEqualTo(StationFixture.STREET);
        assertThat(station.getAddressNumber()).isEqualTo(StationFixture.NUMBER);
        assertThat(station.getAddressComplement()).isEqualTo(StationFixture.COMPLEMENT);
        assertThat(station.getAddressNeighborhood()).isEqualTo(StationFixture.NEIGHBORHOOD);
        assertThat(station.getAddressCity()).isEqualTo(StationFixture.CITY);
        assertThat(station.getAddressState()).isEqualTo(StationFixture.STATE);
        assertThat(station.getTimezone()).isEqualTo(StationFixture.TIMEZONE);
        assertThat(station.getActive()).isTrue();
    }

    @Test
    void shouldMapNullMaskedFieldsToEmptyText() {
        StationInputDTO input = StationFixture.validInput();
        input.setCnpj(null);
        input.setStateRegistration(null);
        input.setAddressZipCode(null);

        Station station = mapper.stationInputDTOToStation(input);

        assertThat(station.getCnpj()).isEmpty();
        assertThat(station.getStateRegistration()).isEmpty();
        assertThat(station.getAddressZipCode()).isEmpty();
    }

    @Test
    void shouldMapEntityToResponse() {
        Station station = StationFixture.persistedEntity();

        StationResponseDTO response = mapper.stationToStationResponseDTO(station);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(station);
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<Station> stations = List.of(StationFixture.persistedEntity(), StationFixture.persistedEntity());

        List<StationResponseDTO> responses = mapper.listStationToListStationResponseDTO(stations);

        assertThat(responses)
                .extracting(StationResponseDTO::getId)
                .containsExactly(stations.get(0).getId(), stations.get(1).getId());
    }

    @Test
    void shouldMapEmptyListToEmptyResponseList() {
        assertThat(mapper.listStationToListStationResponseDTO(List.of())).isEmpty();
    }

    @Test
    void shouldUpdateEntityFromInputKeepingId() {
        Station station = StationFixture.persistedEntity();
        UUID originalId = station.getId();
        UUID newCompanyId = UUID.randomUUID();
        StationInputDTO input = StationFixture.validInput(newCompanyId);
        input.setId(UUID.randomUUID());
        input.setName("Posto Novo");
        input.setTimezone(StationFixture.OTHER_TIMEZONE);
        input.setActive(false);

        mapper.updateStationFromDto(input, station);

        assertThat(station.getId()).isEqualTo(originalId);
        assertThat(station.getCompanyId()).isEqualTo(newCompanyId);
        assertThat(station.getName()).isEqualTo("Posto Novo");
        assertThat(station.getCnpj()).isEqualTo(StationFixture.CNPJ);
        assertThat(station.getAddressZipCode()).isEqualTo(StationFixture.ZIP_CODE);
        assertThat(station.getTimezone()).isEqualTo(StationFixture.OTHER_TIMEZONE);
        assertThat(station.getActive()).isFalse();
    }
}
