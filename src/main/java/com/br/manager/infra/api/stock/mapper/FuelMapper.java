package com.br.manager.infra.api.stock.mapper;

import com.br.manager.domain.stock.dto.FuelResponseDTO;
import com.br.manager.domain.stock.entity.Fuel;
import com.br.manager.domain.stock.dto.FuelInputDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FuelMapper {

    Fuel fuelInputToFuelEntity(FuelInputDTO inputDTO);

    FuelResponseDTO fuelEntityToFuelResponseDTO(Fuel fuel);

    List<FuelResponseDTO> listFuelEntityToListFuelResponseDTO(List<Fuel> fuel);
}
