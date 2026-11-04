package com.br.manager.domain.venda_recibo.dto;

import com.br.manager.domain.venda_recibo.enums.SituacaoPagamento;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class VendaPagamentoInputDTO {
    private UUID id;
    private UUID vendaId;
    private UUID pagamentoMethodId;
    private BigDecimal valor;
    private SituacaoPagamento status;
    private String referenciaTransacao;
    private Instant autorizadoEm;
    private Instant recebidoEm;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getVendaId() { return vendaId; }
    public void setVendaId(UUID vendaId) { this.vendaId = vendaId; }
    public UUID getPagamentoMethodId() { return pagamentoMethodId; }
    public void setPagamentoMethodId(UUID pagamentoMethodId) { this.pagamentoMethodId = pagamentoMethodId; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public SituacaoPagamento getStatus() { return status; }
    public void setStatus(SituacaoPagamento status) { this.status = status; }
    public String getReferenciaTransacao() { return referenciaTransacao; }
    public void setReferenciaTransacao(String referenciaTransacao) { this.referenciaTransacao = referenciaTransacao; }
    public Instant getAutorizadoEm() { return autorizadoEm; }
    public void setAutorizadoEm(Instant autorizadoEm) { this.autorizadoEm = autorizadoEm; }
    public Instant getRecebidoEm() { return recebidoEm; }
    public void setRecebidoEm(Instant recebidoEm) { this.recebidoEm = recebidoEm; }
}
