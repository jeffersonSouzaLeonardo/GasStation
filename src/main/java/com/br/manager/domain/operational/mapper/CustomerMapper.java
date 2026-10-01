package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.CustomerInputDTO;
import com.br.manager.domain.operational.dto.CustomerResponseDTO;
import com.br.manager.domain.operational.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CustomerMapper {
    public abstract Customer customerInputDTOToCustomer(CustomerInputDTO inputDTO);

    public abstract CustomerResponseDTO customerToCustomerResponseDTO(Customer entity);

    public abstract List<CustomerResponseDTO> listCustomerToListCustomerResponseDTO(List<Customer> entities);

    public abstract void updateCustomerFromDto(CustomerInputDTO dto, @MappingTarget Customer entity);
}
