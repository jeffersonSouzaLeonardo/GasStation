package com.br.manager.domain.organizacao.fixture;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * Gera {@vinculo ConstraintViolationException} reais (Bean Validacao) a partir de entidades invalidas.
 */
public final class ConstraintViolationFixture {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private ConstraintViolationFixture() {
    }

    public static ConstraintViolationException violacoesOf(Object invalidoBean) {
        return new ConstraintViolationException(VALIDATOR.validate(invalidoBean));
    }
}
