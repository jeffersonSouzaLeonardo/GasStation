package com.br.manager.domain.operacional.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class EstacaoProdutoResponseDTO {
    private UUID id;
    private UUID produtoId;
    private BigDecimal estoqueMinimo;
    private BigDecimal pontoReposicao;
    private Boolean ativo;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getProdutoId() { return produtoId; }
    public void setProdutoId(UUID produtoId) { this.produtoId = produtoId; }
    public BigDecimal getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(BigDecimal estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }
    public BigDecimal getPontoReposicao() { return pontoReposicao; }
    public void setPontoReposicao(BigDecimal pontoReposicao) { this.pontoReposicao = pontoReposicao; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
