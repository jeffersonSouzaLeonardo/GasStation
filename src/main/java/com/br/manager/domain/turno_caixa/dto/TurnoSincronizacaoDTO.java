package com.br.manager.domain.turno_caixa.dto;

import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Turno recebido do PDV no endpoint de sincronização (contrato fixo com o PDV).
 */
public class TurnoSincronizacaoDTO {

    @NotNull(message = "O ID do turno é obrigatório.")
    private UUID id;

    @NotNull(message = "O ID do posto é obrigatório.")
    private UUID estacaoId;

    private String nome;

    @NotNull(message = "A data de abertura é obrigatória.")
    private LocalDateTime openedAt;

    private LocalDateTime fechadoAt;

    @NotNull(message = "O status do turno é obrigatório.")
    private TurnoSituacaoEnum status;

    @NotNull(message = "O usuário que abriu é obrigatório.")
    private UUID openedBy;

    private UUID fechadoBy;

    private String observacoes;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @JsonProperty("stationId")
    public UUID getEstacaoId() {
        return estacaoId;
    }

    @JsonProperty("stationId")
    public void setEstacaoId(UUID estacaoId) {
        this.estacaoId = estacaoId;
    }

    @JsonProperty("name")
    public String getNome() {
        return nome;
    }

    @JsonProperty("name")
    public void setNome(String nome) {
        this.nome = nome;
    }

    @JsonProperty("openedAt")
    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    @JsonProperty("openedAt")
    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }

    @JsonProperty("closedAt")
    public LocalDateTime getFechadoAt() {
        return fechadoAt;
    }

    @JsonProperty("closedAt")
    public void setFechadoAt(LocalDateTime fechadoAt) {
        this.fechadoAt = fechadoAt;
    }

    @JsonProperty("status")
    public TurnoSituacaoEnum getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(TurnoSituacaoEnum status) {
        this.status = status;
    }

    @JsonProperty("openedBy")
    public UUID getOpenedBy() {
        return openedBy;
    }

    @JsonProperty("openedBy")
    public void setOpenedBy(UUID openedBy) {
        this.openedBy = openedBy;
    }

    @JsonProperty("closedBy")
    public UUID getFechadoBy() {
        return fechadoBy;
    }

    @JsonProperty("closedBy")
    public void setFechadoBy(UUID fechadoBy) {
        this.fechadoBy = fechadoBy;
    }

    @JsonProperty("notes")
    public String getObservacoes() {
        return observacoes;
    }

    @JsonProperty("notes")
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
