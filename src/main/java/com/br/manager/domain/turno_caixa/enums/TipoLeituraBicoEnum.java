package com.br.manager.domain.turno_caixa.enums;

public enum TipoLeituraBicoEnum {
    ABERTURA,
    FECHAMENTO,
    TESTE,
    CALIBRACAO;

    public String nome() {
        return name();
    }
}