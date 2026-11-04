package com.br.manager.domain.operacional.entity;

import com.br.manager.domain.operacional.enums.ProdutoTypeEnum;
import com.br.manager.domain.operacional.enums.TipoUnidadeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotBlank(message = "SKU é obrigatório.")
    @Column(name = "sku", nullable = false, length = 100)
    private String sku;

    @NotBlank(message = "Produto nome é obrigatório.")
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotNull(message = "Produto type é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_produto", nullable = false)
    private ProdutoTypeEnum produtoType;

    @NotNull(message = "Produto unidade é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "unidade", nullable = false)
    private TipoUnidadeEnum unidade;

    @Column(name = "anp_codigo", length = 20)
    private String anpCode;

    @Column(name = "ncm", length = 10)
    private String ncm;

    @Column(name = "cest", length = 10)
    private String cest;

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
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public ProdutoTypeEnum getProdutoType() { return produtoType; }
    public void setProdutoType(ProdutoTypeEnum produtoType) { this.produtoType = produtoType; }
    public TipoUnidadeEnum getUnidade() { return unidade; }
    public void setUnidade(TipoUnidadeEnum unidade) { this.unidade = unidade; }
    public String getAnpCode() { return anpCode; }
    public void setAnpCode(String anpCode) { this.anpCode = anpCode; }
    public String getNcm() { return ncm; }
    public void setNcm(String ncm) { this.ncm = ncm; }
    public String getCest() { return cest; }
    public void setCest(String cest) { this.cest = cest; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
    public LocalDateTime getExcluidoEm() { return excluidoEm; }
    public void setExcluidoEm(LocalDateTime excluidoEm) { this.excluidoEm = excluidoEm; }
}
