package com.br.manager.domain.operacional.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class BombaInputDTO {
    private UUID id;
    private String codigo;
    private String localizacao;
    private Boolean ativo = true;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    @JsonProperty("code")
    public String getCodigo() { return codigo; }
    @JsonProperty("code")
    public void setCodigo(String codigo) { this.codigo = codigo; }
    @JsonProperty("location")
    public String getLocalizacao() { return localizacao; }
    @JsonProperty("location")
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
    @JsonProperty("active")
    public Boolean getAtivo() { return ativo; }
    @JsonProperty("active")
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
