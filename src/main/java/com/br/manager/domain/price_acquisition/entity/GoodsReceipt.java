package com.br.manager.domain.price_acquisition.entity;

import com.br.manager.domain.operational.entity.Supplier;
import com.br.manager.domain.organization.entity.Station;
import com.br.manager.domain.organization.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "goods_receipts")
public class GoodsReceipt {

    public static final class Status {
        public static final String DRAFT = "DRAFT";
        public static final String RECEIVED = "RECEIVED";
        public static final String PARTIALLY_RECEIVED = "PARTIALLY_RECEIVED";
        public static final String CANCELED = "CANCELED";
    }

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Supplier is required.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    @NotBlank(message = "Invoice number is required.")
    @Column(name = "invoice_number", nullable = false)
    private String invoiceNumber;

    @NotBlank(message = "Invoice key is required.")
    @Column(name = "invoice_key", nullable = false)
    private String invoiceKey;

    @NotNull(message = "Received date is required.")
    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    @NotBlank(message = "Status is required.")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @NotNull(message = "Freight amount is required.")
    @Column(name = "freight_amount", precision = 19, scale = 4, nullable = false)
    private BigDecimal freightAmount;

    @NotNull(message = "Other costs are required.")
    @Column(name = "other_costs", precision = 19, scale = 4, nullable = false)
    private BigDecimal otherCosts;

    @NotNull(message = "Received by is required.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_by", nullable = false)
    private User receivedBy;

    @OneToMany(mappedBy = "goodsReceipt", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GoodsReceiptItem> items = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceKey() {
        return invoiceKey;
    }

    public void setInvoiceKey(String invoiceKey) {
        this.invoiceKey = invoiceKey;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getFreightAmount() {
        return freightAmount;
    }

    public void setFreightAmount(BigDecimal freightAmount) {
        this.freightAmount = freightAmount;
    }

    public BigDecimal getOtherCosts() {
        return otherCosts;
    }

    public void setOtherCosts(BigDecimal otherCosts) {
        this.otherCosts = otherCosts;
    }

    public User getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(User receivedBy) {
        this.receivedBy = receivedBy;
    }

    public List<GoodsReceiptItem> getItems() {
        return items;
    }

    public void setItems(List<GoodsReceiptItem> items) {
        this.items.clear();
        if (items != null) {
            items.forEach(item -> {
                item.setGoodsReceipt(this);
                this.items.add(item);
            });
        }
    }
}
