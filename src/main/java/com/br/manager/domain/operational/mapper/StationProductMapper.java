package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.StationProductInputDTO;
import com.br.manager.domain.operational.dto.StationProductResponseDTO;
import com.br.manager.domain.operational.entity.StationProduct;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class StationProductMapper {
    public abstract StationProduct stationProductInputDTOToStationProduct(StationProductInputDTO inputDTO);

    public abstract StationProductResponseDTO stationProductToStationProductResponseDTO(StationProduct entity);

    public abstract List<StationProductResponseDTO> listStationProductToListStationProductResponseDTO(List<StationProduct> entities);

    public abstract void updateStationProductFromDto(StationProductInputDTO dto, @MappingTarget StationProduct entity);
}
