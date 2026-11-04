package com.br.manager.domain.turno_caixa.dto;

import com.br.manager.domain.turno_caixa.enums.CargoAlocacaoTurnoEnum;

import java.util.UUID;

public class TurnoAlocacaoResponseDTO {

    private UUID id;
    private UUID turnoId;
    private UUID usuarioId;
    private CargoAlocacaoTurnoEnum cargo;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTurnoId() {
        return turnoId;
    }

    public void setTurnoId(UUID turnoId) {
        this.turnoId = turnoId;
    }

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
