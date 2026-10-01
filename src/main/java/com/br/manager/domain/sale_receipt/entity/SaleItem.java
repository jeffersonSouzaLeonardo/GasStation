package com.br.manager.domain.sale_receipt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "sale_item")
public class SaleItem {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    @NotNull(message = "Line number is required.")
    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    @NotNull(message = "Product ID is required.")
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "nozzle_id")
    private UUID nozzleId;

    @Column(name = "tank_id")
    private UUID tankId;

    @NotNull(message = "Quantity is required.")
    @Column(name = "quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    @NotNull(message = "Unit price is required.")
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal unitPrice;

    @NotNull(message = "Discount amount is required.")
    @Column(name = "discount_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "meter_start", precision = 19, scale = 4)
    private BigDecimal meterStart;

    @Column(name = "meter_end", precision = 19, scale = 4)
    private BigDecimal meterEnd;
public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
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

    public UUID getTankId() {
        return tankId;
    }

    public void setTankId(UUID tankId) {
        this.tankId = tankId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getMeterStart() {
        return meterStart;
    }

    public void setMeterStart(BigDecimal meterStart) {
        this.meterStart = meterStart;
    }

    public BigDecimal getMeterEnd() {
        return meterEnd;
    }

    public void setMeterEnd(BigDecimal meterEnd) {
        this.meterEnd = meterEnd;
    }
}
