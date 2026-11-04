package com.br.manager.domain.operacional.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public class BicoInputDTO {
    private UUID id;
    private UUID bombaId;
    private UUID tanqueId;
    private String codigo;
    private UUID produtoId;
    private BigDecimal numeroMedidor = BigDecimal.ZERO;
    private Boolean ativo = true;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    @JsonProperty("pumpId")
    public UUID getBombaId() { return bombaId; }
    @JsonProperty("pumpId")
    public void setBombaId(UUID bombaId) { this.bombaId = bombaId; }
    @JsonProperty("tankId")
    public UUID getTanqueId() { return tanqueId; }
    @JsonProperty("tankId")
    public void setTanqueId(UUID tanqueId) { this.tanqueId = tanqueId; }
    @JsonProperty("code")
    public String getCodigo() { return codigo; }
    @JsonProperty("code")
    public void setCodigo(String codigo) { this.codigo = codigo; }
    @JsonProperty("productId")
    public UUID getProdutoId() { return produtoId; }
    @JsonProperty("productId")
    public void setProdutoId(UUID produtoId) { this.produtoId = produtoId; }
    @JsonProperty("meterNumber")
    public BigDecimal getNumeroMedidor() { return numeroMedidor; }
    @JsonProperty("meterNumber")
    public void setNumeroMedidor(BigDecimal numeroMedidor) { this.numeroMedidor = numeroMedidor; }
    @JsonProperty("active")
    public Boolean getAtivo() { return ativo; }
    @JsonProperty("active")
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
