package com.br.manager.domain.operacional.entity;

import com.br.manager.domain.operacional.enums.SituacaoCombustivelEnum;
import com.br.manager.domain.operacional.enums.UnidadeCombustivelEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
public class Combustivel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do combustível é obrigatório.")
    private String nome;

    @NotNull(message = "A unidade de medida é obrigatória.")
    @Enumerated(EnumType.STRING)
    @Column(name = "unidade")
    private UnidadeCombustivelEnum unidade;

    @NotNull(message = "O status do combustível é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SituacaoCombustivelEnum status;

    @Column(name = "id_anp")
    private String idAnp;

    @Column(name = "excluido")
    private LocalDateTime excluido;

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

    public String getIdAnp() {
        return idAnp;
    }

    public void setIdAnp(String idAnp) {
        this.idAnp = idAnp;
    }

    public LocalDateTime getExcluido() {
        return excluido;
    }

    public void setExcluido(LocalDateTime excluido) {
        this.excluido = excluido;
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

    public void setStatus(SituacaoCombustivelEnum status) {
        this.status = status;
    }
}
