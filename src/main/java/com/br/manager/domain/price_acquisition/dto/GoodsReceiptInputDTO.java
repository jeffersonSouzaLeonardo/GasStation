package com.br.manager.domain.price_acquisition.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class GoodsReceiptInputDTO {
    private UUID id;
    private UUID stationId;
    private UUID supplierId;
    private UUID purchaseOrderId;
    private String invoiceNumber;
    private String invoiceKey;
    private LocalDateTime receivedAt;
    private String status;
    private BigDecimal freightAmount;
    private BigDecimal otherCosts;
    private UUID receivedBy;
    private List<GoodsReceiptItemInputDTO> items;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getSupplierId() { return supplierId; }
    public void setSupplierId(UUID supplierId) { this.supplierId = supplierId; }

    public UUID getPurchaseOrderId() { return purchaseOrderId; }
    public void setPurchaseOrderId(UUID purchaseOrderId) { this.purchaseOrderId = purchaseOrderId; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public String getInvoiceKey() { return invoiceKey; }
    public void setInvoiceKey(String invoiceKey) { this.invoiceKey = invoiceKey; }

    public LocalDateTime getReceivedAt() { return receivedAt; }
    public void setReceivedAt(LocalDateTime receivedAt) { this.receivedAt = receivedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getFreightAmount() { return freightAmount; }
    public void setFreightAmount(BigDecimal freightAmount) { this.freightAmount = freightAmount; }

    public BigDecimal getOtherCosts() { return otherCosts; }
    public void setOtherCosts(BigDecimal otherCosts) { this.otherCosts = otherCosts; }

    public UUID getReceivedBy() { return receivedBy; }
    public void setReceivedBy(UUID receivedBy) { this.receivedBy = receivedBy; }

    public List<GoodsReceiptItemInputDTO> getItems() { return items; }
    public void setItems(List<GoodsReceiptItemInputDTO> items) { this.items = items; }
}
