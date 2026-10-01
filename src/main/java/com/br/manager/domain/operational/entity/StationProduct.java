package com.br.manager.domain.operational.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "station_products")
public class StationProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "O ID do produto é obrigatório.")
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "min_stock", precision = 15, scale = 4)
    private BigDecimal minStock = BigDecimal.ZERO;

    @Column(name = "reorder_point", precision = 15, scale = 4)
    private BigDecimal reorderPoint = BigDecimal.ZERO;

    @NotNull(message = "O campo ativo é obrigatório.")
    @Column(name = "active", nullable = false)
    private Boolean active = true;
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public BigDecimal getMinStock() { return minStock; }
    public void setMinStock(BigDecimal minStock) { this.minStock = minStock; }
    public BigDecimal getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(BigDecimal reorderPoint) { this.reorderPoint = reorderPoint; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
