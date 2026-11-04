package com.br.manager.domain.organizacao.dto;

import com.br.manager.domain.organizacao.enums.TipoCargoEnum;

import java.util.UUID;

public class VinculoUsuarioResponseDTO {
    private UUID id;
    private UUID usuarioId;
    private UUID empresaId;
    private UUID estacaoId;
    private TipoCargoEnum cargo;
    private Boolean ativo;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public UUID getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(UUID empresaId) {
        this.empresaId = empresaId;
    }

    public UUID getEstacaoId() {
        return estacaoId;
    }

    public void setEstacaoId(UUID estacaoId) {
        this.estacaoId = estacaoId;
    }

    public TipoCargoEnum getCargo() {
        return cargo;
    }

    public void setCargo(TipoCargoEnum cargo) {
        this.cargo = cargo;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
