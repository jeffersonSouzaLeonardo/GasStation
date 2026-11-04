package com.br.manager.domain.turno_caixa.entity;

import com.br.manager.domain.turno_caixa.enums.TipoMovimentoCaixaEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "movimentos_caixa")
public class MovimentoCaixa {

    @Id
@Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "O turno é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turno_id", nullable = false)
    private Turno turno;

    @NotNull(message = "O tipo de movimentação é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimento", nullable = false, length = 25)
    private TipoMovimentoCaixaEnum tipoMovimento;

    @Column(name = "metodo_pagamento_id")
    private UUID pagamentoMethodId;

    @NotNull(message = "O valor é obrigatório.")
    @DecimalMin(value = "0.00", message = "O valor não pode ser negativo.")
    @Column(name = "valor", nullable = false, precision = 18, scale = 2)
    private BigDecimal valor;

    @NotNull(message = "A data da movimentação é obrigatória.")
    @Column(name = "ocorrido_em", nullable = false)
    private LocalDateTime ocorridoEm;

    @Column(name = "referencia", length = 100)
    private String referencia;

    @Column(name = "motivo", length = 255)
    private String motivo;

    @NotNull(message = "O usuário criador é obrigatório.")
    @Column(name = "criado_por", nullable = false)
    private UUID criadoPor;

    @Column(name = "aprovado_por")
    private UUID aprovadoPor;
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

    public TipoMovimentoCaixaEnum getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(TipoMovimentoCaixaEnum tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public UUID getPagamentoMethodId() {
        return pagamentoMethodId;
    }

    public void setPagamentoMethodId(UUID pagamentoMethodId) {
        this.pagamentoMethodId = pagamentoMethodId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDateTime getOcorridoEm() {
        return ocorridoEm;
    }

    public void setOcorridoEm(LocalDateTime ocorridoEm) {
        this.ocorridoEm = ocorridoEm;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public UUID getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(UUID criadoPor) {
        this.criadoPor = criadoPor;
    }

    public UUID getAprovadoPor() {
        return aprovadoPor;
    }

    public void setAprovadoPor(UUID aprovadoPor) {
        this.aprovadoPor = aprovadoPor;
    }
}
