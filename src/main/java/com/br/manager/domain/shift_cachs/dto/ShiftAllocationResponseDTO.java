package com.br.manager.domain.shift_cachs.dto;

import com.br.manager.domain.shift_cachs.enums.ShiftAllocationRoleEnum;

import java.util.UUID;

public class ShiftAllocationResponseDTO {

    private UUID id;
    private UUID shiftId;
    private UUID userId;
    private ShiftAllocationRoleEnum role;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getShiftId() {
        return shiftId;
    }

    public void setShiftId(UUID shiftId) {
        this.shiftId = shiftId;
    }

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
