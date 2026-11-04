package com.br.manager.domain.turno_caixa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "encerramentos_pagamento")
public class FechamentoPagamento {

    @Id
@Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "O turno é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turno_id", nullable = false)
    private Turno turno;

    @NotNull(message = "O ID da forma de pagamento é obrigatório.")
    @Column(name = "metodo_pagamento_id", nullable = false)
    private UUID pagamentoMethodId;

    @NotNull(message = "O valor esperado é obrigatório.")
    @DecimalMin(value = "0.00", message = "O valor esperado não pode ser negativo.")
    @Column(name = "valor_esperado", nullable = false, precision = 18, scale = 2)
    private BigDecimal valorEsperado;

    @NotNull(message = "O valor declarado é obrigatório.")
    @DecimalMin(value = "0.00", message = "O valor declarado não pode ser negativo.")
    @Column(name = "valor_declarado", nullable = false, precision = 18, scale = 2)
    private BigDecimal valorDeclarado;

    @NotNull(message = "O valor da diferença é obrigatório.")
    @Column(name = "valor_diferenca", nullable = false, precision = 18, scale = 2)
    private BigDecimal valorDiferenca;

    @Column(name = "referencia_comprovante", length = 255)
    private String referenciaComprovante;
public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
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
