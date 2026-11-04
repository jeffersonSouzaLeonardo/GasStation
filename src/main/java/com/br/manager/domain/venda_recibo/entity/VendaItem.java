package com.br.manager.domain.venda_recibo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "item_venda")
public class VendaItem {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;

    @NotNull(message = "Linha numero é obrigatório.")
    @Column(name = "numero_linha", nullable = false)
    private Integer linhaNumero;

    @NotNull(message = "O ID de Produto é obrigatório.")
    @Column(name = "produto_id", nullable = false)
    private UUID produtoId;

    @Column(name = "bico_id")
    private UUID bicoId;

    @Column(name = "tanque_id")
    private UUID tanqueId;

    @NotNull(message = "Quantity é obrigatório.")
    @Column(name = "quantidade", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantidade;

    @NotNull(message = "Unidade preco é obrigatório.")
    @Column(name = "preco_unitario", nullable = false, precision = 19, scale = 4)
    private BigDecimal precoUnitario;

    @NotNull(message = "Desconto valor é obrigatório.")
    @Column(name = "valor_desconto", nullable = false, precision = 19, scale = 4)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Column(name = "medidor_inicial", precision = 19, scale = 4)
    private BigDecimal medidorInicial;

    @Column(name = "medidor_final", precision = 19, scale = 4)
    private BigDecimal medidorFinal;
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

    public Integer getLinhaNumero() {
        return linhaNumero;
    }

    public void setLinhaNumero(Integer linhaNumero) {
        this.linhaNumero = linhaNumero;
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

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public BigDecimal getMedidorInicial() {
        return medidorInicial;
    }

    public void setMedidorInicial(BigDecimal medidorInicial) {
        this.medidorInicial = medidorInicial;
    }

    public BigDecimal getMedidorFinal() {
        return medidorFinal;
    }

    public void setMedidorFinal(BigDecimal medidorFinal) {
        this.medidorFinal = medidorFinal;
    }
}
