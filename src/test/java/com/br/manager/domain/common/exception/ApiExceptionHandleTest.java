package com.br.manager.domain.common.exception;

import com.br.manager.domain.organization.entity.Station;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandleTest {

    private static final String PATH = "/station";
    private static final String MESSAGE = "Erro ao salvar posto";
    private static final String CAUSE_MESSAGE = "Duplicate entry";
    private static final String NAME_REQUIRED = "O nome do posto é obrigatório.";

    private final ApiExceptionHandle handler = new ApiExceptionHandle();
    private final WebRequest request = new ServletWebRequest(new MockHttpServletRequest("POST", PATH));

    @Test
    void shouldReturnBadRequestUsingMessageWhenBusinessExceptionHasNoCause() {
        ResponseEntity<?> response = handler.responseException(new BusinessException(MESSAGE), request);

        ResponseExceptionDTO body = (ResponseExceptionDTO) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.toString());
        assertThat(body.getTitle()).isEqualTo(MESSAGE);
        assertThat(body.getValidationErrors()).containsExactly(MESSAGE);
        assertThat(body.getInstance()).endsWith(PATH);
        assertThat(body.getTimestamp()).isNotNull();
    }

    @Test
    void shouldUseCauseMessageAsTitleWhenBusinessExceptionHasCause() {
        BusinessException exception = new BusinessException(MESSAGE, new IllegalStateException(CAUSE_MESSAGE));

        ResponseEntity<?> response = handler.responseException(exception, request);

        ResponseExceptionDTO body = (ResponseExceptionDTO) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body.getTitle()).isEqualTo(CAUSE_MESSAGE);
        assertThat(body.getValidationErrors()).containsExactly(MESSAGE);
    }

    @Test
    void shouldReturnEmptyValidationErrorsWhenBusinessExceptionHasNoMessage() {
        ResponseEntity<?> response = handler.responseException(new BusinessException(null), request);

        ResponseExceptionDTO body = (ResponseExceptionDTO) response.getBody();
        assertThat(body.getValidationErrors()).isEmpty();
    }

    @Test
    void shouldReturnNotFoundForNotFoundBusinessException() {
        ResponseEntity<?> response = handler.responseException(new NotFoundBusinessException("Posto não encontrado"), request);

        ResponseExceptionDTO body = (ResponseExceptionDTO) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(body.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.toString());
        assertThat(body.getTitle()).isEqualTo("Posto não encontrado");
        assertThat(body.getInstance()).endsWith(PATH);
        assertThat(body.getTimestamp()).isNotNull();
    }

    @Test
    void shouldListFieldErrorsForMethodArgumentNotValid() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Station(), "station");
        bindingResult.addError(new FieldError("station", "name", NAME_REQUIRED));
        bindingResult.addError(new FieldError("station", "companyId", "O ID da empresa é obrigatório."));
        MethodParameter parameter = new MethodParameter(
                ApiExceptionHandleTest.class.getDeclaredMethod("validatedEndpoint", Station.class), 0);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
                exception, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);

        ResponseExceptionDTO body = (ResponseExceptionDTO) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body.getValidationErrors())
                .containsExactly("name: " + NAME_REQUIRED, "companyId: O ID da empresa é obrigatório.");
        assertThat(body.getTitle()).isEqualTo("name: " + NAME_REQUIRED + ", companyId: O ID da empresa é obrigatório.");
        assertThat(body.getInstance()).endsWith(PATH);
    }

    @Test
    void shouldListViolationsForConstraintViolationException() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Station station = new Station();
        station.setCompanyId(UUID.randomUUID());
        ConstraintViolationException exception = new ConstraintViolationException(validator.validate(station));

        ResponseEntity<?> response = handler.handleConstraintViolation(exception, request);

        ResponseExceptionDTO body = (ResponseExceptionDTO) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.toString());
        assertThat(body.getValidationErrors()).containsExactly("name: " + NAME_REQUIRED);
        assertThat(body.getTitle()).isEqualTo("name: " + NAME_REQUIRED);
        assertThat(body.getInstance()).endsWith(PATH);
    }

    @SuppressWarnings("unused")
    private void validatedEndpoint(Station station) {
    }
}
