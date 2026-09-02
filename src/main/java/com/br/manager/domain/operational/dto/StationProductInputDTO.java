package com.br.manager.domain.operational.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class StationProductInputDTO {
    private UUID id;
    private UUID stationId;
    private UUID productId;
    private BigDecimal minStock = BigDecimal.ZERO;
    private BigDecimal reorderPoint = BigDecimal.ZERO;
    private Boolean active = true;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getStationId() { return stationId; }
    public void setStationId(UUID stationId) { this.stationId = stationId; }
    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public BigDecimal getMinStock() { return minStock; }
    public void setMinStock(BigDecimal minStock) { this.minStock = minStock; }
    public BigDecimal getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(BigDecimal reorderPoint) { this.reorderPoint = reorderPoint; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
