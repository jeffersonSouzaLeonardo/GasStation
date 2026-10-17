package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.CustomerInputDTO;
import com.br.manager.domain.operational.dto.CustomerResponseDTO;
import com.br.manager.domain.operational.entity.Customer;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerMapper {
    public Customer customerInputDTOToCustomer(CustomerInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        Customer entity = new Customer();
        BeanUtils.copyProperties(inputDTO, entity);
        return entity;
    }

    public CustomerResponseDTO customerToCustomerResponseDTO(Customer entity) {
        if (entity == null) {
            return null;
        }
        CustomerResponseDTO dto = new CustomerResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public List<CustomerResponseDTO> listCustomerToListCustomerResponseDTO(List<Customer> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(this::customerToCustomerResponseDTO).toList();
    }

    public void updateCustomerFromDto(CustomerInputDTO dto, Customer entity) {
        if (dto == null || entity == null) {
            return;
        }
        BeanUtils.copyProperties(dto, entity);
    }
}
