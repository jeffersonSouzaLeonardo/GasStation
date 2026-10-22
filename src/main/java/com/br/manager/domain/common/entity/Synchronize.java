package com.br.manager.domain.common.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "synchronize")
public class Synchronize {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "O nome da tabela é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private SynchronizeNameTableEnum nameTable;

    @Column(name = "last_synchronize")
    private LocalDateTime lastSynchronize;

    public LocalDateTime getLastSynchronize() {
        return lastSynchronize;
    }

    public void setLastSynchronize(LocalDateTime lastSynchronize) {
        this.lastSynchronize = lastSynchronize;
    }

    public SynchronizeNameTableEnum getNameTable() {
        return nameTable;
    }

}
