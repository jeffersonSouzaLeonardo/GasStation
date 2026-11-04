package com.br.manager.domain.turno_caixa.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class TurnoAberturaRequestDTO {

    @NotNull(message = "O ID de Estacao é obrigatório.")
    private UUID estacaoId;

    @NotNull(message = "O ID de Caixeiro usuario é obrigatório.")
    private UUID caixeiroUsuarioId;

    @NotNull(message = "Abertura dinheiro é obrigatório.")
    @DecimalMin(value = "0.00", message = "Abertura dinheiro cannot be negative.")
    private BigDecimal fundoAbertura;

    @NotNull(message = "Opened by é obrigatório.")
    private UUID openedBy;

    @Size(max = 500, message = "Observacoes cannot exceed 500 caracteres.")
    private String observacoes;

    private List<TurnoAlocacaoInputDTO> alocacoes;

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

    public BigDecimal getFundoAbertura() {
        return fundoAbertura;
    }

    public void setFundoAbertura(BigDecimal fundoAbertura) {
        this.fundoAbertura = fundoAbertura;
    }

    public UUID getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(UUID openedBy) {
        this.openedBy = openedBy;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public List<TurnoAlocacaoInputDTO> getAlocacoes() {
        return alocacoes;
    }

    public void setAlocacoes(List<TurnoAlocacaoInputDTO> alocacoes) {
        this.alocacoes = alocacoes;
    }
}
