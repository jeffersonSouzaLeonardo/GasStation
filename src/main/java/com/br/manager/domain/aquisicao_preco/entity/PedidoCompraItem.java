package com.br.manager.domain.aquisicao_preco.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "itens_pedido_compra")
public class PedidoCompraItem {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Compra order é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_compra_id", nullable = false)
    private PedidoCompra pedidoCompra;

    @NotNull(message = "Produto é obrigatório.")
    @Column(name = "produto_id", nullable = false)
    private UUID produtoId;

    @NotNull(message = "Quantity é obrigatório.")
    @Min(value = 1, message = "Quantity deve be greater than zero.")
    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @NotNull(message = "Unidade custo é obrigatório.")
    @DecimalMin(value = "0.00", inclusive = false, message = "Unidade custo deve be greater than zero.")
    @Column(name = "custo_unitario", precision = 19, scale = 4, nullable = false)
    private BigDecimal custoUnitario;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public PedidoCompra getPedidoCompra() {
        return pedidoCompra;
    }

    public void setPedidoCompra(PedidoCompra pedidoCompra) {
        this.pedidoCompra = pedidoCompra;
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
        if (custoUnitario == null || quantidade == null) {
            return BigDecimal.ZERO;
        }
        return custoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}
