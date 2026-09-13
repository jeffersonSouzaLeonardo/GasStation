package com.br.manager.domain.price_acquisition.mapper;

import com.br.manager.domain.price_acquisition.dto.PurchaseOrderItemInputDTO;
import com.br.manager.domain.price_acquisition.dto.PurchaseOrderItemResponseDTO;
import com.br.manager.domain.price_acquisition.entity.PurchaseOrder;
import com.br.manager.domain.price_acquisition.entity.PurchaseOrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class PurchaseOrderItemMapper {

    public PurchaseOrderItem toEntity(PurchaseOrderItemInputDTO dto) {
        if (dto == null) {
            return null;
        }

        PurchaseOrderItem entity = new PurchaseOrderItem();
        entity.setId(dto.getId());

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(dto.getPurchaseOrderId());
        entity.setPurchaseOrder(purchaseOrder);

        entity.setProductId(dto.getProductId());
        entity.setQuantity(dto.getQuantity());
        entity.setUnitCost(dto.getUnitCost());
        return entity;
    }

    public PurchaseOrderItemResponseDTO toResponseDTO(PurchaseOrderItem item) {
        if (item == null) {
            return null;
        }

        PurchaseOrderItemResponseDTO dto = new PurchaseOrderItemResponseDTO();
        dto.setId(item.getId());
        dto.setPurchaseOrderId(item.getPurchaseOrder() != null ? item.getPurchaseOrder().getId() : null);
        dto.setProductId(item.getProductId());
        dto.setQuantity(item.getQuantity());
        dto.setUnitCost(item.getUnitCost());
        dto.setTotalCost(item.getTotalCost());
        return dto;
    }

    public List<PurchaseOrderItemResponseDTO> toResponseDTOList(List<PurchaseOrderItem> items) {
        if (items == null) {
            return null;
        }

        return items.stream()
                .filter(Objects::nonNull)
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public void updateEntityFromDto(PurchaseOrderItemInputDTO dto, PurchaseOrderItem entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getProductId() != null) {
            entity.setProductId(dto.getProductId());
        }
        if (dto.getQuantity() != null) {
            entity.setQuantity(dto.getQuantity());
        }
        if (dto.getUnitCost() != null) {
            entity.setUnitCost(dto.getUnitCost());
        }
    }
}
