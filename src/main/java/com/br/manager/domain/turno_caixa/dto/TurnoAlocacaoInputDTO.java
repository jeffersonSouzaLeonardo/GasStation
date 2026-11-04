package com.br.manager.domain.turno_caixa.dto;

import com.br.manager.domain.turno_caixa.enums.CargoAlocacaoTurnoEnum;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class TurnoAlocacaoInputDTO {

    @NotNull(message = "O ID de Usuario é obrigatório.")
    private UUID usuarioId;

    @NotNull(message = "Alocacao cargo é obrigatório.")
    private CargoAlocacaoTurnoEnum cargo;

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public CargoAlocacaoTurnoEnum getCargo() {
        return cargo;
    }

    public void setCargo(CargoAlocacaoTurnoEnum cargo) {
        this.cargo = cargo;
    }
}
