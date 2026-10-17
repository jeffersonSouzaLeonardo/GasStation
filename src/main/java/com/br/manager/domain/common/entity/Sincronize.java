package com.br.manager.domain.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sincronize")
public class Sincronize {

    @Id
    private UUID id;

    @NotBlank(message = "O nome da tabela é obrigatório.")
    @Column(name = "name", nullable = false)
    private SincronizeNameTableEnum nameTable;

    @Column(name = "last_sincronize")
    private LocalDateTime lastSincronize;

    public LocalDateTime getLastSincronize() {
        return lastSincronize;
    }

    public void setLastSincronize(LocalDateTime lastSincronize) {
        this.lastSincronize = lastSincronize;
    }

    public SincronizeNameTableEnum getNameTable() {
        return nameTable;
    }

}
