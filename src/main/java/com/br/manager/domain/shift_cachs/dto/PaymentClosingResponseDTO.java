package com.br.manager.domain.shift_cachs.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentClosingResponseDTO {

    private UUID id;
    private UUID shiftId;
    private UUID paymentMethodId;
    private BigDecimal expectedAmount;
    private BigDecimal declaredAmount;
    private BigDecimal differenceAmount;
    private String evidenceReference;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getShiftId() {
        return shiftId;
    }

    public void setShiftId(UUID shiftId) {
        this.shiftId = shiftId;
    }

    public UUID getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(UUID paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public BigDecimal getExpectedAmount() {
        return expectedAmount;
    }

    public void setExpectedAmount(BigDecimal expectedAmount) {
        this.expectedAmount = expectedAmount;
    }

    public BigDecimal getDeclaredAmount() {
        return declaredAmount;
    }

    public void setDeclaredAmount(BigDecimal declaredAmount) {
        this.declaredAmount = declaredAmount;
    }

    public BigDecimal getDifferenceAmount() {
        return differenceAmount;
    }

    public void setDifferenceAmount(BigDecimal differenceAmount) {
        this.differenceAmount = differenceAmount;
    }

    public String getEvidenceReference() {
        return evidenceReference;
    }

    public void setEvidenceReference(String evidenceReference) {
        this.evidenceReference = evidenceReference;
    }
}
