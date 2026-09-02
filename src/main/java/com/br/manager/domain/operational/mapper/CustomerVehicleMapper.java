package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.CustomerVehicleInputDTO;
import com.br.manager.domain.operational.dto.CustomerVehicleResponseDTO;
import com.br.manager.domain.operational.entity.CustomerVehicle;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerVehicleMapper {
    CustomerVehicle customerVehicleInputDTOToCustomerVehicle(CustomerVehicleInputDTO inputDTO);

    CustomerVehicleResponseDTO customerVehicleToCustomerVehicleResponseDTO(CustomerVehicle entity);

    List<CustomerVehicleResponseDTO> listCustomerVehicleToListCustomerVehicleResponseDTO(List<CustomerVehicle> entities);

    void updateCustomerVehicleFromDto(CustomerVehicleInputDTO dto, @MappingTarget CustomerVehicle entity);
}
