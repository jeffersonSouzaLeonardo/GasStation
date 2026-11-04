package com.br.manager.domain.organizacao.entity;

import com.br.manager.domain.organizacao.enums.TipoCargoEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "usuario_estacao_cargos")
public class VinculoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "O ID do usuário é obrigatório.")
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @NotNull(message = "O ID da empresa é obrigatório.")
    @Column(name = "empresa_id", nullable = false)
    private UUID empresaId;

    @Column(name = "estacao_id")
    private UUID estacaoId;

    @NotNull(message = "O papel é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "cargo", nullable = false)
    private TipoCargoEnum cargo;

    @NotNull(message = "O campo ativo é obrigatório.")
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

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
