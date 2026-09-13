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
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Purchase order with ID %s not found", inputDTO.getPurchaseOrderId())));
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
            throw new BusinessException("Error while creating purchase order item", e);
        }
    }

    public PurchaseOrderItemResponseDTO update(PurchaseOrderItemInputDTO inputDTO) {
        try {
            if (inputDTO == null || inputDTO.getId() == null) {
                throw new BusinessException("Purchase order item ID is required for update.");
            }

            PurchaseOrderItem entity = purchaseOrderItemRepository.findById(inputDTO.getId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Purchase order item with ID %s not found", inputDTO.getId())));

            purchaseOrderItemMapper.updateEntityFromDto(inputDTO, entity);

            if (inputDTO.getPurchaseOrderId() != null) {
                PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(inputDTO.getPurchaseOrderId())
                        .orElseThrow(() -> new NotFoundBusinessException(String.format("Purchase order with ID %s not found", inputDTO.getPurchaseOrderId())));
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
            throw new BusinessException("Error while updating purchase order item", e);
        }
    }

    public void delete(UUID id) {
        try {
            if (!purchaseOrderItemRepository.existsById(id)) {
                throw new NotFoundBusinessException(String.format("Purchase order item with ID %s not found", id));
            }
            purchaseOrderItemRepository.deleteById(id);
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Error while deleting purchase order item", e);
        }
    }

    public PurchaseOrderItemResponseDTO findById(UUID id) {
        PurchaseOrderItem entity = purchaseOrderItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Purchase order item with ID %s not found", id)));
        return purchaseOrderItemMapper.toResponseDTO(entity);
    }

    public List<PurchaseOrderItemResponseDTO> findAll() {
        return purchaseOrderItemMapper.toResponseDTOList(purchaseOrderItemRepository.findAll());
    }

    public List<PurchaseOrderItemResponseDTO> findByPurchaseOrderId(UUID purchaseOrderId) {
        return purchaseOrderItemMapper.toResponseDTOList(purchaseOrderItemRepository.findByPurchaseOrderId(purchaseOrderId));
    }

    private void validatePayload(PurchaseOrderItemInputDTO inputDTO) {
        if (inputDTO == null) {
            throw new BusinessException("Purchase order item payload is required.");
        }
        if (inputDTO.getPurchaseOrderId() == null) {
            throw new BusinessException("Purchase order ID is required.");
        }
        if (inputDTO.getProductId() == null) {
            throw new BusinessException("Product ID is required.");
        }
        if (inputDTO.getQuantity() == null || inputDTO.getQuantity() <= 0) {
            throw new BusinessException("Quantity must be greater than zero.");
        }
        if (inputDTO.getUnitCost() == null) {
            throw new BusinessException("Unit cost is required.");
        }
    }
}
