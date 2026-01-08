package com.br.manager.infra.api.stock.mapper;

import com.br.manager.domain.stock.dto.FuelInputDTO;
import com.br.manager.domain.stock.dto.FuelResponseDTO;
import com.br.manager.domain.stock.entity.Fuel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FuelMapper {

    @Mapping(target = "unitFuelEnum", source = "unit")
    @Mapping(target = "statusFuelEnum", source = "status")
    Fuel fuelInputToFuelEntity(FuelInputDTO inputDTO);

    @Mapping(target = "unit", source = "unitFuelEnum")
    @Mapping(target = "status", source = "statusFuelEnum")
    FuelResponseDTO fuelEntityToFuelResponseDTO(Fuel fuel);

    List<FuelResponseDTO> listFuelEntityToListFuelResponseDTO(List<Fuel> fuel);
}
