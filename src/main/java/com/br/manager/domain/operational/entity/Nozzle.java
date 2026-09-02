package com.br.manager.domain.operational.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "nozzles")
public class Nozzle {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "Pump ID is required.")
    @Column(name = "pump_id", nullable = false)
    private UUID pumpId;

    @NotNull(message = "Tank ID is required.")
    @Column(name = "tank_id", nullable = false)
    private UUID tankId;

    @NotBlank(message = "Nozzle code is required.")
    @Size(min = 1, max = 50, message = "Nozzle code must have between 1 and 50 characters.")
    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @NotNull(message = "Product ID is required.")
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "meter_number", precision = 15, scale = 4)
    private BigDecimal meterNumber = BigDecimal.ZERO;

    @NotNull(message = "Active flag is required.")
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @PrePersist
    public void ensureId() { if (this.id == null) { this.id = UUID.randomUUID(); } }

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
