package com.br.manager.domain.venda_recibo.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class VendaItemInputDTO {
    private UUID id;
    private UUID vendaId;
    private Integer linhaNumero;
    private UUID produtoId;
    private UUID bicoId;
    private UUID tanqueId;
    private BigDecimal quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal valorDesconto;
    private BigDecimal medidorInicial;
    private BigDecimal medidorFinal;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getVendaId() { return vendaId; }
    public void setVendaId(UUID vendaId) { this.vendaId = vendaId; }
    public Integer getLinhaNumero() { return linhaNumero; }
    public void setLinhaNumero(Integer linhaNumero) { this.linhaNumero = linhaNumero; }
    public UUID getProdutoId() { return produtoId; }
    public void setProdutoId(UUID produtoId) { this.produtoId = produtoId; }
    public UUID getBicoId() { return bicoId; }
    public void setBicoId(UUID bicoId) { this.bicoId = bicoId; }
    public UUID getTanqueId() { return tanqueId; }
    public void setTanqueId(UUID tanqueId) { this.tanqueId = tanqueId; }
    public BigDecimal getQuantidade() { return quantidade; }
    public void setQuantidade(BigDecimal quantidade) { this.quantidade = quantidade; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }
    public BigDecimal getValorDesconto() { return valorDesconto; }
    public void setValorDesconto(BigDecimal valorDesconto) { this.valorDesconto = valorDesconto; }
    public BigDecimal getMedidorInicial() { return medidorInicial; }
    public void setMedidorInicial(BigDecimal medidorInicial) { this.medidorInicial = medidorInicial; }
    public BigDecimal getMedidorFinal() { return medidorFinal; }
    public void setMedidorFinal(BigDecimal medidorFinal) { this.medidorFinal = medidorFinal; }
}
