package com.br.manager.domain.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ApiExceptionHandle extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> responseException(BusinessException e, WebRequest request){

        ResponseExceptionDTO error = new ResponseExceptionDTO();

        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class);
        HttpStatus status = (responseStatus != null) ? responseStatus.code() : HttpStatus.BAD_REQUEST;

        error.setStatus(status.toString());
        error.setInstance(request.getDescription(false).replace("uri", ""));
        error.setTimestamp(LocalDateTime.now());
        error.setTitle(e.getMessage());

        return ResponseEntity.status(status).body(error);
    }

}
