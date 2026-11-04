package com.br.manager.domain.turno_caixa.dto;

import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TurnoResponseDTO {

    private UUID id;
    private UUID estacaoId;
    private UUID caixeiroUsuarioId;
    private LocalDateTime openedAt;
    private LocalDateTime fechadoAt;
    private BigDecimal fundoAbertura;
    private TurnoSituacaoEnum status;
    private UUID openedBy;
    private UUID fechadoBy;
    private String observacoes;
    private List<TurnoAlocacaoResponseDTO> alocacoes;
    private List<BicoLeituraResponseDTO> leiturasBico;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getEstacaoId() {
        return estacaoId;
    }

    public void setEstacaoId(UUID estacaoId) {
        this.estacaoId = estacaoId;
    }

    public UUID getCaixeiroUsuarioId() {
        return caixeiroUsuarioId;
    }

    public void setCaixeiroUsuarioId(UUID caixeiroUsuarioId) {
        this.caixeiroUsuarioId = caixeiroUsuarioId;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }

    public LocalDateTime getFechadoAt() {
        return fechadoAt;
    }

    public void setFechadoAt(LocalDateTime fechadoAt) {
        this.fechadoAt = fechadoAt;
    }

    public BigDecimal getFundoAbertura() {
        return fundoAbertura;
    }

    public void setFundoAbertura(BigDecimal fundoAbertura) {
        this.fundoAbertura = fundoAbertura;
    }

    public TurnoSituacaoEnum getStatus() {
        return status;
    }

    public void setStatus(TurnoSituacaoEnum status) {
        this.status = status;
    }

    public UUID getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(UUID openedBy) {
        this.openedBy = openedBy;
    }

    public UUID getFechadoBy() {
        return fechadoBy;
    }

    public void setFechadoBy(UUID fechadoBy) {
        this.fechadoBy = fechadoBy;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public List<TurnoAlocacaoResponseDTO> getAlocacoes() {
        return alocacoes;
    }

    public void setAlocacoes(List<TurnoAlocacaoResponseDTO> alocacoes) {
        this.alocacoes = alocacoes;
    }

    public List<BicoLeituraResponseDTO> getLeiturasBico() {
        return leiturasBico;
    }

    public void setLeiturasBico(List<BicoLeituraResponseDTO> leiturasBico) {
        this.leiturasBico = leiturasBico;
    }
}
