package com.br.manager.domain.operational.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.dto.PumpInputDTO;
import com.br.manager.domain.operational.dto.PumpResponseDTO;
import com.br.manager.domain.operational.entity.Pump;
import com.br.manager.domain.operational.mapper.PumpMapper;
import com.br.manager.domain.operational.repository.PumpRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PumpService {

    @Autowired
    private PumpRepository pumpRepository;

    @Autowired
    private PumpMapper pumpMapper;

    public PumpResponseDTO create(PumpInputDTO inputDTO) {
        try {
            Pump entity = pumpMapper.pumpInputDTOToPump(inputDTO);
            return pumpMapper.pumpToPumpResponseDTO(pumpRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (Exception e) {
            String pumpCode = inputDTO != null && StringUtils.hasText(inputDTO.getCode()) ? inputDTO.getCode() : "";
            throw new BusinessException("Erro ao salvar bomba " + pumpCode, e);
        }
    }

    public PumpResponseDTO update(PumpInputDTO inputDTO) {
        try {
            Pump entity = pumpRepository.findByIdAndActiveTrue(inputDTO.getId());
            if (entity == null) {
                throw new NotFoundBusinessException(String.format("Bomba com ID %s não encontrada", inputDTO.getId()));
            }
            pumpMapper.updatePumpFromDto(inputDTO, entity);
            return pumpMapper.pumpToPumpResponseDTO(pumpRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            String pumpCode = inputDTO != null && StringUtils.hasText(inputDTO.getCode()) ? inputDTO.getCode() : "";
            throw new BusinessException("Erro ao salvar bomba " + pumpCode, e);
        }
    }

    public List<PumpResponseDTO> findAll() {
        return pumpMapper.listPumpToListPumpResponseDTO(pumpRepository.findAllByActiveTrue());
    }

    public List<PumpResponseDTO> findByDescription(String description) {
        if (!StringUtils.hasText(description)) {
            return findAll();
        }
        return pumpMapper.listPumpToListPumpResponseDTO(pumpRepository.findByCodeContainingIgnoreCaseAndActiveTrue(description));
    }

    public PumpResponseDTO find(UUID id) {
        Pump entity = pumpRepository.findByIdAndActiveTrue(id);
        if (entity == null) {
            throw new NotFoundBusinessException(String.format("Bomba com ID %s não encontrada", id));
        }
        return pumpMapper.pumpToPumpResponseDTO(entity);
    }

    public void delete(UUID id) {
        try {
            Pump entity = pumpRepository.findById(id)
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Bomba com ID %s não encontrada", id)));
            entity.setActive(false);
            pumpRepository.saveAndFlush(entity);
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Erro ao excluir bomba", e);
        }
    }

    public Pump getPumpEntityById(UUID id) {
        Pump entity = pumpRepository.findByIdAndActiveTrue(id);
        if (entity == null) {
            throw new NotFoundBusinessException(String.format("Bomba com ID %s não encontrada", id));
        }
        return entity;
    }
}
