package com.br.manager.domain.operacional.dto;

import com.br.manager.domain.operacional.enums.ProdutoTypeEnum;
import com.br.manager.domain.operacional.enums.TipoUnidadeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class ProdutoInputDTO {
    private UUID id;
    private String sku;
    private String nome;
    private ProdutoTypeEnum produtoType;
    private TipoUnidadeEnum unidade;
    private String anpCode;
    private String ncm;
    private String cest;
    private Boolean ativo = true;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    @JsonProperty("name")
    public String getNome() { return nome; }
    @JsonProperty("name")
    public void setNome(String nome) { this.nome = nome; }
    @JsonProperty("productType")
    public ProdutoTypeEnum getProdutoType() { return produtoType; }
    @JsonProperty("productType")
    public void setProdutoType(ProdutoTypeEnum produtoType) { this.produtoType = produtoType; }
    @JsonProperty("unit")
    public TipoUnidadeEnum getUnidade() { return unidade; }
    @JsonProperty("unit")
    public void setUnidade(TipoUnidadeEnum unidade) { this.unidade = unidade; }
    @JsonProperty("anpCode")
    public String getAnpCode() { return anpCode; }
    @JsonProperty("anpCode")
    public void setAnpCode(String anpCode) { this.anpCode = anpCode; }
    public String getNcm() { return ncm; }
    public void setNcm(String ncm) { this.ncm = ncm; }
    public String getCest() { return cest; }
    public void setCest(String cest) { this.cest = cest; }
    @JsonProperty("active")
    public Boolean getAtivo() { return ativo; }
    @JsonProperty("active")
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
