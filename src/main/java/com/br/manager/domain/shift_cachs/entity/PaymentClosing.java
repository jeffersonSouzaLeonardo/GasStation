package com.br.manager.domain.shift_cachs.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payment_closings")
public class PaymentClosing {

    @Id
@Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "O turno é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @NotNull(message = "O ID da forma de pagamento é obrigatório.")
    @Column(name = "payment_method_id", nullable = false)
    private UUID paymentMethodId;

    @NotNull(message = "O valor esperado é obrigatório.")
    @DecimalMin(value = "0.00", message = "O valor esperado não pode ser negativo.")
    @Column(name = "expected_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal expectedAmount;

    @NotNull(message = "O valor declarado é obrigatório.")
    @DecimalMin(value = "0.00", message = "O valor declarado não pode ser negativo.")
    @Column(name = "declared_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal declaredAmount;

    @NotNull(message = "O valor da diferença é obrigatório.")
    @Column(name = "difference_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal differenceAmount;

    @Column(name = "evidence_reference", length = 255)
    private String evidenceReference;
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
