package com.br.manager.domain.operational.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class NozzleResponseDTO {
    private UUID id;
    private UUID pumpId;
    private UUID tankId;
    private String code;
    private UUID productId;
    private BigDecimal meterNumber;
    private Boolean active;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPumpId() { return pumpId; }
    public void setPumpId(UUID pumpId) { this.pumpId = pumpId; }
    public UUID getTankId() { return tankId; }
    public void setTankId(UUID tankId) { this.tankId = tankId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public BigDecimal getMeterNumber() { return meterNumber; }
    public void setMeterNumber(BigDecimal meterNumber) { this.meterNumber = meterNumber; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
