package com.br.manager.domain.turno_caixa.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Payload de fechamento de uma sessão de caixa, com os totais consolidados.
 */
public class SessaoCaixaFechamentoInputDTO {

    private UUID id;

    @DecimalMin(value = "0.00", message = "O total em dinheiro não pode ser negativo.")
    private BigDecimal totalDinheiro;

    @DecimalMin(value = "0.00", message = "O total em cartão não pode ser negativo.")
    private BigDecimal totalCartao;

    @DecimalMin(value = "0.00", message = "O total em Pix não pode ser negativo.")
    private BigDecimal totalPix;

    @DecimalMin(value = "0.00", message = "O total de sangrias não pode ser negativo.")
    private BigDecimal totalSangria;

    @Size(max = 500, message = "As observações não podem exceder 500 caracteres.")
    private String observacoes;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getTotalDinheiro() {
        return totalDinheiro;
    }

    public void setTotalDinheiro(BigDecimal totalDinheiro) {
        this.totalDinheiro = totalDinheiro;
    }

    public BigDecimal getTotalCartao() {
        return totalCartao;
    }

    public void setTotalCartao(BigDecimal totalCartao) {
        this.totalCartao = totalCartao;
    }

    public BigDecimal getTotalPix() {
        return totalPix;
    }

    public void setTotalPix(BigDecimal totalPix) {
        this.totalPix = totalPix;
    }

    public BigDecimal getTotalSangria() {
        return totalSangria;
    }

    public void setTotalSangria(BigDecimal totalSangria) {
        this.totalSangria = totalSangria;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
