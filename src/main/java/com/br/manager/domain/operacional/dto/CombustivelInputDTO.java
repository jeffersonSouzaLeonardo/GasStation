package com.br.manager.domain.operacional.dto;

import com.br.manager.domain.operacional.enums.SituacaoCombustivelEnum;
import com.br.manager.domain.operacional.enums.UnidadeCombustivelEnum;

public class CombustivelInputDTO {
    private Long id;
    private String nome;
    private UnidadeCombustivelEnum unidade;
    private SituacaoCombustivelEnum status;
    private String idAnp;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public UnidadeCombustivelEnum getUnidade() {
        return unidade;
    }

    public void setUnidade(UnidadeCombustivelEnum unidade) {
        this.unidade = unidade;
    }

    public SituacaoCombustivelEnum getStatus() {
        return status;
    }

    public void setStatus(SituacaoCombustivelEnum ativo) {
        this.status = ativo;
    }

    public String getIdAnp() {
        return idAnp;
    }

    public void setIdAnp(String idAnp) {
        this.idAnp = idAnp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
