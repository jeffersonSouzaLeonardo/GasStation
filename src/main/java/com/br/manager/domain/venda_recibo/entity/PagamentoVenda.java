package com.br.manager.domain.venda_recibo.entity;

import com.br.manager.domain.venda_recibo.enums.SituacaoPagamento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pagamento_venda")
public class PagamentoVenda {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;

    @NotNull(message = "O ID de Pagamento method é obrigatório.")
    @Column(name = "metodo_pagamento_id", nullable = false)
    private UUID pagamentoMethodId;

    @NotNull(message = "Valor é obrigatório.")
    @Column(name = "valor", nullable = false, precision = 19, scale = 4)
    private BigDecimal valor;

    @NotNull(message = "Situacao é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SituacaoPagamento status;

    @Column(name = "referencia_transacao", length = 120)
    private String referenciaTransacao;

    @Column(name = "autorizado_em")
    private Instant autorizadoEm;

    @Column(name = "recebido_em")
    private Instant recebidoEm;
public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
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

    public SituacaoPagamento getStatus() {
        return status;
    }

    public void setStatus(SituacaoPagamento status) {
        this.status = status;
    }

    public String getReferenciaTransacao() {
        return referenciaTransacao;
    }

    public void setReferenciaTransacao(String referenciaTransacao) {
        this.referenciaTransacao = referenciaTransacao;
    }

    public Instant getAutorizadoEm() {
        return autorizadoEm;
    }

    public void setAutorizadoEm(Instant autorizadoEm) {
        this.autorizadoEm = autorizadoEm;
    }

    public Instant getRecebidoEm() {
        return recebidoEm;
    }

    public void setRecebidoEm(Instant recebidoEm) {
        this.recebidoEm = recebidoEm;
    }
}
