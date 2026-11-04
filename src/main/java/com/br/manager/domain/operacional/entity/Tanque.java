package com.br.manager.domain.operacional.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tanques")
public class Tanque {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotBlank(message = "O código do tanque é obrigatório.")
    @Column(name = "codigo", nullable = false, length = 50)
    private String codigo;

    @NotNull(message = "O ID do produto é obrigatório.")
    @Column(name = "produto_id", nullable = false)
    private UUID produtoId;

    @NotNull(message = "A capacidade do tanque é obrigatória.")
    @DecimalMin(value = "0.0001", message = "A capacidade deve ser maior que zero.")
    @Column(name = "capacidade_litros", nullable = false, precision = 15, scale = 4)
    private BigDecimal capacidadeLitros;

    @Column(name = "estoque_morto_litros", precision = 15, scale = 4)
    private BigDecimal estoqueMortoLitros = BigDecimal.ZERO;

    @NotNull(message = "Os litros contabilizados atuais são obrigatórios.")
    @Column(name = "saldo_contabil_litros", nullable = false, precision = 15, scale = 4)
    private BigDecimal saldoContabilLitros = BigDecimal.ZERO;

    @NotNull(message = "O campo ativo é obrigatório.")
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
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public UUID getProdutoId() { return produtoId; }
    public void setProdutoId(UUID produtoId) { this.produtoId = produtoId; }
    public BigDecimal getCapacidadeLitros() { return capacidadeLitros; }
    public void setCapacidadeLitros(BigDecimal capacidadeLitros) { this.capacidadeLitros = capacidadeLitros; }
    public BigDecimal getEstoqueMortoLitros() { return estoqueMortoLitros; }
    public void setEstoqueMortoLitros(BigDecimal estoqueMortoLitros) { this.estoqueMortoLitros = estoqueMortoLitros; }
    public BigDecimal getSaldoContabilLitros() { return saldoContabilLitros; }
    public void setSaldoContabilLitros(BigDecimal saldoContabilLitros) { this.saldoContabilLitros = saldoContabilLitros; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
    public LocalDateTime getExcluidoEm() { return excluidoEm; }
    public void setExcluidoEm(LocalDateTime excluidoEm) { this.excluidoEm = excluidoEm; }
}
