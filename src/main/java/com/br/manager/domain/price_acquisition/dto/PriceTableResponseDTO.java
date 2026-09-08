package com.br.manager.domain.price_acquisition.dto;

import java.util.List;
import java.util.UUID;

public class PriceTableResponseDTO {
    private UUID id;
    private UUID stationId;
    private String name;
    private String validFrom;
    private String validTo;
    private String status;
    private UUID createdBy;
    private List<PriceItemResponseDTO> priceItems;

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

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidTo() {
        return validTo;
    }

    public void setValidTo(String validTo) {
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

    public List<PriceItemResponseDTO> getPriceItems() {
        return priceItems;
    }

    public void setPriceItems(List<PriceItemResponseDTO> priceItems) {
        this.priceItems = priceItems;
    }
}
