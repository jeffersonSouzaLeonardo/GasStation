package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.StationProductInputDTO;
import com.br.manager.domain.operational.dto.StationProductResponseDTO;
import com.br.manager.domain.operational.entity.StationProduct;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StationProductMapper {
    StationProduct stationProductInputDTOToStationProduct(StationProductInputDTO inputDTO);

    StationProductResponseDTO stationProductToStationProductResponseDTO(StationProduct entity);

    List<StationProductResponseDTO> listStationProductToListStationProductResponseDTO(List<StationProduct> entities);

    void updateStationProductFromDto(StationProductInputDTO dto, @MappingTarget StationProduct entity);
}
