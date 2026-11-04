package com.br.manager.domain.comum.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class ManipuladorExcecaoApi extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ExcecaoNegocio.class)
    public ResponseEntity<?> responseException(ExcecaoNegocio e, WebRequest request) {
        RespostaExcecaoDTO error = new RespostaExcecaoDTO();
        error.setStatus(HttpStatus.BAD_REQUEST.toString());
        error.setInstance(request.getDescription(false).replace("uri", ""));
        error.setTimestamp(LocalDateTime.now());
        error.setTitle(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
        error.setValidacaoErrors(e.getMessage() != null ? List.of(e.getMessage()) : List.of());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ExcecaoNegocioNaoEncontrado.class)
    public ResponseEntity<?> responseException(ExcecaoNegocioNaoEncontrado e, WebRequest request) {
        return buildResponse(e, request, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        RespostaExcecaoDTO error = new RespostaExcecaoDTO();
        List<String> validacaoErrors = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();

        error.setStatus(HttpStatus.BAD_REQUEST.toString());
        error.setInstance(request.getDescription(false).replace("uri", ""));
        error.setTimestamp(LocalDateTime.now());
        error.setTitle(String.join(", ", validacaoErrors));
        error.setValidacaoErrors(validacaoErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException e, WebRequest request) {
        RespostaExcecaoDTO error = new RespostaExcecaoDTO();
        List<String> validacaoErrors = e.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();

        error.setStatus(HttpStatus.BAD_REQUEST.toString());
        error.setInstance(request.getDescription(false).replace("uri", ""));
        error.setTimestamp(LocalDateTime.now());
        error.setTitle(String.join(", ", validacaoErrors));
        error.setValidacaoErrors(validacaoErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private ResponseEntity<?> buildResponse(RuntimeException e, WebRequest request, HttpStatus fallbackSituacao) {
        RespostaExcecaoDTO error = new RespostaExcecaoDTO();
        ResponseStatus responseSituacao = AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class);
        HttpStatus status = (responseSituacao != null) ? responseSituacao.value() : fallbackSituacao;

        error.setStatus(status.toString());
        error.setInstance(request.getDescription(false).replace("uri", ""));
        error.setTimestamp(LocalDateTime.now());
        error.setTitle(e.getMessage());

        return ResponseEntity.status(status).body(error);
    }
}
