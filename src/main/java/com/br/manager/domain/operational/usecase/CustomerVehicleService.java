package com.br.manager.domain.operational.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.dto.CustomerVehicleInputDTO;
import com.br.manager.domain.operational.dto.CustomerVehicleResponseDTO;
import com.br.manager.domain.operational.entity.CustomerVehicle;
import com.br.manager.domain.operational.mapper.CustomerVehicleMapper;
import com.br.manager.domain.operational.repository.CustomerVehicleRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerVehicleService {
    @Autowired private CustomerVehicleRepository customerVehicleRepository;
    @Autowired private CustomerVehicleMapper customerVehicleMapper;

    public CustomerVehicleResponseDTO create(CustomerVehicleInputDTO inputDTO) {
        try {
            CustomerVehicle entity = customerVehicleMapper.customerVehicleInputDTOToCustomerVehicle(inputDTO);
            if (entity.getId() == null) { entity.setId(UUID.randomUUID()); }
            return customerVehicleMapper.customerVehicleToCustomerVehicleResponseDTO(customerVehicleRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException e) {
            throw new BusinessException(e.getConstraintViolations().stream().map(v -> v.getMessage()).toList().toString());
        } catch (Exception e) {
            throw new BusinessException("Error while saving customer vehicle", e);
        }
    }

    public CustomerVehicleResponseDTO update(CustomerVehicleInputDTO inputDTO) {
        try {
            CustomerVehicle entity = customerVehicleRepository.findByIdAndActiveTrue(inputDTO.getId());
            if (entity == null) { throw new NotFoundBusinessException(String.format("Customer vehicle with ID %s not found", inputDTO.getId())); }
            customerVehicleMapper.updateCustomerVehicleFromDto(inputDTO, entity);
            return customerVehicleMapper.customerVehicleToCustomerVehicleResponseDTO(customerVehicleRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException e) {
            throw new BusinessException(e.getConstraintViolations().stream().map(v -> v.getMessage()).toList().toString());
        } catch (NotFoundBusinessException e) { throw e; } catch (Exception e) { throw new BusinessException("Error while updating customer vehicle", e); }
    }

    public List<CustomerVehicleResponseDTO> findAll() { return customerVehicleMapper.listCustomerVehicleToListCustomerVehicleResponseDTO(customerVehicleRepository.findAllByActiveTrue()); }

    public List<CustomerVehicleResponseDTO> findByDescription(String description) {
        if (!StringUtils.hasText(description)) return findAll();
        return customerVehicleMapper.listCustomerVehicleToListCustomerVehicleResponseDTO(customerVehicleRepository.findByPlateContainingIgnoreCaseAndActiveTrue(description));
    }

    public CustomerVehicleResponseDTO find(UUID id) {
        CustomerVehicle entity = customerVehicleRepository.findByIdAndActiveTrue(id);
        if (entity == null) throw new NotFoundBusinessException(String.format("Customer vehicle with ID %s not found", id));
        return customerVehicleMapper.customerVehicleToCustomerVehicleResponseDTO(entity);
    }

    public void delete(UUID id) {
        CustomerVehicle entity = customerVehicleRepository.findById(id).orElseThrow(() -> new NotFoundBusinessException(String.format("Customer vehicle with ID %s not found", id)));
        entity.setActive(false);
        customerVehicleRepository.saveAndFlush(entity);
    }
}
