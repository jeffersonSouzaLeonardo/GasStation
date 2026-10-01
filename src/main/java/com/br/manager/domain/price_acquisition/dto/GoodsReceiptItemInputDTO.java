package com.br.manager.domain.price_acquisition.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class GoodsReceiptItemInputDTO {
    private UUID id;
    private UUID goodsReceiptId;
    private UUID productId;
    private UUID tankId;
    private BigDecimal quantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getGoodsReceiptId() { return goodsReceiptId; }
    public void setGoodsReceiptId(UUID goodsReceiptId) { this.goodsReceiptId = goodsReceiptId; }

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }

    public UUID getTankId() { return tankId; }
    public void setTankId(UUID tankId) { this.tankId = tankId; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }

    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
}
