package com.br.manager.domain.operacional.dto;

import com.br.manager.domain.operacional.enums.SituacaoCombustivelEnum;
import com.br.manager.domain.operacional.enums.UnidadeCombustivelEnum;

public class CombustivelResponseDTO {
    private Long id;
    private String nome;
    private UnidadeCombustivelEnum unidade;
    private SituacaoCombustivelEnum status;
    private String idAnp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getIdAnp() {
        return idAnp;
    }

    public void setIdAnp(String idAnp) {
        this.idAnp = idAnp;
    }

    public SituacaoCombustivelEnum getStatus() {
        return status;
    }

    public void setStatus(SituacaoCombustivelEnum status) {
        this.status = status;
    }
}
