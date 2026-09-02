package com.br.manager.domain.operational.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.dto.TankInputDTO;
import com.br.manager.domain.operational.dto.TankResponseDTO;
import com.br.manager.domain.operational.entity.Product;
import com.br.manager.domain.operational.entity.Tank;
import com.br.manager.domain.operational.enums.ProductTypeEnum;
import com.br.manager.domain.operational.mapper.TankMapper;
import com.br.manager.domain.operational.repository.TankRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class TankService {

    @Autowired
    private TankRepository tankRepository;

    @Autowired
    private TankMapper tankMapper;

    @Autowired
    private ProductService productService;

    public TankResponseDTO create(TankInputDTO inputDTO) {
        try {
            Product product = productService.getProductEntityById(inputDTO.getProductId());
            if (product.getProductType() != ProductTypeEnum.FUEL) {
                throw new BusinessException("Only FUEL products can be assigned to a tank.");
            }
            Tank tank = tankMapper.tankInputToTankEntity(inputDTO);
            if (tank.getId() == null) {
                tank.setId(UUID.randomUUID());
            }
            return tankMapper.tankEntityToTankResponseDTO(tankRepository.saveAndFlush(tank));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (Exception e) {
            String tankCode = inputDTO != null && StringUtils.hasText(inputDTO.getCode()) ? inputDTO.getCode() : "";
            throw new BusinessException("Error while saving tank " + tankCode, e);
        }
    }

    public TankResponseDTO update(TankInputDTO inputDTO) {
        try {
            Tank tank = tankRepository.findByIdAndActiveTrue(inputDTO.getId());
            if (tank == null) {
                throw new NotFoundBusinessException(String.format("Tank with ID %s not found", inputDTO.getId()));
            }
            Product product = productService.getProductEntityById(inputDTO.getProductId());
            if (product.getProductType() != ProductTypeEnum.FUEL) {
                throw new BusinessException("Only FUEL products can be assigned to a tank.");
            }
            tankMapper.updateTankFromDto(inputDTO, tank);
            return tankMapper.tankEntityToTankResponseDTO(tankRepository.saveAndFlush(tank));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            String tankCode = inputDTO != null && StringUtils.hasText(inputDTO.getCode()) ? inputDTO.getCode() : "";
            throw new BusinessException("Error while updating tank " + tankCode, e);
        }
    }

    public List<TankResponseDTO> findAll() {
        return tankMapper.listTankEntityToListTankResponseDTO(tankRepository.findAllByActiveTrue());
    }

    public List<TankResponseDTO> findByDescription(String description) {
        if (!StringUtils.hasText(description)) {
            return findAll();
        }
        return tankMapper.listTankEntityToListTankResponseDTO(
                tankRepository.findByCodeContainingIgnoreCaseAndActiveTrue(description));
    }

    public TankResponseDTO find(UUID id) {
        Tank tank = tankRepository.findByIdAndActiveTrue(id);
        if (tank == null) {
            throw new NotFoundBusinessException(String.format("Tank with ID %s not found", id));
        }
        return tankMapper.tankEntityToTankResponseDTO(tank);
    }

    public void delete(UUID id) {
        try {
            Tank tank = tankRepository.findById(id)
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Tank with ID %s not found", id)));
            tank.setActive(false);
            tankRepository.saveAndFlush(tank);
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Error while deleting tank", e);
        }
    }

    public Tank getTankEntityById(UUID id) {
        Tank tank = tankRepository.findByIdAndActiveTrue(id);
        if (tank == null) {
            throw new NotFoundBusinessException(String.format("Tank with ID %s not found", id));
        }
        return tank;
    }
}
