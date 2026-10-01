package com.br.manager.domain.shift_cachs.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class ShiftCloseRequestDTO {

    @NotNull(message = "Closed by is required.")
    private UUID closedBy;

    public UUID getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(UUID closedBy) {
        this.closedBy = closedBy;
    }
}
