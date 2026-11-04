package com.br.manager.domain.turno_caixa.enums;

public enum CargoAlocacaoTurnoEnum {
    CAIXA,
    FRENTISTA,
    SUPERVISOR,
    ESTOQUISTA;

    public String nome() {
        return name();
    }
}