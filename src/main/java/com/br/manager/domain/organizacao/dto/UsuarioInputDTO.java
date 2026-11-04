package com.br.manager.domain.organizacao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public class UsuarioInputDTO {
    private UUID id;
    private String nome;
    private String email;
    private String login;
    private String role;
    private String passwordHash;
    private String telefone;
    private Boolean ativo = true;
    private LocalDateTime ultimaLoginAt;
    private LocalDateTime dataExclusao;

    // Transversal timestamps
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private LocalDateTime excluidoEm;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getNome() {
        return nome;
    }

    @JsonProperty("name")
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @JsonProperty("passwordHash")
    public String getPasswordHash() {
        return passwordHash;
    }

    @JsonProperty("passwordHash")
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @JsonProperty("phone")
    public String getTelefone() {
        return telefone;
    }

    @JsonProperty("phone")
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @JsonProperty("active")
    public Boolean getAtivo() {
        return ativo;
    }

    @JsonProperty("active")
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @JsonProperty("lastLoginAt")
    public LocalDateTime getUltimaLoginAt() {
        return ultimaLoginAt;
    }

    @JsonProperty("lastLoginAt")
    public void setUltimaLoginAt(LocalDateTime ultimaLoginAt) {
        this.ultimaLoginAt = ultimaLoginAt;
    }

    public LocalDateTime getDataExclusao() {
        return dataExclusao;
    }

    public void setDataExclusao(LocalDateTime dataExclusao) {
        this.dataExclusao = dataExclusao;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public LocalDateTime getExcluidoEm() {
        return excluidoEm;
    }

    public void setExcluidoEm(LocalDateTime excluidoEm) {
        this.excluidoEm = excluidoEm;
    }
}
