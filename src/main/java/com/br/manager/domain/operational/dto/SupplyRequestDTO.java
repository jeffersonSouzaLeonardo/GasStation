package com.br.manager.domain.operational.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Abastecimento recebido do PDV no endpoint de sincronização.
 */
public class SupplyRequestDTO {

    @NotNull(message = "O ID do abastecimento é obrigatório.")
    private UUID id;

    @NotNull(message = "A data/hora do abastecimento é obrigatória.")
    private LocalDateTime dataTime;

    @NotNull(message = "O ID do tanque é obrigatório.")
    private UUID tankId;

    @NotNull(message = "O ID da bomba é obrigatório.")
    private UUID pumpId;

    @NotNull(message = "O volume é obrigatório.")
    private BigDecimal volume;

    @NotNull(message = "O valor total é obrigatório.")
    private BigDecimal totalValue;

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
}
