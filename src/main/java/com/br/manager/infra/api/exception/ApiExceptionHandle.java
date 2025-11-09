package com.br.manager.infra.api.exception;

import com.br.manager.common.BusinessException;
import com.br.manager.infra.api.exception.dto.ResponseExceptionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ApiExceptionHandle extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> responseException(BusinessException e, WebRequest request){

        ResponseExceptionDTO error = new ResponseExceptionDTO();
                error.setStatus(HttpStatus.BAD_REQUEST.toString());
                error.setInstance(request.getDescription(false).replace("uri", ""));
                error.setTimestamp(LocalDateTime.now());
                error.setTitle(e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

}
