package com.br.manager.domain.shift_cachs.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentClosingInputDTO {

    @NotNull(message = "Payment method ID is required.")
    private UUID paymentMethodId;

    @NotNull(message = "Expected amount is required.")
    @DecimalMin(value = "0.00", message = "Expected amount cannot be negative.")
    private BigDecimal expectedAmount;

    @NotNull(message = "Declared amount is required.")
    @DecimalMin(value = "0.00", message = "Declared amount cannot be negative.")
    private BigDecimal declaredAmount;

    private String evidenceReference;

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

    public String getEvidenceReference() {
        return evidenceReference;
    }

    public void setEvidenceReference(String evidenceReference) {
        this.evidenceReference = evidenceReference;
    }
}
