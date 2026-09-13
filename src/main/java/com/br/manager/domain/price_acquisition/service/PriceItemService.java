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

    public List<PriceItemResponseDTO> replaceByPriceTable(UUID priceTableId, List<PriceItemInputDTO> inputDTOs) {
        try {
            PriceTable priceTable = priceTableRepository.findById(priceTableId)
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Price table with ID %s not found", priceTableId)));

            List<PriceItem> currentItems = priceItemRepository.findByPriceTableIdAndActiveTrue(priceTableId);
            currentItems.forEach(item -> item.setActive(false));
            priceItemRepository.saveAllAndFlush(currentItems);

            if (inputDTOs == null || inputDTOs.isEmpty()) {
                return List.of();
            }

            List<PriceItem> priceItems = inputDTOs.stream()
                    .map(dto -> {
                        PriceItem entity = priceItemMapper.priceItemInputDTOToPriceItem(dto);
                        if (entity.getId() == null) {
                            entity.setId(UUID.randomUUID());
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
            throw new BusinessException("Error while replacing price items for price table " + priceTableId, e);
        }
    }

    public List<PriceItemResponseDTO> saveAllByPriceTable(UUID priceTableId, List<PriceItemInputDTO> inputDTOs) {
        try {
            PriceTable priceTable = priceTableRepository.findById(priceTableId)
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Price table with ID %s not found", priceTableId)));
            List<PriceItem> priceItems = inputDTOs == null ? List.of() : inputDTOs.stream()
                    .map(dto -> {
                        PriceItem entity = priceItemMapper.priceItemInputDTOToPriceItem(dto);
                        if (entity.getId() == null) {
                            entity.setId(UUID.randomUUID());
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
            throw new BusinessException("Error while saving price items for price table " + priceTableId, e);
        }
    }


    public PriceItemResponseDTO savePriceItem(PriceItemInputDTO inputDTO) {
        try {
            PriceTable priceTable = priceTableRepository.findById(inputDTO.getPriceTableId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Price table with ID %s not found", inputDTO.getPriceTableId())));
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
            throw new BusinessException("Error while saving price items for price table " + inputDTO.getPriceTableId(), e);
        }
    }

    public List<PriceItemResponseDTO> savePriceItems(UUID priceTableId, List<PriceItemInputDTO> inputDTOs) {
        return saveAllByPriceTable(priceTableId, inputDTOs);
    }

    public PriceItemResponseDTO update(PriceItemInputDTO inputDTO) {
        try {
            PriceItem entity = priceItemRepository.findByIdAndActiveTrue(inputDTO.getId());
            if (entity == null) {
                throw new NotFoundBusinessException(String.format("Price item with ID %s not found", inputDTO.getId()));
            }
            priceItemMapper.updatePriceItemFromDto(inputDTO, entity);
            if (inputDTO.getPriceTableId() != null) {
                PriceTable priceTable = priceTableRepository.findById(inputDTO.getPriceTableId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Price table with ID %s not found", inputDTO.getPriceTableId())));
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
            throw new BusinessException("Error while updating price item", e);
        }
    }

    public PriceItemResponseDTO edit(PriceItemInputDTO inputDTO) {
        return update(inputDTO);
    }

    public void delete(UUID id) {
        try {
            PriceItem priceItem = priceItemRepository.findById(id)
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Price item with ID %s not found", id)));
            priceItemRepository.delete(priceItem);
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Error while deleting price item", e);
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
            throw new NotFoundBusinessException(String.format("Price item with ID %s not found", id));
        }
        return priceItemMapper.priceItemToPriceItemResponseDTO(entity);
    }

    public List<PriceItemResponseDTO> findAll() {
        return priceItemMapper.listPriceItemToListPriceItemResponseDTO(priceItemRepository.findAllByActiveTrue());
    }

    public PriceItem getPriceItemEntityById(UUID id) {
        PriceItem entity = priceItemRepository.findByIdAndActiveTrue(id);
        if (entity == null) {
            throw new NotFoundBusinessException(String.format("Price item with ID %s not found", id));
        }
        return entity;
    }
}

