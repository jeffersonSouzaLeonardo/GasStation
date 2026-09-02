package com.br.manager.domain.operational.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.dto.NozzleInputDTO;
import com.br.manager.domain.operational.dto.NozzleResponseDTO;
import com.br.manager.domain.operational.entity.Nozzle;
import com.br.manager.domain.operational.entity.Product;
import com.br.manager.domain.operational.enums.ProductTypeEnum;
import com.br.manager.domain.operational.mapper.NozzleMapper;
import com.br.manager.domain.operational.repository.NozzleRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class NozzleService {

    @Autowired
    private NozzleRepository nozzleRepository;

    @Autowired
    private NozzleMapper nozzleMapper;

    @Autowired
    private ProductService productService;

    public NozzleResponseDTO create(NozzleInputDTO inputDTO) {
        try {
            Product product = productService.getProductEntityById(inputDTO.getProductId());
            if (product.getProductType() != ProductTypeEnum.FUEL) {
                throw new BusinessException("Only FUEL products can be sold by nozzle.");
            }
            Nozzle entity = nozzleMapper.nozzleInputDTOToNozzle(inputDTO);
            if (entity.getId() == null) {
                entity.setId(UUID.randomUUID());
            }
            return nozzleMapper.nozzleToNozzleResponseDTO(nozzleRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception e) {
            String nozzleCode = inputDTO != null && StringUtils.hasText(inputDTO.getCode()) ? inputDTO.getCode() : "";
            throw new BusinessException("Error while saving nozzle " + nozzleCode, e);
        }
    }

    public NozzleResponseDTO update(NozzleInputDTO inputDTO) {
        try {
            Nozzle entity = nozzleRepository.findByIdAndActiveTrue(inputDTO.getId());
            if (entity == null) {
                throw new NotFoundBusinessException(String.format("Nozzle with ID %s not found", inputDTO.getId()));
            }
            Product product = productService.getProductEntityById(inputDTO.getProductId());
            if (product.getProductType() != ProductTypeEnum.FUEL) {
                throw new BusinessException("Only FUEL products can be sold by nozzle.");
            }
            nozzleMapper.updateNozzleFromDto(inputDTO, entity);
            return nozzleMapper.nozzleToNozzleResponseDTO(nozzleRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception e) {
            String nozzleCode = inputDTO != null && StringUtils.hasText(inputDTO.getCode()) ? inputDTO.getCode() : "";
            throw new BusinessException("Error while saving nozzle " + nozzleCode, e);
        }
    }

    public List<NozzleResponseDTO> findAll() {
        return nozzleMapper.listNozzleToListNozzleResponseDTO(nozzleRepository.findAllByActiveTrue());
    }

    public List<NozzleResponseDTO> findByDescription(String description) {
        if (!StringUtils.hasText(description)) {
            return findAll();
        }
        return nozzleMapper.listNozzleToListNozzleResponseDTO(nozzleRepository.findByCodeContainingIgnoreCaseAndActiveTrue(description));
    }

    public NozzleResponseDTO find(UUID id) {
        Nozzle entity = nozzleRepository.findByIdAndActiveTrue(id);
        if (entity == null) {
            throw new NotFoundBusinessException(String.format("Nozzle with ID %s not found", id));
        }
        return nozzleMapper.nozzleToNozzleResponseDTO(entity);
    }

    public void delete(UUID id) {
        try {
            Nozzle entity = nozzleRepository.findById(id)
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Nozzle with ID %s not found", id)));
            entity.setActive(false);
            nozzleRepository.saveAndFlush(entity);
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Error while deleting nozzle", e);
        }
    }

    public Nozzle getNozzleEntityById(UUID id) {
        Nozzle entity = nozzleRepository.findByIdAndActiveTrue(id);
        if (entity == null) {
            throw new NotFoundBusinessException(String.format("Nozzle with ID %s not found", id));
        }
        return entity;
    }
}
