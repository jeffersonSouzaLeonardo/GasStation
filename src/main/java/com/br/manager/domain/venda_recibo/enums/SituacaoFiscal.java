package com.br.manager.domain.venda_recibo.enums;

public enum SituacaoFiscal {
    EMISSAO_PENDENTE,
    EMITIDO,
    REJEITADO,
    CANCELADO,
    EMITIDO_EM_CONTINGENCIA;

    public String nome() {
        return name();
    }
}