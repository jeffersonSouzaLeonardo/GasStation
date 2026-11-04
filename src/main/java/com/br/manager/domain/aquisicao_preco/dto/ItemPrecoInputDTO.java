package com.br.manager.domain.aquisicao_preco.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class ItemPrecoInputDTO {
    private UUID id;
    private UUID precoTabelaId;
    private UUID produtoId;
    private UUID bicoId;
    private BigDecimal precoUnitario;
    private BigDecimal precoMinimo;
    private BigDecimal descontoMaximoPercentual;
    private Boolean ativo = true;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getPrecoTabelaId() { return precoTabelaId; }
    public void setPrecoTabelaId(UUID precoTabelaId) { this.precoTabelaId = precoTabelaId; }

    public UUID getProdutoId() { return produtoId; }
    public void setProdutoId(UUID produtoId) { this.produtoId = produtoId; }

    public UUID getBicoId() { return bicoId; }
    public void setBicoId(UUID bicoId) { this.bicoId = bicoId; }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }

    public BigDecimal getPrecoMinimo() { return precoMinimo; }
    public void setPrecoMinimo(BigDecimal precoMinimo) { this.precoMinimo = precoMinimo; }

    public BigDecimal getDescontoMaximoPercentual() { return descontoMaximoPercentual; }
    public void setDescontoMaximoPercentual(BigDecimal descontoMaximoPercentual) { this.descontoMaximoPercentual = descontoMaximoPercentual; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
