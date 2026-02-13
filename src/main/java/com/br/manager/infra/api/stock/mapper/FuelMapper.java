package com.br.manager.infra.api.stock.mapper;

import com.br.manager.domain.stock.dto.FuelInputDTO;
import com.br.manager.domain.stock.dto.FuelResponseDTO;
import com.br.manager.domain.stock.entity.Fuel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FuelMapper {

    Fuel fuelInputToFuelEntity(FuelInputDTO inputDTO);

    FuelResponseDTO fuelEntityToFuelResponseDTO(Fuel fuel);

    List<FuelResponseDTO> listFuelEntityToListFuelResponseDTO(List<Fuel> fuel);

    void updateFuelFromDto(FuelInputDTO dto, @MappingTarget Fuel entity);
}
