package com.br.manager.domain.operational.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.dto.SupplierInputDTO;
import com.br.manager.domain.operational.dto.SupplierResponseDTO;
import com.br.manager.domain.operational.entity.Supplier;
import com.br.manager.domain.operational.mapper.SupplierMapper;
import com.br.manager.domain.operational.repository.SupplierRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class SupplierService {
    @Autowired private SupplierRepository supplierRepository;
    @Autowired private SupplierMapper supplierMapper;

    public SupplierResponseDTO create(SupplierInputDTO inputDTO) {
        try {
            Supplier entity = supplierMapper.supplierInputDTOToSupplier(inputDTO);
            if (entity.getId() == null) { entity.setId(UUID.randomUUID()); }
            return supplierMapper.supplierToSupplierResponseDTO(supplierRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException e) {
            throw new BusinessException(e.getConstraintViolations().stream().map(v -> v.getMessage()).toList().toString());
        } catch (Exception e) {
            throw new BusinessException("Error while saving supplier", e);
        }
    }

    public SupplierResponseDTO update(SupplierInputDTO inputDTO) {
        try {
            Supplier entity = supplierRepository.findByIdAndActiveTrue(inputDTO.getId());
            if (entity == null) { throw new NotFoundBusinessException(String.format("Supplier with ID %s not found", inputDTO.getId())); }
            supplierMapper.updateSupplierFromDto(inputDTO, entity);
            return supplierMapper.supplierToSupplierResponseDTO(supplierRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException e) {
            throw new BusinessException(e.getConstraintViolations().stream().map(v -> v.getMessage()).toList().toString());
        } catch (NotFoundBusinessException e) { throw e; } catch (Exception e) { throw new BusinessException("Error while updating supplier", e); }
    }

    public List<SupplierResponseDTO> findAll() { return supplierMapper.listSupplierToListSupplierResponseDTO(supplierRepository.findAllByActiveTrue()); }

    public List<SupplierResponseDTO> findByDescription(String description) {
        if (!StringUtils.hasText(description)) return findAll();
        return supplierMapper.listSupplierToListSupplierResponseDTO(supplierRepository.findByLegalNameContainingIgnoreCaseAndActiveTrue(description));
    }

    public SupplierResponseDTO find(UUID id) {
        Supplier entity = supplierRepository.findByIdAndActiveTrue(id);
        if (entity == null) throw new NotFoundBusinessException(String.format("Supplier with ID %s not found", id));
        return supplierMapper.supplierToSupplierResponseDTO(entity);
    }

    public void delete(UUID id) {
        Supplier entity = supplierRepository.findById(id).orElseThrow(() -> new NotFoundBusinessException(String.format("Supplier with ID %s not found", id)));
        entity.setActive(false);
        supplierRepository.saveAndFlush(entity);
    }
}
