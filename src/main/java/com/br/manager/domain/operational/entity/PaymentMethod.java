package com.br.manager.domain.operational.entity;

import com.br.manager.domain.operational.enums.PaymentMethodKindEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "payment_methods")
public class PaymentMethod {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "Company ID is required.")
    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @NotBlank(message = "Payment method name is required.")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull(message = "Payment method kind is required.")
    @Enumerated(EnumType.STRING)
    @Column(name = "kind", nullable = false, length = 50)
    private PaymentMethodKindEnum kind;

    @NotNull(message = "Reference requirement flag is required.")
    @Column(name = "requires_reference", nullable = false)
    private Boolean requiresReference = false;

    @Column(name = "settlement_days")
    private Integer settlementDays = 0;

    @NotNull(message = "Active flag is required.")
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @PrePersist
    public void ensureId() { if (this.id == null) { this.id = UUID.randomUUID(); } }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public PaymentMethodKindEnum getKind() { return kind; }
    public void setKind(PaymentMethodKindEnum kind) { this.kind = kind; }
    public Boolean getRequiresReference() { return requiresReference; }
    public void setRequiresReference(Boolean requiresReference) { this.requiresReference = requiresReference; }
    public Integer getSettlementDays() { return settlementDays; }
    public void setSettlementDays(Integer settlementDays) { this.settlementDays = settlementDays; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
