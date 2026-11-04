package com.br.manager.domain.turno_caixa.entity;

import com.br.manager.domain.turno_caixa.enums.CargoAlocacaoTurnoEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "alocacoes_turno",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_turno_alocacao_turno_usuario", columnNames = {"turno_id", "usuario_id"})
        })
public class AlocacaoTurno {

    @Id
@Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "Turno é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turno_id", nullable = false)
    private Turno turno;

    @NotNull(message = "O ID de Usuario é obrigatório.")
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @NotNull(message = "Alocacao cargo é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "cargo", nullable = false, length = 30)
    private CargoAlocacaoTurnoEnum cargo;
public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
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
