package com.br.manager.domain.turno_caixa.dto;

import com.br.manager.domain.turno_caixa.enums.CargoCaixaEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Payload de abertura de uma sessão de caixa.
 */
public class SessaoCaixaInputDTO {

    @NotNull(message = "O ID do turno é obrigatório.")
    private UUID turnoId;

    @NotNull(message = "O ID do usuário caixa é obrigatório.")
    private UUID caixeiroUsuarioId;

    @NotNull(message = "O papel do operador é obrigatório.")
    private CargoCaixaEnum cargo;

    @NotNull(message = "O fundo de troco é obrigatório.")
    @DecimalMin(value = "0.00", message = "O fundo de troco não pode ser negativo.")
    private BigDecimal fundoAbertura;

    @Size(max = 500, message = "As observações não podem exceder 500 caracteres.")
    private String observacoes;

    public UUID getTurnoId() {
        return turnoId;
    }

    public void setTurnoId(UUID turnoId) {
        this.turnoId = turnoId;
    }

    public UUID getCaixeiroUsuarioId() {
        return caixeiroUsuarioId;
    }

    public void setCaixeiroUsuarioId(UUID caixeiroUsuarioId) {
        this.caixeiroUsuarioId = caixeiroUsuarioId;
    }

    public CargoCaixaEnum getCargo() {
        return cargo;
    }

    public void setCargo(CargoCaixaEnum cargo) {
        this.cargo = cargo;
    }

    public BigDecimal getFundoAbertura() {
        return fundoAbertura;
    }

    public void setFundoAbertura(BigDecimal fundoAbertura) {
        this.fundoAbertura = fundoAbertura;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
