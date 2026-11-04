package com.br.manager.domain.operacional.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public class TanqueResponseDTO {
    private UUID id;
    private String codigo;
    private UUID produtoId;
    private BigDecimal capacidadeLitros;
    private BigDecimal estoqueMortoLitros;
    private BigDecimal saldoContabilLitros;
    private Boolean ativo;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    @JsonProperty("code")
    public String getCodigo() { return codigo; }
    @JsonProperty("code")
    public void setCodigo(String codigo) { this.codigo = codigo; }
    @JsonProperty("productId")
    public UUID getProdutoId() { return produtoId; }
    @JsonProperty("productId")
    public void setProdutoId(UUID produtoId) { this.produtoId = produtoId; }
    @JsonProperty("capacityLiters")
    public BigDecimal getCapacidadeLitros() { return capacidadeLitros; }
    @JsonProperty("capacityLiters")
    public void setCapacidadeLitros(BigDecimal capacidadeLitros) { this.capacidadeLitros = capacidadeLitros; }
    @JsonProperty("deadStockLiters")
    public BigDecimal getEstoqueMortoLitros() { return estoqueMortoLitros; }
    @JsonProperty("deadStockLiters")
    public void setEstoqueMortoLitros(BigDecimal estoqueMortoLitros) { this.estoqueMortoLitros = estoqueMortoLitros; }
    @JsonProperty("currentBookLiters")
    public BigDecimal getSaldoContabilLitros() { return saldoContabilLitros; }
    @JsonProperty("currentBookLiters")
    public void setSaldoContabilLitros(BigDecimal saldoContabilLitros) { this.saldoContabilLitros = saldoContabilLitros; }
    @JsonProperty("active")
    public Boolean getAtivo() { return ativo; }
    @JsonProperty("active")
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
