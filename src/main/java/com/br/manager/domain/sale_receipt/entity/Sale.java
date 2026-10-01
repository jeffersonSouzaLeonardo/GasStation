package com.br.manager.domain.sale_receipt.entity;

import com.br.manager.domain.sale_receipt.enums.FiscalStatus;
import com.br.manager.domain.sale_receipt.enums.SaleStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sale")
public class Sale {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "Station ID is required.")
    @Column(name = "station_id", nullable = false)
    private UUID stationId;

    @NotNull(message = "Shift ID is required.")
    @Column(name = "shift_id", nullable = false)
    private UUID shiftId;

    @NotNull(message = "Sale number is required.")
    @Column(name = "sale_number", nullable = false, length = 50)
    private String saleNumber;

    @NotNull(message = "Status is required.")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private SaleStatus status;

    @NotNull(message = "Sold at is required.")
    @Column(name = "sold_at", nullable = false)
    private Instant soldAt;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "vehicle_id")
    private UUID vehicleId;

    @Column(name = "attendant_user_id")
    private UUID attendantUserId;

    @NotNull(message = "Cashier user ID is required.")
    @Column(name = "cashier_user_id", nullable = false)
    private UUID cashierUserId;

    @NotNull(message = "Subtotal is required.")
    @Column(name = "subtotal", nullable = false, precision = 19, scale = 4)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @NotNull(message = "Discount amount is required.")
    @Column(name = "discount_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "fiscal_status", length = 40)
    private FiscalStatus fiscalStatus;

    @Column(name = "notes", length = 1000)
    private String notes;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SaleItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SalePayment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SaleRefund> refunds = new ArrayList<>();
public boolean isFinanciallyLocked() {
        return status == SaleStatus.PAID || status == SaleStatus.PENDING_CREDIT;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getStationId() {
        return stationId;
    }

    public void setStationId(UUID stationId) {
        this.stationId = stationId;
    }

    public UUID getShiftId() {
        return shiftId;
    }

    public void setShiftId(UUID shiftId) {
        this.shiftId = shiftId;
    }

    public String getSaleNumber() {
        return saleNumber;
    }

    public void setSaleNumber(String saleNumber) {
        this.saleNumber = saleNumber;
    }

    public SaleStatus getStatus() {
        return status;
    }

    public void setStatus(SaleStatus status) {
        this.status = status;
    }

    public Instant getSoldAt() {
        return soldAt;
    }

    public void setSoldAt(Instant soldAt) {
        this.soldAt = soldAt;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(UUID vehicleId) {
        this.vehicleId = vehicleId;
    }

    public UUID getAttendantUserId() {
        return attendantUserId;
    }

    public void setAttendantUserId(UUID attendantUserId) {
        this.attendantUserId = attendantUserId;
    }

    public UUID getCashierUserId() {
        return cashierUserId;
    }

    public void setCashierUserId(UUID cashierUserId) {
        this.cashierUserId = cashierUserId;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public FiscalStatus getFiscalStatus() {
        return fiscalStatus;
    }

    public void setFiscalStatus(FiscalStatus fiscalStatus) {
        this.fiscalStatus = fiscalStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<SaleItem> getItems() {
        return items;
    }

    public void setItems(List<SaleItem> items) {
        this.items.clear();
        if (items != null) {
            items.forEach(this::addItem);
        }
    }

    public void addItem(SaleItem item) {
        item.setSale(this);
        this.items.add(item);
    }

    public List<SalePayment> getPayments() {
        return payments;
    }

    public void setPayments(List<SalePayment> payments) {
        this.payments.clear();
        if (payments != null) {
            payments.forEach(this::addPayment);
        }
    }

    public void addPayment(SalePayment payment) {
        payment.setSale(this);
        this.payments.add(payment);
    }

    public List<SaleRefund> getRefunds() {
        return refunds;
    }

    public void setRefunds(List<SaleRefund> refunds) {
        this.refunds.clear();
        if (refunds != null) {
            refunds.forEach(this::addRefund);
        }
    }

    public void addRefund(SaleRefund refund) {
        refund.setSale(this);
        this.refunds.add(refund);
    }
}
