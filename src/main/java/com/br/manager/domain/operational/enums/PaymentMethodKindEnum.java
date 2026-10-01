package com.br.manager.domain.operational.enums;

public enum PaymentMethodKindEnum {
    MONEY("Dinheiro"),
    CREDIT_CARD("Cartão de crédito"),
    DEBIT_CARD("Cartão de débito"),
    PIX("PIX"),
    CONVENIO("Convênio"),
    BANK_TRANSFER("Transferência bancária"),
    CHECK("Cheque"),
    OTHER("Outro");

    private final String descricao;

    PaymentMethodKindEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
