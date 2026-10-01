package com.br.manager.domain.shift_cachs.entity;

import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "shifts")
public class Shift {

    @Id
@Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "O ID do posto é obrigatório.")
    @Column(name = "station_id", nullable = false)
    private UUID stationId;

    @NotNull(message = "O ID do usuário caixa é obrigatório.")
    @Column(name = "cashier_user_id", nullable = false)
    private UUID cashierUserId;

    @NotNull(message = "A data de abertura é obrigatória.")
    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @NotNull(message = "O caixa inicial é obrigatório.")
    @DecimalMin(value = "0.00", message = "O caixa inicial não pode ser negativo.")
    @Column(name = "opening_cash", nullable = false, precision = 18, scale = 2)
    private BigDecimal openingCash;

    @NotNull(message = "O status do turno é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ShiftStatusEnum status;

    @NotNull(message = "O usuário que abriu é obrigatório.")
    @Column(name = "opened_by", nullable = false)
    private UUID openedBy;

    @Column(name = "closed_by")
    private UUID closedBy;

    @Column(name = "notes", length = 500)
    private String notes;

    @OneToMany(mappedBy = "shift", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShiftAllocation> allocations = new ArrayList<>();

    @OneToMany(mappedBy = "shift", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NozzleReading> nozzleReadings = new ArrayList<>();

    @OneToMany(mappedBy = "shift", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CashMovement> cashMovements = new ArrayList<>();

    @OneToMany(mappedBy = "shift", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentClosing> paymentClosings = new ArrayList<>();
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

    public UUID getCashierUserId() {
        return cashierUserId;
    }

    public void setCashierUserId(UUID cashierUserId) {
        this.cashierUserId = cashierUserId;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public BigDecimal getOpeningCash() {
        return openingCash;
    }

    public void setOpeningCash(BigDecimal openingCash) {
        this.openingCash = openingCash;
    }

    public ShiftStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ShiftStatusEnum status) {
        this.status = status;
    }

    public UUID getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(UUID openedBy) {
        this.openedBy = openedBy;
    }

    public UUID getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(UUID closedBy) {
        this.closedBy = closedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<ShiftAllocation> getAllocations() {
        return allocations;
    }

    public void setAllocations(List<ShiftAllocation> allocations) {
        this.allocations = allocations;
    }

    public List<NozzleReading> getNozzleReadings() {
        return nozzleReadings;
    }

    public void setNozzleReadings(List<NozzleReading> nozzleReadings) {
        this.nozzleReadings = nozzleReadings;
    }

    public List<CashMovement> getCashMovements() {
        return cashMovements;
    }

    public void setCashMovements(List<CashMovement> cashMovements) {
        this.cashMovements = cashMovements;
    }

    public List<PaymentClosing> getPaymentClosings() {
        return paymentClosings;
    }

    public void setPaymentClosings(List<PaymentClosing> paymentClosings) {
        this.paymentClosings = paymentClosings;
    }
}
