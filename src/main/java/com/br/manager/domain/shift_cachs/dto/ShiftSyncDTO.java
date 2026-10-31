package com.br.manager.domain.shift_cachs.dto;

import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Turno recebido do PDV no endpoint de sincronização (contrato fixo com o PDV).
 */
public class ShiftSyncDTO {

    @NotNull(message = "O ID do turno é obrigatório.")
    private UUID id;

    @NotNull(message = "O ID do posto é obrigatório.")
    private UUID stationId;

    private String name;

    @NotNull(message = "A data de abertura é obrigatória.")
    private LocalDateTime openedAt;

    private LocalDateTime closedAt;

    @NotNull(message = "O status do turno é obrigatório.")
    private ShiftStatusEnum status;

    @NotNull(message = "O usuário que abriu é obrigatório.")
    private UUID openedBy;

    private UUID closedBy;

    private String notes;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getStationId() {
        return stationId;
    }

    public void setStationId(UUID stationId) {
        this.stationId = stationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public ShiftStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ShiftStatusEnum status) {
        this.status = status;
    }

    public UUID getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(UUID openedBy) {
        this.openedBy = openedBy;
    }

    public UUID getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(UUID closedBy) {
        this.closedBy = closedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
