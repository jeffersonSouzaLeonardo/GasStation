package com.br.manager.domain.sale_receipt.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class SaleItemInputDTO {
    private UUID id;
    private UUID saleId;
    private Integer lineNumber;
    private UUID productId;
    private UUID nozzleId;
    private UUID tankId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountAmount;
    private BigDecimal meterStart;
    private BigDecimal meterEnd;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSaleId() { return saleId; }
    public void setSaleId(UUID saleId) { this.saleId = saleId; }
    public Integer getLineNumber() { return lineNumber; }
    public void setLineNumber(Integer lineNumber) { this.lineNumber = lineNumber; }
    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public UUID getNozzleId() { return nozzleId; }
    public void setNozzleId(UUID nozzleId) { this.nozzleId = nozzleId; }
    public UUID getTankId() { return tankId; }
    public void setTankId(UUID tankId) { this.tankId = tankId; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public BigDecimal getMeterStart() { return meterStart; }
    public void setMeterStart(BigDecimal meterStart) { this.meterStart = meterStart; }
    public BigDecimal getMeterEnd() { return meterEnd; }
    public void setMeterEnd(BigDecimal meterEnd) { this.meterEnd = meterEnd; }
}
