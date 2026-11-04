package com.br.manager.domain.venda_recibo.enums;

public enum VendaSituacao {
    ABERTO,
    PAGO,
    CANCELADO,
    REEMBOLSADO,
    CREDITO_PENDENTE;

    public String nome() {
        return name();
    }
}