package com.br.manager.domain.operacional.support;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * Gera ConstraintViolationException reais (via Bean Validacao) para simular
 * falhas de validacao disparadas pelo Hibernate no momento do flush.
 */
public final class OperacionalBViolacoes {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private OperacionalBViolacoes() {
    }

    public static ConstraintViolationException violacoesOf(Object invalidoBean) {
        return new ConstraintViolationException(VALIDATOR.validate(invalidoBean));
    }
}
