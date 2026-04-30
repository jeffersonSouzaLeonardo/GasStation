package com.br.manager.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundBusinessException extends BusinessException {

    public NotFoundBusinessException(String message){
        super(message);
    }

    public NotFoundBusinessException(String message, Throwable cause){
        super(message, cause);
    }
}
