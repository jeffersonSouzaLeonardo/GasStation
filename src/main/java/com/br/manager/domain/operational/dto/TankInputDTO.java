package com.br.manager.domain.operational.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class TankInputDTO {
    private UUID id;
    private UUID stationId;
    private String code;
    private UUID productId;
    private BigDecimal capacityLiters;
    private BigDecimal deadStockLiters;
    private BigDecimal currentBookLiters;
    private Boolean active = true;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getStationId() { return stationId; }
    public void setStationId(UUID stationId) { this.stationId = stationId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public BigDecimal getCapacityLiters() { return capacityLiters; }
    public void setCapacityLiters(BigDecimal capacityLiters) { this.capacityLiters = capacityLiters; }
    public BigDecimal getDeadStockLiters() { return deadStockLiters; }
    public void setDeadStockLiters(BigDecimal deadStockLiters) { this.deadStockLiters = deadStockLiters; }
    public BigDecimal getCurrentBookLiters() { return currentBookLiters; }
    public void setCurrentBookLiters(BigDecimal currentBookLiters) { this.currentBookLiters = currentBookLiters; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
