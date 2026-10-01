package com.br.manager.domain.shift_cachs.dto;

import com.br.manager.domain.shift_cachs.enums.CashMovementTypeEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class CashMovementInputDTO {

    @NotNull(message = "Movement type is required.")
    private CashMovementTypeEnum movementType;

    private UUID paymentMethodId;

    @NotNull(message = "Amount is required.")
    @DecimalMin(value = "0.00", message = "Amount cannot be negative.")
    private BigDecimal amount;

    @NotNull(message = "Movement date is required.")
    private LocalDateTime occurredAt;

    private String reference;

    private String reason;

    @NotNull(message = "Created by is required.")
    private UUID createdBy;

    private UUID approvedBy;

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
