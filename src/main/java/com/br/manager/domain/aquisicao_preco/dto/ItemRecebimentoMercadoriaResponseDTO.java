package com.br.manager.domain.aquisicao_preco.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class ItemRecebimentoMercadoriaResponseDTO {
    private UUID id;
    private UUID mercadoriaReciboId;
    private UUID produtoId;
    private UUID tanqueId;
    private BigDecimal quantidade;
    private BigDecimal custoUnitario;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getMercadoriaReciboId() { return mercadoriaReciboId; }
    public void setMercadoriaReciboId(UUID mercadoriaReciboId) { this.mercadoriaReciboId = mercadoriaReciboId; }

    public UUID getProdutoId() { return produtoId; }
    public void setProdutoId(UUID produtoId) { this.produtoId = produtoId; }

    public UUID getTanqueId() { return tanqueId; }
    public void setTanqueId(UUID tanqueId) { this.tanqueId = tanqueId; }

    public BigDecimal getQuantidade() { return quantidade; }
    public void setQuantidade(BigDecimal quantidade) { this.quantidade = quantidade; }

    public BigDecimal getCustoUnitario() { return custoUnitario; }
    public void setCustoUnitario(BigDecimal custoUnitario) { this.custoUnitario = custoUnitario; }

}
