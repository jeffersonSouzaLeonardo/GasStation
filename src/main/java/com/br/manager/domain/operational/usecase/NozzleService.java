package com.br.manager.domain.operational.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.common.service.SynchronizeService;
import com.br.manager.domain.operational.dto.NozzleInputDTO;
import com.br.manager.domain.operational.dto.NozzleResponseDTO;
import com.br.manager.domain.operational.entity.Nozzle;
import com.br.manager.domain.operational.entity.Product;
import com.br.manager.domain.operational.enums.ProductTypeEnum;
import com.br.manager.domain.operational.mapper.NozzleMapper;
import com.br.manager.domain.operational.repository.NozzleRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NozzleService {

    @Autowired
    private NozzleRepository nozzleRepository;

    @Autowired
    private NozzleMapper nozzleMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private SynchronizeService synchronizeService;

    public NozzleResponseDTO create(NozzleInputDTO inputDTO) {
        try {
            Product product = productService.getProductEntityById(inputDTO.getProductId());
            if (product.getProductType() != ProductTypeEnum.FUEL) {
                throw new BusinessException("Apenas produtos do tipo COMBUSTÍVEL podem ser vendidos pela bomba.");
            }
            Nozzle entity = nozzleMapper.nozzleInputDTOToNozzle(inputDTO);
            if (entity.getId() == null) {
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
            throw new BusinessException("Erro ao salvar bomba " + nozzleCode, e);
        }
    }

    public NozzleResponseDTO update(NozzleInputDTO inputDTO) {
        try {
            Nozzle entity = nozzleRepository.findByIdAndActiveTrue(inputDTO.getId());
            if (entity == null) {
                throw new NotFoundBusinessException(String.format("Bomba com ID %s não encontrada", inputDTO.getId()));
            }
            Product product = productService.getProductEntityById(inputDTO.getProductId());
            if (product.getProductType() != ProductTypeEnum.FUEL) {
                throw new BusinessException("Apenas produtos do tipo COMBUSTÍVEL podem ser vendidos pela bomba.");
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
            throw new BusinessException("Erro ao salvar bomba " + nozzleCode, e);
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
            throw new NotFoundBusinessException(String.format("Bomba com ID %s não encontrada", id));
        }
        return nozzleMapper.nozzleToNozzleResponseDTO(entity);
    }

    public void delete(UUID id) {
        try {
            Nozzle entity = nozzleRepository.findById(id)
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Bomba com ID %s não encontrada", id)));
            entity.setActive(false);
            entity.setDeletedAt(LocalDateTime.now());
            nozzleRepository.saveAndFlush(entity);
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

    public Nozzle getNozzleEntityById(UUID id) {
        Nozzle entity = nozzleRepository.findByIdAndActiveTrue(id);
        if (entity == null) {
            throw new NotFoundBusinessException(String.format("Bomba com ID %s não encontrada", id));
        }
        return entity;
    }

    /**
     * Retorna registros criados, atualizados ou deletados após a última sincronização da tabela informada.
     */
    public List<NozzleResponseDTO> findChangedSinceSynchronize(String tableName) {
        var synchronize = synchronizeService.findByTableName(tableName);
        var since = synchronize.getLastSynchronize();
        if (since == null) {
            // se não houver data, considerar desde a época Unix para garantir consistência
            since = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
        }
        List<Nozzle> changed = nozzleRepository.findAllChangedSince(since);
        return nozzleMapper.listNozzleToListNozzleResponseDTO(changed);
    }
}
