package com.br.manager.domain.aquisicao_preco.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class ItemPedidoCompraResponseDTO {

    private UUID id;
    private UUID compraOrderId;
    private UUID produtoId;
    private Integer quantidade;
    private BigDecimal custoUnitario;
    private BigDecimal custoTotal;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCompraOrderId() {
        return compraOrderId;
    }

    public void setCompraOrderId(UUID compraOrderId) {
        this.compraOrderId = compraOrderId;
    }

    public UUID getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(UUID produtoId) {
        this.produtoId = produtoId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getCustoUnitario() {
        return custoUnitario;
    }

    public void setCustoUnitario(BigDecimal custoUnitario) {
        this.custoUnitario = custoUnitario;
    }

    public BigDecimal getCustoTotal() {
        return custoTotal;
    }

    public void setCustoTotal(BigDecimal custoTotal) {
        this.custoTotal = custoTotal;
    }
}
