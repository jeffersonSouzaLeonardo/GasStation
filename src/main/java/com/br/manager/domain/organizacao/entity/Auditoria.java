package com.br.manager.domain.organizacao.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "logs_auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @NotBlank(message = "O nome da entidade é obrigatório.")
    @Column(name = "nome_entidade", nullable = false, length = 100)
    private String entityName;

    @NotBlank(message = "O ID da entidade é obrigatório.")
    @Column(name = "entidade_id", nullable = false, length = 100)
    private String entityId;

    @NotBlank(message = "A ação é obrigatória.")
    @Column(name = "acao", nullable = false, length = 50)
    private String acao;

    @Column(name = "dados_anteriores", columnDefinition = "jsonb")
    private String beforeData;

    @Column(name = "dados_posteriores", columnDefinition = "jsonb")
    private String afterData;

    @Column(name = "endereco_ip", length = 45)
    private String enderecoIp;

    @NotNull(message = "A data de criação é obrigatória.")
    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm;

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

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public String getBeforeData() {
        return beforeData;
    }

    public void setBeforeData(String beforeData) {
        this.beforeData = beforeData;
    }

    public String getAfterData() {
        return afterData;
    }

    public void setAfterData(String afterData) {
        this.afterData = afterData;
    }

    public String getEnderecoIp() {
        return enderecoIp;
    }

    public void setEnderecoIp(String enderecoIp) {
        this.enderecoIp = enderecoIp;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(OffsetDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
