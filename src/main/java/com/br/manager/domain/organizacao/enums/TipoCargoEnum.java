package com.br.manager.domain.organizacao.enums;

public enum TipoCargoEnum {
    ADMIN,
    GERENTE,
    CAIXA,
    FRENTISTA;

    public String nome() {
        return name();
    }
}