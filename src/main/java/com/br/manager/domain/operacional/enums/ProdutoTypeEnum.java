package com.br.manager.domain.operacional.enums;

public enum ProdutoTypeEnum {
    COMBUSTIVEL,
    LUBRIFICANTE,
    SERVICO,
    CONVENIENCIA;

    public String nome() {
        return name();
    }
}