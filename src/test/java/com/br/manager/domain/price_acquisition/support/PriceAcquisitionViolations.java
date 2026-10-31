package com.br.manager.domain.price_acquisition.support;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;

/**
 * Cria excecoes reais de Bean Validation para simular falhas de validacao disparadas pelo JPA.
 */
public final class PriceAcquisitionViolations {

    public static final String VIOLATION_MESSAGE = "Valor invalido.";

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private PriceAcquisitionViolations() {
    }

    public static ConstraintViolationException constraintViolationException() {
        return new ConstraintViolationException(VALIDATOR.validate(new InvalidBean()));
    }

    private static final class InvalidBean {
        @NotNull(message = VIOLATION_MESSAGE)
        private String value;
    }
}
