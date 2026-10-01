package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.SupplierInputDTO;
import com.br.manager.domain.operational.dto.SupplierResponseDTO;
import com.br.manager.domain.operational.entity.Supplier;
import com.br.manager.domain.common.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SupplierMapper {
    public Supplier supplierInputDTOToSupplier(SupplierInputDTO inputDTO) {
        Supplier entity = new Supplier();
        entity.setId(inputDTO.getId());
        entity.setLegalName(inputDTO.getLegalName());
        entity.setCnpjCpf(StringUtils.removeMask(inputDTO.getCnpjCpf()));
        entity.setStateRegistration(StringUtils.removeMask(inputDTO.getStateRegistration()));
        entity.setEmail(inputDTO.getEmail());
        entity.setPhone(StringUtils.removeMask(inputDTO.getPhone()));
        entity.setAddressStreet(inputDTO.getAddressStreet());
        entity.setAddressNumber(inputDTO.getAddressNumber());
        entity.setAddressComplement(inputDTO.getAddressComplement());
        entity.setAddressNeighborhood(inputDTO.getAddressNeighborhood());
        entity.setAddressCity(inputDTO.getAddressCity());
        entity.setAddressState(inputDTO.getAddressState());
        entity.setAddressZipCode(StringUtils.removeMask(inputDTO.getAddressZipCode()));
        entity.setActive(inputDTO.getActive());
        return entity;
    }

    public SupplierResponseDTO supplierToSupplierResponseDTO(Supplier entity) {
        SupplierResponseDTO dto = new SupplierResponseDTO();
        dto.setId(entity.getId());
        dto.setLegalName(entity.getLegalName());
        dto.setCnpjCpf(entity.getCnpjCpf());
        dto.setStateRegistration(entity.getStateRegistration());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setAddressStreet(entity.getAddressStreet());
        dto.setAddressNumber(entity.getAddressNumber());
        dto.setAddressComplement(entity.getAddressComplement());
        dto.setAddressNeighborhood(entity.getAddressNeighborhood());
        dto.setAddressCity(entity.getAddressCity());
        dto.setAddressState(entity.getAddressState());
        dto.setAddressZipCode(entity.getAddressZipCode());
        dto.setActive(entity.getActive());
        return dto;
    }

    public List<SupplierResponseDTO> listSupplierToListSupplierResponseDTO(List<Supplier> entities) {
        return entities.stream().map(this::supplierToSupplierResponseDTO).toList();
    }

    public void updateSupplierFromDto(SupplierInputDTO dto, Supplier entity) {
        entity.setLegalName(dto.getLegalName());
        entity.setCnpjCpf(StringUtils.removeMask(dto.getCnpjCpf()));
        entity.setStateRegistration(StringUtils.removeMask(dto.getStateRegistration()));
        entity.setEmail(dto.getEmail());
        entity.setPhone(StringUtils.removeMask(dto.getPhone()));
        entity.setAddressStreet(dto.getAddressStreet());
        entity.setAddressNumber(dto.getAddressNumber());
        entity.setAddressComplement(dto.getAddressComplement());
        entity.setAddressNeighborhood(dto.getAddressNeighborhood());
        entity.setAddressCity(dto.getAddressCity());
        entity.setAddressState(dto.getAddressState());
        entity.setAddressZipCode(StringUtils.removeMask(dto.getAddressZipCode()));
        entity.setActive(dto.getActive());
    }
}
