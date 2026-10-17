package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.FuelInputDTO;
import com.br.manager.domain.operational.dto.FuelResponseDTO;
import com.br.manager.domain.operational.entity.Fuel;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FuelMapper {
    public Fuel fuelInputToFuelEntity(FuelInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        Fuel entity = new Fuel();
        BeanUtils.copyProperties(inputDTO, entity);
        return entity;
    }

    public FuelResponseDTO fuelEntityToFuelResponseDTO(Fuel fuel) {
        if (fuel == null) {
            return null;
        }
        FuelResponseDTO dto = new FuelResponseDTO();
        BeanUtils.copyProperties(fuel, dto);
        return dto;
    }

    public List<FuelResponseDTO> listFuelEntityToListFuelResponseDTO(List<Fuel> fuel) {
        if (fuel == null) {
            return null;
        }
        return fuel.stream().map(this::fuelEntityToFuelResponseDTO).toList();
    }

    public void updateFuelFromDto(FuelInputDTO dto, Fuel entity) {
        if (dto == null || entity == null) {
            return;
        }
        BeanUtils.copyProperties(dto, entity);
    }
}
