package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.SupplierInputDTO;
import com.br.manager.domain.operational.dto.SupplierResponseDTO;
import com.br.manager.domain.operational.entity.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    Supplier supplierInputDTOToSupplier(SupplierInputDTO inputDTO);

    SupplierResponseDTO supplierToSupplierResponseDTO(Supplier entity);

    List<SupplierResponseDTO> listSupplierToListSupplierResponseDTO(List<Supplier> entities);

    void updateSupplierFromDto(SupplierInputDTO dto, @MappingTarget Supplier entity);
}
