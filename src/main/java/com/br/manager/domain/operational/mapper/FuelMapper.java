package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.FuelInputDTO;
import com.br.manager.domain.operational.dto.FuelResponseDTO;
import com.br.manager.domain.operational.entity.Fuel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class FuelMapper {
    public abstract Fuel fuelInputToFuelEntity(FuelInputDTO inputDTO);

    public abstract FuelResponseDTO fuelEntityToFuelResponseDTO(Fuel fuel);

    public abstract List<FuelResponseDTO> listFuelEntityToListFuelResponseDTO(List<Fuel> fuel);

    public abstract void updateFuelFromDto(FuelInputDTO dto, @MappingTarget Fuel entity);
}
