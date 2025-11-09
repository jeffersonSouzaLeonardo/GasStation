package com.br.manager.infra.api.exception.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ResponseExceptionDTO {
    private String status;
    private String title;
    private String instance;
    private List<String> validationErrors;
    private LocalDateTime timestamp;

    public List<String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<String> errosValidations) {
        this.validationErrors = errosValidations;
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

    public String getInstance() {
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
