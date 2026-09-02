package com.br.manager.domain.operational.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tanks")
public class Tank {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "Station ID is required.")
    @Column(name = "station_id", nullable = false)
    private UUID stationId;

    @NotBlank(message = "Tank code is required.")
    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @NotNull(message = "Product ID is required.")
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @NotNull(message = "Tank capacity is required.")
    @DecimalMin(value = "0.0001", message = "Capacity must be greater than zero.")
    @Column(name = "capacity_liters", nullable = false, precision = 15, scale = 4)
    private BigDecimal capacityLiters;

    @Column(name = "dead_stock_liters", precision = 15, scale = 4)
    private BigDecimal deadStockLiters = BigDecimal.ZERO;

    @NotNull(message = "Current book liters is required.")
    @Column(name = "current_book_liters", nullable = false, precision = 15, scale = 4)
    private BigDecimal currentBookLiters = BigDecimal.ZERO;

    @NotNull(message = "Active flag is required.")
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @PrePersist
    public void ensureId() { if (this.id == null) { this.id = UUID.randomUUID(); } }

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
