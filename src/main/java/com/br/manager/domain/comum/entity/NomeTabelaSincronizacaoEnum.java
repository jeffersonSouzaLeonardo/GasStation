package com.br.manager.domain.comum.entity;

public enum NomeTabelaSincronizacaoEnum {
    USUARIO,
    EMPRESA,
    ESTACAO,
    BICO,
    PRODUTO,
    BOMBA,
    TANQUE,
    FORNECEDOR,
    TURNO,
    SESSAO_CAIXA;

    private final String tabelaName;

    NomeTabelaSincronizacaoEnum() {
        this.tabelaName = name().toLowerCase();
    }

    public String getTabelaName() {
        return tabelaName;
    }

    public String nome() {
        return name();
    }
}