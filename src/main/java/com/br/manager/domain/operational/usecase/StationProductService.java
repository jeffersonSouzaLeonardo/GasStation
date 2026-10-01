package com.br.manager.domain.operational.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.dto.StationProductInputDTO;
import com.br.manager.domain.operational.dto.StationProductResponseDTO;
import com.br.manager.domain.operational.entity.StationProduct;
import com.br.manager.domain.operational.mapper.StationProductMapper;
import com.br.manager.domain.operational.repository.StationProductRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class StationProductService {

    @Autowired
    private StationProductRepository stationProductRepository;

    @Autowired
    private StationProductMapper stationProductMapper;

    public StationProductResponseDTO create(StationProductInputDTO inputDTO) {
        try {
            StationProduct entity = stationProductMapper.stationProductInputDTOToStationProduct(inputDTO);
            if (entity.getId() == null) {
                            }
            return stationProductMapper.stationProductToStationProductResponseDTO(stationProductRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (Exception e) {
            String productLabel = inputDTO != null && inputDTO.getProductId() != null ? inputDTO.getProductId().toString() : "";
            throw new BusinessException("Erro ao salvar produto do posto " + productLabel, e);
        }
    }

    public StationProductResponseDTO update(StationProductInputDTO inputDTO) {
        try {
            StationProduct entity = stationProductRepository.findByIdAndActiveTrue(inputDTO.getId());
            if (entity == null) {
                throw new NotFoundBusinessException(String.format("Produto do posto com ID %s não encontrado", inputDTO.getId()));
            }
            stationProductMapper.updateStationProductFromDto(inputDTO, entity);
            return stationProductMapper.stationProductToStationProductResponseDTO(stationProductRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            String productLabel = inputDTO != null && inputDTO.getProductId() != null ? inputDTO.getProductId().toString() : "";
            throw new BusinessException("Erro ao salvar produto do posto " + productLabel, e);
        }
    }

    public List<StationProductResponseDTO> findAll() {
        return stationProductMapper.listStationProductToListStationProductResponseDTO(stationProductRepository.findAllByActiveTrue());
    }

    public List<StationProductResponseDTO> findByDescription(String description) {
        if (!StringUtils.hasText(description)) {
            return findAll();
        }
        try {
            UUID productId = UUID.fromString(description);
            return stationProductMapper.listStationProductToListStationProductResponseDTO(
                    stationProductRepository.findByProductIdAndActiveTrue(productId));
        } catch (IllegalArgumentException exception) {
            return List.of();
        }
    }

    public StationProductResponseDTO find(UUID id) {
        StationProduct entity = stationProductRepository.findByIdAndActiveTrue(id);
        if (entity == null) {
            throw new NotFoundBusinessException(String.format("Produto do posto com ID %s não encontrado", id));
        }
        return stationProductMapper.stationProductToStationProductResponseDTO(entity);
    }

    public void delete(UUID id) {
        try {
            StationProduct entity = stationProductRepository.findById(id)
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Produto do posto com ID %s não encontrado", id)));
            entity.setActive(false);
            stationProductRepository.saveAndFlush(entity);
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Erro ao excluir produto do posto", e);
        }
    }

    public StationProduct getStationProductEntityById(UUID id) {
        StationProduct entity = stationProductRepository.findByIdAndActiveTrue(id);
        if (entity == null) {
            throw new NotFoundBusinessException(String.format("Produto do posto com ID %s não encontrado", id));
        }
        return entity;
    }
}
