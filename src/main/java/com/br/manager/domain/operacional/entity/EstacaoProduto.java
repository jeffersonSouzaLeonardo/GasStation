package com.br.manager.domain.operacional.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "estacao_produtos")
public class EstacaoProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "O ID do produto é obrigatório.")
    @Column(name = "produto_id", nullable = false)
    private UUID produtoId;

    @Column(name = "estoque_minimo", precision = 15, scale = 4)
    private BigDecimal estoqueMinimo = BigDecimal.ZERO;

    @Column(name = "ponto_reposicao", precision = 15, scale = 4)
    private BigDecimal pontoReposicao = BigDecimal.ZERO;

    @NotNull(message = "O campo ativo é obrigatório.")
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;
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
