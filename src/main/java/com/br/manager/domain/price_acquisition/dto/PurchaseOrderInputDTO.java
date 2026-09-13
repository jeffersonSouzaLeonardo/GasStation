package com.br.manager.domain.price_acquisition.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PurchaseOrderInputDTO {

    private UUID id;
    private UUID stationId;
    private UUID supplierId;
    private String number;
    private String status;
    private LocalDateTime orderedAt;
    private LocalDateTime expectedAt;
    private UUID createdBy;
    private List<PurchaseOrderItemInputDTO> items;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getStationId() {
        return stationId;
    }

    public void setStationId(UUID stationId) {
        this.stationId = stationId;
    }

    public UUID getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(UUID supplierId) {
        this.supplierId = supplierId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }

    public void setOrderedAt(LocalDateTime orderedAt) {
        this.orderedAt = orderedAt;
    }

    public LocalDateTime getExpectedAt() {
        return expectedAt;
    }

    public void setExpectedAt(LocalDateTime expectedAt) {
        this.expectedAt = expectedAt;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public List<PurchaseOrderItemInputDTO> getItems() {
        return items;
    }

    public void setItems(List<PurchaseOrderItemInputDTO> items) {
        this.items = items;
    }
}
