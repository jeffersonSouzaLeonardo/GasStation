package com.br.manager.domain.operacional.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Abastecimento recebido do PDV no endpoint de sincronização.
 */
public class AbastecimentoRequestDTO {

    @NotNull(message = "O ID do abastecimento é obrigatório.")
    private UUID id;

    @NotNull(message = "A data/hora do abastecimento é obrigatória.")
    private LocalDateTime dataHora;

    @NotNull(message = "O ID do tanque é obrigatório.")
    private UUID tanqueId;

    @NotNull(message = "O ID da bomba é obrigatório.")
    private UUID bombaId;

    @NotNull(message = "O volume é obrigatório.")
    private BigDecimal volume;

    @NotNull(message = "O valor total é obrigatório.")
    private BigDecimal valorTotal;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    @JsonProperty("dataTime")
    public LocalDateTime getDataHora() { return dataHora; }
    @JsonProperty("dataTime")
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    @JsonProperty("tankId")
    public UUID getTanqueId() { return tanqueId; }
    @JsonProperty("tankId")
    public void setTanqueId(UUID tanqueId) { this.tanqueId = tanqueId; }
    @JsonProperty("pumpId")
    public UUID getBombaId() { return bombaId; }
    @JsonProperty("pumpId")
    public void setBombaId(UUID bombaId) { this.bombaId = bombaId; }
    public BigDecimal getVolume() { return volume; }
    public void setVolume(BigDecimal volume) { this.volume = volume; }
    @JsonProperty("totalValue")
    public BigDecimal getValorTotal() { return valorTotal; }
    @JsonProperty("totalValue")
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
}
