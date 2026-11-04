package com.br.manager.domain.operacional.dto;

import com.br.manager.domain.operacional.enums.TipoMetodoPagamentoEnum;

import java.util.UUID;

public class MetodoPagamentoInputDTO {
    private UUID id;
    private String nome;
    private TipoMetodoPagamentoEnum tipo;
    private Boolean exigeReferencia = false;
    private Integer diasLiquidacao = 0;
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
