package com.br.manager.domain.comum.exception;

import com.br.manager.domain.organizacao.entity.Estacao;
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

class ManipuladorExcecaoApiTest {

    private static final String PATH = "/estacao";
    private static final String MESSAGE = "Erro ao salvar posto";
    private static final String CAUSE_MESSAGE = "Duplicate entry";
    private static final String NAME_OBRIGATORIO = "O nome do posto é obrigatório.";

    private final ManipuladorExcecaoApi handler = new ManipuladorExcecaoApi();
    private final WebRequest request = new ServletWebRequest(new MockHttpServletRequest("POST", PATH));

    @Test
    void shouldReturnBadRequestUsingMessageWhenExcecaoNegocioHasNoCause() {
        ResponseEntity<?> response = handler.responseException(new ExcecaoNegocio(MESSAGE), request);

        RespostaExcecaoDTO body = (RespostaExcecaoDTO) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.toString());
        assertThat(body.getTitle()).isEqualTo(MESSAGE);
        assertThat(body.getValidacaoErrors()).containsExactly(MESSAGE);
        assertThat(body.getINSTANCE()).endsWith(PATH);
        assertThat(body.getTimestamp()).isNotNull();
    }

    @Test
    void shouldUseCauseMessageAsTitleWhenExcecaoNegocioHasCause() {
        ExcecaoNegocio exception = new ExcecaoNegocio(MESSAGE, new IllegalStateException(CAUSE_MESSAGE));

        ResponseEntity<?> response = handler.responseException(exception, request);

        RespostaExcecaoDTO body = (RespostaExcecaoDTO) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body.getTitle()).isEqualTo(CAUSE_MESSAGE);
        assertThat(body.getValidacaoErrors()).containsExactly(MESSAGE);
    }

    @Test
    void shouldReturnEmptyValidacaoErrorsWhenExcecaoNegocioHasNoMessage() {
        ResponseEntity<?> response = handler.responseException(new ExcecaoNegocio(null), request);

        RespostaExcecaoDTO body = (RespostaExcecaoDTO) response.getBody();
        assertThat(body.getValidacaoErrors()).isEmpty();
    }

    @Test
    void shouldReturnNotFoundForNotFoundExcecaoNegocio() {
        ResponseEntity<?> response = handler.responseException(new ExcecaoNegocioNaoEncontrado("Posto não encontrado"), request);

        RespostaExcecaoDTO body = (RespostaExcecaoDTO) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(body.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.toString());
        assertThat(body.getTitle()).isEqualTo("Posto não encontrado");
        assertThat(body.getINSTANCE()).endsWith(PATH);
        assertThat(body.getTimestamp()).isNotNull();
    }

    @Test
    void shouldListFieldErrorsForMethodArgumentNotValido() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Estacao(), "estacao");
        bindingResult.addError(new FieldError("estacao", "nome", NAME_OBRIGATORIO));
        bindingResult.addError(new FieldError("estacao", "empresaId", "O ID da empresa é obrigatório."));
        MethodParameter parameter = new MethodParameter(
                ManipuladorExcecaoApiTest.class.getDeclaredMethod("validatedEndpoint", Estacao.class), 0);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
                exception, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);

        RespostaExcecaoDTO body = (RespostaExcecaoDTO) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body.getValidacaoErrors())
                .containsExactly("nome: " + NAME_OBRIGATORIO, "empresaId: O ID da empresa é obrigatório.");
        assertThat(body.getTitle()).isEqualTo("nome: " + NAME_OBRIGATORIO + ", empresaId: O ID da empresa é obrigatório.");
        assertThat(body.getINSTANCE()).endsWith(PATH);
    }

    @Test
    void shouldListViolacoesForConstraintViolationException() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Estacao estacao = new Estacao();
        estacao.setEmpresaId(UUID.randomUUID());
        ConstraintViolationException exception = new ConstraintViolationException(validator.validate(estacao));

        ResponseEntity<?> response = handler.handleConstraintViolation(exception, request);

        RespostaExcecaoDTO body = (RespostaExcecaoDTO) response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.toString());
        assertThat(body.getValidacaoErrors()).containsExactly("nome: " + NAME_OBRIGATORIO);
        assertThat(body.getTitle()).isEqualTo("nome: " + NAME_OBRIGATORIO);
        assertThat(body.getINSTANCE()).endsWith(PATH);
    }

    @SuppressWarnings("unused")
    private void validatedEndpoint(Estacao estacao) {
    }
}
