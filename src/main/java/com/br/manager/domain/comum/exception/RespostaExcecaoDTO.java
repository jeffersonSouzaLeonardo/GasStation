package com.br.manager.domain.comum.exception;

import java.time.LocalDateTime;
import java.util.List;

public class RespostaExcecaoDTO {
    private String status;
    private String title;
    private String instance;
    private List<String> validacaoErrors;
    private LocalDateTime timestamp;

    public List<String> getValidacaoErrors() {
        return validacaoErrors;
    }

    public void setValidacaoErrors(List<String> errosValidations) {
        this.validacaoErrors = errosValidations;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getINSTANCE() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

}
