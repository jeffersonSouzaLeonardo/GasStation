package com.br.manager.domain.organization.mapper;

import com.br.manager.domain.common.StringUtils;
import com.br.manager.domain.organization.dto.StationInputDTO;
import com.br.manager.domain.organization.dto.StationResponseDTO;
import com.br.manager.domain.organization.entity.Station;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StationMapper {

    public Station stationInputDTOToStation(StationInputDTO inputDTO) {
        Station station = new Station();
        station.setId(inputDTO.getId());
        station.setCompanyId(inputDTO.getCompanyId());
        station.setName(inputDTO.getName());
        station.setCnpj(StringUtils.removeMask(inputDTO.getCnpj()));
        station.setStateRegistration(StringUtils.removeMask(inputDTO.getStateRegistration()));
        station.setAddressStreet(inputDTO.getAddressStreet());
        station.setAddressNumber(inputDTO.getAddressNumber());
        station.setAddressComplement(inputDTO.getAddressComplement());
        station.setAddressNeighborhood(inputDTO.getAddressNeighborhood());
        station.setAddressCity(inputDTO.getAddressCity());
        station.setAddressState(inputDTO.getAddressState());
        station.setAddressZipCode(StringUtils.removeMask(inputDTO.getAddressZipCode()));
        station.setTimezone(inputDTO.getTimezone());
        station.setActive(inputDTO.getActive());
        return station;
    }

    public StationResponseDTO stationToStationResponseDTO(Station station) {
        StationResponseDTO dto = new StationResponseDTO();
        dto.setId(station.getId());
        dto.setCompanyId(station.getCompanyId());
        dto.setName(station.getName());
        dto.setCnpj(station.getCnpj());
        dto.setStateRegistration(station.getStateRegistration());
        dto.setAddressStreet(station.getAddressStreet());
        dto.setAddressNumber(station.getAddressNumber());
        dto.setAddressComplement(station.getAddressComplement());
        dto.setAddressNeighborhood(station.getAddressNeighborhood());
        dto.setAddressCity(station.getAddressCity());
        dto.setAddressState(station.getAddressState());
        dto.setAddressZipCode(station.getAddressZipCode());
        dto.setTimezone(station.getTimezone());
        dto.setActive(station.getActive());
        return dto;
    }

    public List<StationResponseDTO> listStationToListStationResponseDTO(List<Station> stations) {
        return stations.stream().map(this::stationToStationResponseDTO).toList();
    }

    public void updateStationFromDto(StationInputDTO dto, Station entity) {
        entity.setCompanyId(dto.getCompanyId());
        entity.setName(dto.getName());
        entity.setCnpj(StringUtils.removeMask(dto.getCnpj()));
        entity.setStateRegistration(StringUtils.removeMask(dto.getStateRegistration()));
        entity.setAddressStreet(dto.getAddressStreet());
        entity.setAddressNumber(dto.getAddressNumber());
        entity.setAddressComplement(dto.getAddressComplement());
        entity.setAddressNeighborhood(dto.getAddressNeighborhood());
        entity.setAddressCity(dto.getAddressCity());
        entity.setAddressState(dto.getAddressState());
        entity.setAddressZipCode(StringUtils.removeMask(dto.getAddressZipCode()));
        entity.setTimezone(dto.getTimezone());
        entity.setActive(dto.getActive());
    }
}
