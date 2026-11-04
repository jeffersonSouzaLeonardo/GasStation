package com.br.manager.domain.operacional.enums;

public enum TipoMetodoPagamentoEnum {
    DINHEIRO("Dinheiro"),
    CARTAO_CREDITO("Cartão de crédito"),
    CARTAO_DEBITO("Cartão de débito"),
    PIX("PIX"),
    CONVENIO("Convênio"),
    TRANSFERENCIA_BANCARIA("Transferência bancária"),
    CHEQUE("Cheque"),
    OUTRO("Outro");

    private final String descricao;

    TipoMetodoPagamentoEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public String nome() {
        return name();
    }
}