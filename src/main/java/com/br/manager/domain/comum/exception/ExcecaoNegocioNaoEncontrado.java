package com.br.manager.domain.comum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ExcecaoNegocioNaoEncontrado extends ExcecaoNegocio {

    public ExcecaoNegocioNaoEncontrado(String message){
        super(message);
    }

    public ExcecaoNegocioNaoEncontrado(String message, Throwable cause){
        super(message, cause);
    }
}
