package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.common.StringUtils;
import com.br.manager.domain.operational.dto.CustomerVehicleInputDTO;
import com.br.manager.domain.operational.dto.CustomerVehicleResponseDTO;
import com.br.manager.domain.operational.entity.CustomerVehicle;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerVehicleMapper {
    public CustomerVehicle customerVehicleInputDTOToCustomerVehicle(CustomerVehicleInputDTO inputDTO) {
        CustomerVehicle entity = new CustomerVehicle();
        entity.setId(inputDTO.getId());
        entity.setCustomerId(inputDTO.getCustomerId());
        entity.setPlate(StringUtils.removeMask(inputDTO.getPlate()));
        entity.setBrand(inputDTO.getBrand());
        entity.setModel(inputDTO.getModel());
        entity.setFuelType(inputDTO.getFuelType());
        entity.setActive(inputDTO.getActive());
        return entity;
    }

    public CustomerVehicleResponseDTO customerVehicleToCustomerVehicleResponseDTO(CustomerVehicle entity) {
        CustomerVehicleResponseDTO dto = new CustomerVehicleResponseDTO();
        dto.setId(entity.getId());
        dto.setCustomerId(entity.getCustomerId());
        dto.setPlate(entity.getPlate());
        dto.setBrand(entity.getBrand());
        dto.setModel(entity.getModel());
        dto.setFuelType(entity.getFuelType());
        dto.setActive(entity.getActive());
        return dto;
    }

    public List<CustomerVehicleResponseDTO> listCustomerVehicleToListCustomerVehicleResponseDTO(List<CustomerVehicle> entities) {
        return entities.stream().map(this::customerVehicleToCustomerVehicleResponseDTO).toList();
    }

    public void updateCustomerVehicleFromDto(CustomerVehicleInputDTO dto, CustomerVehicle entity) {
        entity.setCustomerId(dto.getCustomerId());
        entity.setPlate(StringUtils.removeMask(dto.getPlate()));
        entity.setBrand(dto.getBrand());
        entity.setModel(dto.getModel());
        entity.setFuelType(dto.getFuelType());
        entity.setActive(dto.getActive());
    }
}
