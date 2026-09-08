package com.br.manager.domain.price_acquisition.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class PriceItemResponseDTO {
    private UUID id;
    private UUID priceTableId;
    private UUID productId;
    private UUID nozzleId;
    private BigDecimal unitPrice;
    private BigDecimal minPrice;
    private BigDecimal maxDiscountPercent;
    private Boolean active;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getPriceTableId() { return priceTableId; }
    public void setPriceTableId(UUID priceTableId) { this.priceTableId = priceTableId; }

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }

    public UUID getNozzleId() { return nozzleId; }
    public void setNozzleId(UUID nozzleId) { this.nozzleId = nozzleId; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }

    public BigDecimal getMaxDiscountPercent() { return maxDiscountPercent; }
    public void setMaxDiscountPercent(BigDecimal maxDiscountPercent) { this.maxDiscountPercent = maxDiscountPercent; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
