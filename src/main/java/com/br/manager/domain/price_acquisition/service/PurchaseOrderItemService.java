package com.br.manager.domain.price_acquisition.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.price_acquisition.dto.PurchaseOrderItemInputDTO;
import com.br.manager.domain.price_acquisition.dto.PurchaseOrderItemResponseDTO;
import com.br.manager.domain.price_acquisition.entity.PurchaseOrder;
import com.br.manager.domain.price_acquisition.entity.PurchaseOrderItem;
import com.br.manager.domain.price_acquisition.mapper.PurchaseOrderItemMapper;
import com.br.manager.domain.price_acquisition.repository.PurchaseOrderItemRepository;
import com.br.manager.domain.price_acquisition.repository.PurchaseOrderRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PurchaseOrderItemService {

    @Autowired
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderItemMapper purchaseOrderItemMapper;

    public PurchaseOrderItemResponseDTO create(PurchaseOrderItemInputDTO inputDTO) {
        try {
            validatePayload(inputDTO);

            PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(inputDTO.getPurchaseOrderId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Pedido de compra com ID %s não encontrado", inputDTO.getPurchaseOrderId())));
            PurchaseOrderItem entity = purchaseOrderItemMapper.toEntity(inputDTO);
            entity.setPurchaseOrder(purchaseOrder);
            return purchaseOrderItemMapper.toResponseDTO(purchaseOrderItemRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Erro ao criar item do pedido de compra", e);
        }
    }

    public PurchaseOrderItemResponseDTO update(PurchaseOrderItemInputDTO inputDTO) {
        try {
            if (inputDTO == null || inputDTO.getId() == null) {
                throw new BusinessException("O ID do item do pedido de compra é obrigatório para atualização.");
            }

            PurchaseOrderItem entity = purchaseOrderItemRepository.findById(inputDTO.getId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Item do pedido de compra com ID %s não encontrado", inputDTO.getId())));

            purchaseOrderItemMapper.updateEntityFromDto(inputDTO, entity);

            if (inputDTO.getPurchaseOrderId() != null) {
                PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(inputDTO.getPurchaseOrderId())
                        .orElseThrow(() -> new NotFoundBusinessException(String.format("Pedido de compra com ID %s não encontrado", inputDTO.getPurchaseOrderId())));
                entity.setPurchaseOrder(purchaseOrder);
            }

            return purchaseOrderItemMapper.toResponseDTO(purchaseOrderItemRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Erro ao atualizar item do pedido de compra", e);
        }
    }

    public void delete(UUID id) {
        try {
            if (!purchaseOrderItemRepository.existsById(id)) {
                throw new NotFoundBusinessException(String.format("Item do pedido de compra com ID %s não encontrado", id));
            }
            purchaseOrderItemRepository.deleteById(id);
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Erro ao excluir item do pedido de compra", e);
        }
    }

    public PurchaseOrderItemResponseDTO findById(UUID id) {
        PurchaseOrderItem entity = purchaseOrderItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Item do pedido de compra com ID %s não encontrado", id)));
        return purchaseOrderItemMapper.toResponseDTO(entity);
    }

    public List<PurchaseOrderItemResponseDTO> findAll() {
        return purchaseOrderItemMapper.toResponseDTOList(purchaseOrderItemRepository.findAll());
    }

    public List<PurchaseOrderItemResponseDTO> findByPurchaseOrderId(UUID purchaseOrderId) {
        return purchaseOrderItemMapper.toResponseDTOList(purchaseOrderItemRepository.findByPurchaseOrderId(purchaseOrderId));
    }

    public List<PurchaseOrderItemResponseDTO> replaceByPurchaseOrder(PurchaseOrder purchaseOrder, List<PurchaseOrderItemInputDTO> inputDTOs) {
        try {
            purchaseOrderItemRepository.deleteByPurchaseOrder(purchaseOrder);

            if (inputDTOs == null || inputDTOs.isEmpty()) {
                return List.of();
            }

            List<PurchaseOrderItem> items = inputDTOs.stream()
                    .map(dto -> {
                        PurchaseOrderItem entity = purchaseOrderItemMapper.toEntity(dto);
                        if (entity.getId() != null) {
                            entity.setId(null);
                        }
                        entity.setPurchaseOrder(purchaseOrder);
                        return entity;
                    })
                    .toList();

            return purchaseOrderItemMapper.toResponseDTOList(purchaseOrderItemRepository.saveAllAndFlush(items));
        } catch (jakarta.validation.ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Erro ao substituir itens do pedido de compra " + purchaseOrder.getId(), e);
        }
    }

    public List<PurchaseOrderItemResponseDTO> saveAllByPurchaseOrder(UUID purchaseOrderId, List<PurchaseOrderItemInputDTO> inputDTOs) {
        try {
            PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Pedido de compra com ID %s não encontrado", purchaseOrderId)));

            List<PurchaseOrderItem> items = inputDTOs == null ? List.of() : inputDTOs.stream()
                    .map(dto -> {
                        PurchaseOrderItem entity = purchaseOrderItemMapper.toEntity(dto);
                        entity.setPurchaseOrder(purchaseOrder);
                        return entity;
                    })
                    .toList();

            return purchaseOrderItemMapper.toResponseDTOList(purchaseOrderItemRepository.saveAllAndFlush(items));
        } catch (jakarta.validation.ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Erro ao salvar itens do pedido de compra " + purchaseOrderId, e);
        }
    }

    public List<PurchaseOrderItemResponseDTO> savePurchaseOrderItems(UUID purchaseOrderId, List<PurchaseOrderItemInputDTO> inputDTOs) {
        return saveAllByPurchaseOrder(purchaseOrderId, inputDTOs);
    }

    private void validatePayload(PurchaseOrderItemInputDTO inputDTO) {
        if (inputDTO == null) {
            throw new BusinessException("O payload do item do pedido de compra é obrigatório.");
        }
        if (inputDTO.getPurchaseOrderId() == null) {
            throw new BusinessException("O ID do pedido de compra é obrigatório.");
        }
        if (inputDTO.getProductId() == null) {
            throw new BusinessException("O ID do produto é obrigatório.");
        }
        if (inputDTO.getQuantity() == null || inputDTO.getQuantity() <= 0) {
            throw new BusinessException("A quantidade deve ser maior que zero.");
        }
        if (inputDTO.getUnitCost() == null) {
            throw new BusinessException("O custo unitário é obrigatório.");
        }
    }
}
