package com.br.manager.domain.operational.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.dto.CustomerInputDTO;
import com.br.manager.domain.operational.dto.CustomerResponseDTO;
import com.br.manager.domain.operational.entity.Customer;
import com.br.manager.domain.operational.mapper.CustomerMapper;
import com.br.manager.domain.operational.repository.CustomerRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {
    @Autowired private CustomerRepository customerRepository;
    @Autowired private CustomerMapper customerMapper;

    public CustomerResponseDTO create(CustomerInputDTO inputDTO) {
        try {
            Customer entity = customerMapper.customerInputDTOToCustomer(inputDTO);
            if (entity.getId() == null) { entity.setId(UUID.randomUUID()); }
            return customerMapper.customerToCustomerResponseDTO(customerRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException e) {
            throw new BusinessException(e.getConstraintViolations().stream().map(v -> v.getMessage()).toList().toString());
        } catch (Exception e) {
            throw new BusinessException("Error while saving customer", e);
        }
    }

    public CustomerResponseDTO update(CustomerInputDTO inputDTO) {
        try {
            Customer entity = customerRepository.findByIdAndActiveTrue(inputDTO.getId());
            if (entity == null) { throw new NotFoundBusinessException(String.format("Customer with ID %s not found", inputDTO.getId())); }
            customerMapper.updateCustomerFromDto(inputDTO, entity);
            return customerMapper.customerToCustomerResponseDTO(customerRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException e) {
            throw new BusinessException(e.getConstraintViolations().stream().map(v -> v.getMessage()).toList().toString());
        } catch (NotFoundBusinessException e) { throw e; } catch (Exception e) { throw new BusinessException("Error while updating customer", e); }
    }

    public List<CustomerResponseDTO> findAll() { return customerMapper.listCustomerToListCustomerResponseDTO(customerRepository.findAllByActiveTrue()); }

    public List<CustomerResponseDTO> findByDescription(String description) {
        if (!StringUtils.hasText(description)) return findAll();
        return customerMapper.listCustomerToListCustomerResponseDTO(customerRepository.findByNameContainingIgnoreCaseAndActiveTrue(description));
    }

    public CustomerResponseDTO find(UUID id) {
        Customer entity = customerRepository.findByIdAndActiveTrue(id);
        if (entity == null) throw new NotFoundBusinessException(String.format("Customer with ID %s not found", id));
        return customerMapper.customerToCustomerResponseDTO(entity);
    }

    public void delete(UUID id) {
        Customer entity = customerRepository.findById(id).orElseThrow(() -> new NotFoundBusinessException(String.format("Customer with ID %s not found", id)));
        entity.setActive(false);
        customerRepository.saveAndFlush(entity);
    }
}
