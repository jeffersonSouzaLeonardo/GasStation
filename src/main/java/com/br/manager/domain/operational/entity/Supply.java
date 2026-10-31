package com.br.manager.domain.operational.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Abastecimento registrado no PDV e enviado para a retaguarda.
 * O identificador é gerado pelo PDV e replicado aqui (sem geração automática).
 */
@Entity
@Table(name = "supplies")
public class Supply {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "A data/hora do abastecimento é obrigatória.")
    @Column(name = "data_time", nullable = false)
    private LocalDateTime dataTime;

    @NotNull(message = "O ID do tanque é obrigatório.")
    @Column(name = "tank_id", nullable = false)
    private UUID tankId;

    @NotNull(message = "O ID da bomba é obrigatório.")
    @Column(name = "pump_id", nullable = false)
    private UUID pumpId;

    @NotNull(message = "O volume é obrigatório.")
    @Column(name = "volume", nullable = false, precision = 15, scale = 4)
    private BigDecimal volume;

    @NotNull(message = "O valor total é obrigatório.")
    @Column(name = "total_value", nullable = false, precision = 15, scale = 4)
    private BigDecimal totalValue;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public LocalDateTime getDataTime() { return dataTime; }
    public void setDataTime(LocalDateTime dataTime) { this.dataTime = dataTime; }
    public UUID getTankId() { return tankId; }
    public void setTankId(UUID tankId) { this.tankId = tankId; }
    public UUID getPumpId() { return pumpId; }
    public void setPumpId(UUID pumpId) { this.pumpId = pumpId; }
    public BigDecimal getVolume() { return volume; }
    public void setVolume(BigDecimal volume) { this.volume = volume; }
    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
