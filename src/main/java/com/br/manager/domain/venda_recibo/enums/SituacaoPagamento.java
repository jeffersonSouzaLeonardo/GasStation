package com.br.manager.domain.venda_recibo.enums;

public enum SituacaoPagamento {
    PENDENTE,
    AUTORIZADO,
    RECEBIDO,
    REJEITADO,
    REEMBOLSADO;

    public String nome() {
        return name();
    }
}