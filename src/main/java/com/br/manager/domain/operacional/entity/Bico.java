package com.br.manager.domain.operacional.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bicos")
public class Bico {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "O ID de Bomba é obrigatório.")
    @Column(name = "bomba_id", nullable = false)
    private UUID bombaId;

    @NotNull(message = "O ID de Tanque é obrigatório.")
    @Column(name = "tanque_id", nullable = false)
    private UUID tanqueId;

    @NotBlank(message = "Bico codigo é obrigatório.")
    @Size(min = 1, max = 50, message = "Bico codigo deve ter entre 1 e 50 caracteres.")
    @Column(name = "codigo", nullable = false, length = 50)
    private String codigo;

    @NotNull(message = "O ID de Produto é obrigatório.")
    @Column(name = "produto_id", nullable = false)
    private UUID produtoId;

    @Column(name = "numero_medidor", precision = 15, scale = 4)
    private BigDecimal numeroMedidor = BigDecimal.ZERO;

    @NotNull(message = "Ativo indicador é obrigatório.")
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Column(name = "excluido_em")
    private LocalDateTime excluidoEm;
public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getBombaId() { return bombaId; }
    public void setBombaId(UUID bombaId) { this.bombaId = bombaId; }
    public UUID getTanqueId() { return tanqueId; }
    public void setTanqueId(UUID tanqueId) { this.tanqueId = tanqueId; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public UUID getProdutoId() { return produtoId; }
    public void setProdutoId(UUID produtoId) { this.produtoId = produtoId; }
    public BigDecimal getNumeroMedidor() { return numeroMedidor; }
    public void setNumeroMedidor(BigDecimal numeroMedidor) { this.numeroMedidor = numeroMedidor; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
    public LocalDateTime getExcluidoEm() { return excluidoEm; }
    public void setExcluidoEm(LocalDateTime excluidoEm) { this.excluidoEm = excluidoEm; }
}
