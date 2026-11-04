package com.br.manager.domain.aquisicao_preco.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "itens_preco")
public class ItemPreco {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Preco tabela é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tabela_preco_id", nullable = false)
    private TabelaPreco tabelaPreco;

    @NotNull(message = "O ID de Produto é obrigatório.")
    @Column(name = "produto_id", nullable = false)
    private UUID produtoId;

    @Column(name = "bico_id")
    private UUID bicoId;

    @NotNull(message = "Unidade preco é obrigatório.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unidade preco deve be greater than zero.")
    @Column(name = "preco_unitario", precision = 10, scale = 4, nullable = false)
    private BigDecimal precoUnitario;

    @DecimalMin(value = "0.0", message = "Min preco cannot be negative.")
    @Column(name = "preco_minimo", precision = 10, scale = 4)
    private BigDecimal precoMinimo;

    @DecimalMin(value = "0.0", message = "Max desconto percentual cannot be negative.")
    @DecimalMax(value = "100.0", message = "Max desconto percentual cannot exceed 100%.")
    @Column(name = "desconto_maximo_percentual", precision = 5, scale = 2)
    private BigDecimal descontoMaximoPercentual;

    @NotNull(message = "Ativo indicador é obrigatório.")
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    public ItemPreco() {
    }

    // Getters e Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TabelaPreco getTabelaPreco() {
        return tabelaPreco;
    }

    public void setTabelaPreco(TabelaPreco tabelaPreco) {
        this.tabelaPreco = tabelaPreco;
    }

    public UUID getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(UUID produtoId) {
        this.produtoId = produtoId;
    }

    public UUID getBicoId() {
        return bicoId;
    }

    public void setBicoId(UUID bicoId) {
        this.bicoId = bicoId;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getPrecoMinimo() {
        return precoMinimo;
    }

    public void setPrecoMinimo(BigDecimal precoMinimo) {
        this.precoMinimo = precoMinimo;
    }

    public BigDecimal getDescontoMaximoPercentual() {
        return descontoMaximoPercentual;
    }

    public void setDescontoMaximoPercentual(BigDecimal descontoMaximoPercentual) {
        this.descontoMaximoPercentual = descontoMaximoPercentual;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}