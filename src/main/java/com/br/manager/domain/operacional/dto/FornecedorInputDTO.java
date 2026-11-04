package com.br.manager.domain.operacional.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class FornecedorInputDTO {
    private UUID id;
    private String legalName;
    private String cnpjCpf;
    private String inscricaoEstadual;
    private String email;
    private String telefone;
    private String rua;
    private String numeroEndereco;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private Boolean ativo = true;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    @JsonProperty("legalName")
    public String getLegalName() { return legalName; }
    @JsonProperty("legalName")
    public void setLegalName(String legalName) { this.legalName = legalName; }
    public String getCnpjCpf() { return cnpjCpf; }
    public void setCnpjCpf(String cnpjCpf) { this.cnpjCpf = cnpjCpf; }
    @JsonProperty("stateRegistration")
    public String getInscricaoEstadual() { return inscricaoEstadual; }
    @JsonProperty("stateRegistration")
    public void setInscricaoEstadual(String inscricaoEstadual) { this.inscricaoEstadual = inscricaoEstadual; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    @JsonProperty("phone")
    public String getTelefone() { return telefone; }
    @JsonProperty("phone")
    public void setTelefone(String telefone) { this.telefone = telefone; }
    @JsonProperty("addressStreet")
    public String getRua() { return rua; }
    @JsonProperty("addressStreet")
    public void setRua(String rua) { this.rua = rua; }
    @JsonProperty("addressNumber")
    public String getNumeroEndereco() { return numeroEndereco; }
    @JsonProperty("addressNumber")
    public void setNumeroEndereco(String numeroEndereco) { this.numeroEndereco = numeroEndereco; }
    @JsonProperty("addressComplement")
    public String getComplemento() { return complemento; }
    @JsonProperty("addressComplement")
    public void setComplemento(String complemento) { this.complemento = complemento; }
    @JsonProperty("addressNeighborhood")
    public String getBairro() { return bairro; }
    @JsonProperty("addressNeighborhood")
    public void setBairro(String bairro) { this.bairro = bairro; }
    @JsonProperty("addressCity")
    public String getCidade() { return cidade; }
    @JsonProperty("addressCity")
    public void setCidade(String cidade) { this.cidade = cidade; }
    @JsonProperty("addressState")
    public String getEstado() { return estado; }
    @JsonProperty("addressState")
    public void setEstado(String estado) { this.estado = estado; }
    @JsonProperty("addressZipCode")
    public String getCep() { return cep; }
    @JsonProperty("addressZipCode")
    public void setCep(String cep) { this.cep = cep; }
    @JsonProperty("active")
    public Boolean getAtivo() { return ativo; }
    @JsonProperty("active")
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
