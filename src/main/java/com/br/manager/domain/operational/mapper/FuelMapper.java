package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.FuelInputDTO;
import com.br.manager.domain.operational.dto.FuelResponseDTO;
import com.br.manager.domain.operational.entity.Fuel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FuelMapper {

    Fuel fuelInputToFuelEntity(FuelInputDTO inputDTO);

    FuelResponseDTO fuelEntityToFuelResponseDTO(Fuel fuel);

    List<FuelResponseDTO> listFuelEntityToListFuelResponseDTO(List<Fuel> fuel);

    void updateFuelFromDto(FuelInputDTO dto, @MappingTarget Fuel entity);
}
