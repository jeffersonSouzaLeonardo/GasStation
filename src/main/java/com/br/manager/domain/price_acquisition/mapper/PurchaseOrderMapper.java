package com.br.manager.domain.price_acquisition.mapper;

import com.br.manager.domain.operational.entity.Supplier;
import com.br.manager.domain.organization.entity.Station;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.price_acquisition.dto.PurchaseOrderInputDTO;
import com.br.manager.domain.price_acquisition.dto.PurchaseOrderItemResponseDTO;
import com.br.manager.domain.price_acquisition.dto.PurchaseOrderResponseDTO;
import com.br.manager.domain.price_acquisition.entity.PurchaseOrder;
import com.br.manager.domain.price_acquisition.entity.PurchaseOrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class PurchaseOrderMapper {

    public PurchaseOrder toEntity(PurchaseOrderInputDTO dto) {
        if (dto == null) {
            return null;
        }

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(dto.getId());
        purchaseOrder.setNumber(dto.getNumber());
        purchaseOrder.setStatus(dto.getStatus() != null ? dto.getStatus() : PurchaseOrder.Status.DRAFT);
        purchaseOrder.setOrderedAt(dto.getOrderedAt());
        purchaseOrder.setExpectedAt(dto.getExpectedAt());

        if (dto.getStationId() != null) {
            Station station = new Station();
            station.setId(dto.getStationId());
            purchaseOrder.setStation(station);
        }

        if (dto.getSupplierId() != null) {
            Supplier supplier = new Supplier();
            supplier.setId(dto.getSupplierId());
            purchaseOrder.setSupplier(supplier);
        }

        if (dto.getCreatedBy() != null) {
            User user = new User();
            user.setId(dto.getCreatedBy());
            purchaseOrder.setCreatedBy(user);
        }

        purchaseOrder.setItems(new java.util.ArrayList<>());

        return purchaseOrder;
    }

    public PurchaseOrderResponseDTO toResponseDTO(PurchaseOrder purchaseOrder) {
        if (purchaseOrder == null) {
            return null;
        }

        PurchaseOrderResponseDTO dto = new PurchaseOrderResponseDTO();
        dto.setId(purchaseOrder.getId());
        dto.setStationId(purchaseOrder.getStation() != null ? purchaseOrder.getStation().getId() : null);
        dto.setSupplierId(purchaseOrder.getSupplier() != null ? purchaseOrder.getSupplier().getId() : null);
        dto.setNumber(purchaseOrder.getNumber());
        dto.setStatus(purchaseOrder.getStatus());
        dto.setOrderedAt(purchaseOrder.getOrderedAt());
        dto.setExpectedAt(purchaseOrder.getExpectedAt());
        dto.setCreatedBy(purchaseOrder.getCreatedBy() != null ? purchaseOrder.getCreatedBy().getId() : null);
        dto.setTotalAmount(purchaseOrder.getTotalAmount());

        if (purchaseOrder.getItems() != null) {
            dto.setItems(purchaseOrder.getItems().stream()
                    .filter(Objects::nonNull)
                    .map(this::toItemResponseDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public PurchaseOrderItemResponseDTO toItemResponseDTO(PurchaseOrderItem item) {
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
}
