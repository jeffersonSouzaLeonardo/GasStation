package com.br.manager.domain.shift_cachs.dto;

import com.br.manager.domain.shift_cachs.enums.ShiftAllocationRoleEnum;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class ShiftAllocationInputDTO {

    @NotNull(message = "User ID is required.")
    private UUID userId;

    @NotNull(message = "Allocation role is required.")
    private ShiftAllocationRoleEnum role;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public ShiftAllocationRoleEnum getRole() {
        return role;
    }

    public void setRole(ShiftAllocationRoleEnum role) {
        this.role = role;
    }
}
