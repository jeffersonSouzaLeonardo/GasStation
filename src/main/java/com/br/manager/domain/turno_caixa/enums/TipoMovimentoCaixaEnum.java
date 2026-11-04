package com.br.manager.domain.turno_caixa.enums;

public enum TipoMovimentoCaixaEnum {
    ABERTURA,
    VENDA,
    SANGRIA,
    SUPRIMENTO,
    REEMBOLSO,
    AJUSTE,
    FECHAMENTO;

    public String nome() {
        return name();
    }
}