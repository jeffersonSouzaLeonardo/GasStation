package com.br.manager.domain.price_acquisition.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "price_items")
public class PriceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "Price table is required.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_table_id", nullable = false)
    private PriceTable priceTable;

    @NotNull(message = "Product ID is required.")
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "nozzle_id")
    private UUID nozzleId;

    @NotNull(message = "Unit price is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than zero.")
    @Column(name = "unit_price", precision = 10, scale = 4, nullable = false)
    private BigDecimal unitPrice;

    @DecimalMin(value = "0.0", message = "Min price cannot be negative.")
    @Column(name = "min_price", precision = 10, scale = 4)
    private BigDecimal minPrice;

    @DecimalMin(value = "0.0", message = "Max discount percent cannot be negative.")
    @DecimalMax(value = "100.0", message = "Max discount percent cannot exceed 100%.")
    @Column(name = "max_discount_percent", precision = 5, scale = 2)
    private BigDecimal maxDiscountPercent;

    @NotNull(message = "Active flag is required.")
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    public PriceItem() {
    }

    // Getters e Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public PriceTable getPriceTable() {
        return priceTable;
    }

    public void setPriceTable(PriceTable priceTable) {
        this.priceTable = priceTable;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public UUID getNozzleId() {
        return nozzleId;
    }

    public void setNozzleId(UUID nozzleId) {
        this.nozzleId = nozzleId;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxDiscountPercent() {
        return maxDiscountPercent;
    }

    public void setMaxDiscountPercent(BigDecimal maxDiscountPercent) {
        this.maxDiscountPercent = maxDiscountPercent;
    }
}