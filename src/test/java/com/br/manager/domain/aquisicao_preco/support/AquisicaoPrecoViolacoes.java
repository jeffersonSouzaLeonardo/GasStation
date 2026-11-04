package com.br.manager.domain.aquisicao_preco.support;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;

/**
 * Cria excecoes reais de Bean Validacao para simular falhas de validacao disparadas pelo JPA.
 */
public final class AquisicaoPrecoViolacoes {

    public static final String VIOLATION_MESSAGE = "Valor invalido.";

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private AquisicaoPrecoViolacoes() {
    }

    public static ConstraintViolationException constraintViolationException() {
        return new ConstraintViolationException(VALIDATOR.validate(new InvalidoBean()));
    }

    private static final class InvalidoBean {
        @NotNull(message = VIOLATION_MESSAGE)
        private String value;
    }
}
