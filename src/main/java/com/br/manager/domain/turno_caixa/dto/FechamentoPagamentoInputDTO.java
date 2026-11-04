package com.br.manager.domain.turno_caixa.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class FechamentoPagamentoInputDTO {

    @NotNull(message = "O ID de Pagamento method é obrigatório.")
    private UUID pagamentoMethodId;

    @NotNull(message = "Esperado valor é obrigatório.")
    @DecimalMin(value = "0.00", message = "Esperado valor cannot be negative.")
    private BigDecimal valorEsperado;

    @NotNull(message = "Declarado valor é obrigatório.")
    @DecimalMin(value = "0.00", message = "Declarado valor cannot be negative.")
    private BigDecimal valorDeclarado;

    private String referenciaComprovante;

    public UUID getPagamentoMethodId() {
        return pagamentoMethodId;
    }

    public void setPagamentoMethodId(UUID pagamentoMethodId) {
        this.pagamentoMethodId = pagamentoMethodId;
    }

    public BigDecimal getValorEsperado() {
        return valorEsperado;
    }

    public void setValorEsperado(BigDecimal valorEsperado) {
        this.valorEsperado = valorEsperado;
    }

    public BigDecimal getValorDeclarado() {
        return valorDeclarado;
    }

    public void setValorDeclarado(BigDecimal valorDeclarado) {
        this.valorDeclarado = valorDeclarado;
    }

    public String getReferenciaComprovante() {
        return referenciaComprovante;
    }

    public void setReferenciaComprovante(String referenciaComprovante) {
        this.referenciaComprovante = referenciaComprovante;
    }
}
