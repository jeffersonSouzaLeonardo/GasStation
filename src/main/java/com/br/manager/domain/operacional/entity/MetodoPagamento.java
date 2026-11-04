package com.br.manager.domain.operacional.entity;

import com.br.manager.domain.operacional.enums.TipoMetodoPagamentoEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "metodos_pagamento")
public class MetodoPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotBlank(message = "Pagamento method nome é obrigatório.")
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @NotNull(message = "Pagamento method tipo é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 50)
    private TipoMetodoPagamentoEnum tipo;

    @NotNull(message = "Referencia requirement indicador é obrigatório.")
    @Column(name = "exige_referencia", nullable = false)
    private Boolean exigeReferencia = false;

    @Column(name = "dias_liquidacao")
    private Integer diasLiquidacao = 0;

    @NotNull(message = "Ativo indicador é obrigatório.")
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;
public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public TipoMetodoPagamentoEnum getTipo() { return tipo; }
    public void setTipo(TipoMetodoPagamentoEnum tipo) { this.tipo = tipo; }
    public Boolean getExigeReferencia() { return exigeReferencia; }
    public void setExigeReferencia(Boolean exigeReferencia) { this.exigeReferencia = exigeReferencia; }
    public Integer getDiasLiquidacao() { return diasLiquidacao; }
    public void setDiasLiquidacao(Integer diasLiquidacao) { this.diasLiquidacao = diasLiquidacao; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
