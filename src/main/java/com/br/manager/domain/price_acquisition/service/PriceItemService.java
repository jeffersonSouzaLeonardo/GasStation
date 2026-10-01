package com.br.manager.domain.price_acquisition.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.price_acquisition.dto.PriceItemInputDTO;
import com.br.manager.domain.price_acquisition.dto.PriceItemResponseDTO;
import com.br.manager.domain.price_acquisition.entity.PriceItem;
import com.br.manager.domain.price_acquisition.entity.PriceTable;
import com.br.manager.domain.price_acquisition.mapper.PriceItemMapper;
import com.br.manager.domain.price_acquisition.repository.PriceItemRepository;
import com.br.manager.domain.price_acquisition.repository.PriceTableRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PriceItemService {

    @Autowired
    private PriceItemRepository priceItemRepository;

    @Autowired
    private PriceItemMapper priceItemMapper;

    @Autowired
    private PriceTableRepository priceTableRepository;

    public List<PriceItemResponseDTO> replaceByPriceTable(PriceTable priceTable, List<PriceItemInputDTO> inputDTOs) {
        try {

            priceItemRepository.deleteByPriceTable(priceTable);

            if (inputDTOs == null || inputDTOs.isEmpty()) {
                return List.of();
            }

            List<PriceItem> priceItems = inputDTOs.stream()
                    .map(dto -> {
                        PriceItem entity = priceItemMapper.priceItemInputDTOToPriceItem(dto);
                        // If the client sent an id but rows were deleted above, clear the id so JPA will insert a new row
                        if (entity.getId() != null) {
                            entity.setId(null);
                        }
                        entity.setPriceTable(priceTable);
                        entity.setActive(dto.getActive() != null ? dto.getActive() : true);
                        return entity;
                    })
                    .toList();

            return priceItemMapper.listPriceItemToListPriceItemResponseDTO(priceItemRepository.saveAllAndFlush(priceItems));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Erro ao substituir itens de preço da tabela " + priceTable.getName(), e);
        }
    }

    public List<PriceItemResponseDTO> saveAllByPriceTable(UUID priceTableId, List<PriceItemInputDTO> inputDTOs) {
        try {
            PriceTable priceTable = priceTableRepository.findById(priceTableId)
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Tabela de preços com ID %s não encontrada", priceTableId)));
            List<PriceItem> priceItems = inputDTOs == null ? List.of() : inputDTOs.stream()
                    .map(dto -> {
                        PriceItem entity = priceItemMapper.priceItemInputDTOToPriceItem(dto);
                        if (entity.getId() == null) {
                                                    }
                        entity.setPriceTable(priceTable);
                        return entity;
                    })
                    .toList();

            return priceItemMapper.listPriceItemToListPriceItemResponseDTO(priceItemRepository.saveAllAndFlush(priceItems));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Erro ao salvar itens de preço da tabela " + priceTableId, e);
        }
    }


    public PriceItemResponseDTO savePriceItem(PriceItemInputDTO inputDTO) {
        try {
            PriceTable priceTable = priceTableRepository.findById(inputDTO.getPriceTableId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Tabela de preços com ID %s não encontrada", inputDTO.getPriceTableId())));
            PriceItem entity = priceItemMapper.priceItemInputDTOToPriceItem(inputDTO);
            entity.setPriceTable(priceTable);
            return priceItemMapper.priceItemToPriceItemResponseDTO(priceItemRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Erro ao salvar itens de preço da tabela " + inputDTO.getPriceTableId(), e);
        }
    }

    public List<PriceItemResponseDTO> savePriceItems(UUID priceTableId, List<PriceItemInputDTO> inputDTOs) {
        return saveAllByPriceTable(priceTableId, inputDTOs);
    }

    public PriceItemResponseDTO update(PriceItemInputDTO inputDTO) {
        try {
            PriceItem entity = priceItemRepository.findByIdAndActiveTrue(inputDTO.getId());
            if (entity == null) {
                throw new NotFoundBusinessException(String.format("Item de preço com ID %s não encontrado", inputDTO.getId()));
            }
            priceItemMapper.updatePriceItemFromDto(inputDTO, entity);
            if (inputDTO.getPriceTableId() != null) {
                PriceTable priceTable = priceTableRepository.findById(inputDTO.getPriceTableId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Tabela de preços com ID %s não encontrada", inputDTO.getPriceTableId())));
                entity.setPriceTable(priceTable);
            }
            return priceItemMapper.priceItemToPriceItemResponseDTO(priceItemRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Erro ao atualizar item de preço", e);
        }
    }

    public void delete(UUID id) {
        try {
            PriceItem priceItem = priceItemRepository.findById(id)
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Item de preço com ID %s não encontrado", id)));
            priceItemRepository.delete(priceItem);
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Erro ao excluir item de preço", e);
        }
    }

    public List<PriceItemResponseDTO> findByPriceTable(UUID priceTableId) {
        return priceItemMapper.listPriceItemToListPriceItemResponseDTO(priceItemRepository.findByPriceTableIdAndActiveTrue(priceTableId));
    }

    public List<PriceItemResponseDTO> findAllByPriceTable(UUID priceTableId) {
        return findByPriceTable(priceTableId);
    }

    public PriceItemResponseDTO find(UUID id) {
        PriceItem entity = priceItemRepository.findByIdAndActiveTrue(id);
        if (entity == null) {
            throw new NotFoundBusinessException(String.format("Item de preço com ID %s não encontrado", id));
        }
        return priceItemMapper.priceItemToPriceItemResponseDTO(entity);
    }

    public List<PriceItemResponseDTO> findAll() {
        return priceItemMapper.listPriceItemToListPriceItemResponseDTO(priceItemRepository.findAllByActiveTrue());
    }

    public PriceItem getPriceItemEntityById(UUID id) {
        PriceItem entity = priceItemRepository.findByIdAndActiveTrue(id);
        if (entity == null) {
            throw new NotFoundBusinessException(String.format("Item de preço com ID %s não encontrado", id));
        }
        return entity;
    }
}
