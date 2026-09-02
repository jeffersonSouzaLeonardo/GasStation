package com.br.manager.domain.operational.dto;

import com.br.manager.domain.operational.enums.PaymentMethodKindEnum;

import java.util.UUID;

public class PaymentMethodInputDTO {
    private UUID id;
    private UUID companyId;
    private String name;
    private PaymentMethodKindEnum kind;
    private Boolean requiresReference = false;
    private Integer settlementDays = 0;
    private Boolean active = true;

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
