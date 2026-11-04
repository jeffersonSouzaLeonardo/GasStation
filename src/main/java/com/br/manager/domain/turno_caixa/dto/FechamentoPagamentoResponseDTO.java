package com.br.manager.domain.turno_caixa.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class FechamentoPagamentoResponseDTO {

    private UUID id;
    private UUID turnoId;
    private UUID pagamentoMethodId;
    private BigDecimal valorEsperado;
    private BigDecimal valorDeclarado;
    private BigDecimal valorDiferenca;
    private String referenciaComprovante;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTurnoId() {
        return turnoId;
    }

    public void setTurnoId(UUID turnoId) {
        this.turnoId = turnoId;
    }

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

    public BigDecimal getValorDiferenca() {
        return valorDiferenca;
    }

    public void setValorDiferenca(BigDecimal valorDiferenca) {
        this.valorDiferenca = valorDiferenca;
    }

    public String getReferenciaComprovante() {
        return referenciaComprovante;
    }

    public void setReferenciaComprovante(String referenciaComprovante) {
        this.referenciaComprovante = referenciaComprovante;
    }
}
