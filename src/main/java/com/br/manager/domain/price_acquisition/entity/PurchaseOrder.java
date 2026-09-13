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
@Table(name = "purchase_orders")
public class PurchaseOrder {

    public static final class Status {
        public static final String DRAFT = "DRAFT";
        public static final String APPROVED = "APPROVED";
        public static final String SENT = "SENT";
        public static final String COMPLETED = "COMPLETED";
        public static final String CANCELED = "CANCELED";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "Station is required.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @NotNull(message = "Supplier is required.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @NotBlank(message = "Purchase order number is required.")
    @Column(name = "number", nullable = false, unique = true)
    private String number;

    @NotBlank(message = "Status is required.")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @NotNull(message = "Ordered date is required.")
    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    @NotNull(message = "Expected date is required.")
    @Column(name = "expected_at", nullable = false)
    private LocalDateTime expectedAt;

    @NotNull(message = "Created by is required.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PurchaseOrderItem> items = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }

    public void setOrderedAt(LocalDateTime orderedAt) {
        this.orderedAt = orderedAt;
    }

    public LocalDateTime getExpectedAt() {
        return expectedAt;
    }

    public void setExpectedAt(LocalDateTime expectedAt) {
        this.expectedAt = expectedAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public List<PurchaseOrderItem> getItems() {
        return items;
    }

    public void setItems(List<PurchaseOrderItem> items) {
        this.items.clear();
        if (items != null) {
            items.forEach(item -> {
                item.setPurchaseOrder(this);
                this.items.add(item);
            });
        }
    }

    public BigDecimal getTotalAmount() {
        return items.stream()
                .map(PurchaseOrderItem::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
