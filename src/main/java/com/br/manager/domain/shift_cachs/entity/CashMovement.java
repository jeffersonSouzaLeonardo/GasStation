package com.br.manager.domain.shift_cachs.entity;

import com.br.manager.domain.shift_cachs.enums.CashMovementTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cash_movements")
public class CashMovement {

    @Id
@Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "O turno é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @NotNull(message = "O tipo de movimentação é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 25)
    private CashMovementTypeEnum movementType;

    @Column(name = "payment_method_id")
    private UUID paymentMethodId;

    @NotNull(message = "O valor é obrigatório.")
    @DecimalMin(value = "0.00", message = "O valor não pode ser negativo.")
    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @NotNull(message = "A data da movimentação é obrigatória.")
    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "reference", length = 100)
    private String reference;

    @Column(name = "reason", length = 255)
    private String reason;

    @NotNull(message = "O usuário criador é obrigatório.")
    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "approved_by")
    private UUID approvedBy;
public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public CashMovementTypeEnum getMovementType() {
        return movementType;
    }

    public void setMovementType(CashMovementTypeEnum movementType) {
        this.movementType = movementType;
    }

    public UUID getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(UUID paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(UUID approvedBy) {
        this.approvedBy = approvedBy;
    }
}
