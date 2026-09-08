package com.br.manager.domain.price_acquisition.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PriceTableInputDTO {
    private UUID id;
    private UUID stationId;
    private String name;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private String status;
    private UUID createdBy;
    private List<PriceItemInputDTO> priceItems;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public List<PriceItemInputDTO> getPriceItems() {
        return priceItems;
    }

    public void setPriceItems(List<PriceItemInputDTO> priceItems) {
        this.priceItems = priceItems;
    }
}
