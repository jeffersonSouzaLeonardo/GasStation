package com.br.manager.domain.operational.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "station_products")
public class StationProduct {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "Station ID is required.")
    @Column(name = "station_id", nullable = false)
    private UUID stationId;

    @NotNull(message = "Product ID is required.")
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "min_stock", precision = 15, scale = 4)
    private BigDecimal minStock = BigDecimal.ZERO;

    @Column(name = "reorder_point", precision = 15, scale = 4)
    private BigDecimal reorderPoint = BigDecimal.ZERO;

    @NotNull(message = "Active flag is required.")
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @PrePersist
    public void ensureId() { if (this.id == null) { this.id = UUID.randomUUID(); } }

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
