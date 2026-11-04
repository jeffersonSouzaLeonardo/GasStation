package com.br.manager.domain.aquisicao_preco.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "itens_recebimento_mercadorias")
public class RecebimentoMercadoriaItem {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Mercadoria recibo é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recebimento_mercadoria_id", nullable = false)
    private RecebimentoMercadoria recebimentoMercadoria;

    @NotNull(message = "Produto é obrigatório.")
    @Column(name = "produto_id", nullable = false)
    private UUID produtoId;

    @Column(name = "tanque_id")
    private UUID tanqueId;

    @NotNull(message = "Quantity é obrigatório.")
    @DecimalMin(value = "0.00", inclusive = false, message = "Quantity deve be greater than zero.")
    @Column(name = "quantidade", precision = 19, scale = 4, nullable = false)
    private BigDecimal quantidade;

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

    public RecebimentoMercadoria getRecebimentoMercadoria() {
        return recebimentoMercadoria;
    }

    public void setRecebimentoMercadoria(RecebimentoMercadoria recebimentoMercadoria) {
        this.recebimentoMercadoria = recebimentoMercadoria;
    }

    public UUID getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(UUID produtoId) {
        this.produtoId = produtoId;
    }

    public UUID getTanqueId() {
        return tanqueId;
    }

    public void setTanqueId(UUID tanqueId) {
        this.tanqueId = tanqueId;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getCustoUnitario() {
        return custoUnitario;
    }

    public void setCustoUnitario(BigDecimal custoUnitario) {
        this.custoUnitario = custoUnitario;
    }
}
