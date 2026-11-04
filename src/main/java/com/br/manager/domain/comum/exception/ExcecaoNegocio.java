package com.br.manager.domain.comum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ExcecaoNegocio extends RuntimeException {

    public ExcecaoNegocio(String message){
        super(message);
    }

    public ExcecaoNegocio(String message, Throwable cause){
        super(message, cause);
    }
}
