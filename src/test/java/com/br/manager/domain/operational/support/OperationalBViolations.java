package com.br.manager.domain.operational.support;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * Gera ConstraintViolationException reais (via Bean Validation) para simular
 * falhas de validacao disparadas pelo Hibernate no momento do flush.
 */
public final class OperationalBViolations {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private OperationalBViolations() {
    }

    public static ConstraintViolationException violationsOf(Object invalidBean) {
        return new ConstraintViolationException(VALIDATOR.validate(invalidBean));
    }
}
