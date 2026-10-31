package com.br.manager.domain.organization.fixture;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * Gera {@link ConstraintViolationException} reais (Bean Validation) a partir de entidades invalidas.
 */
public final class ConstraintViolationFixture {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private ConstraintViolationFixture() {
    }

    public static ConstraintViolationException violationsOf(Object invalidBean) {
        return new ConstraintViolationException(VALIDATOR.validate(invalidBean));
    }
}
