package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.CustomerInputDTO;
import com.br.manager.domain.operational.dto.CustomerResponseDTO;
import com.br.manager.domain.operational.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer customerInputDTOToCustomer(CustomerInputDTO inputDTO);

    CustomerResponseDTO customerToCustomerResponseDTO(Customer entity);

    List<CustomerResponseDTO> listCustomerToListCustomerResponseDTO(List<Customer> entities);

    void updateCustomerFromDto(CustomerInputDTO dto, @MappingTarget Customer entity);
}
