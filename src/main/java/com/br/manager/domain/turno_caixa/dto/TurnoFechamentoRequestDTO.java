package com.br.manager.domain.turno_caixa.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class TurnoFechamentoRequestDTO {

    @NotNull(message = "Fechado by é obrigatório.")
    private UUID fechadoBy;

    public UUID getFechadoBy() {
        return fechadoBy;
    }

    public void setFechadoBy(UUID fechadoBy) {
        this.fechadoBy = fechadoBy;
    }
}
