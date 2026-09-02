package com.br.manager.domain.organization.mapper;

import com.br.manager.domain.organization.dto.StationInputDTO;
import com.br.manager.domain.organization.dto.StationResponseDTO;
import com.br.manager.domain.organization.entity.Station;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StationMapper {

    Station stationInputDTOToStation(StationInputDTO inputDTO);

    StationResponseDTO stationToStationResponseDTO(Station station);

    List<StationResponseDTO> listStationToListStationResponseDTO(List<Station> stations);

    void updateStationFromDto(StationInputDTO dto, @MappingTarget Station entity);
}
