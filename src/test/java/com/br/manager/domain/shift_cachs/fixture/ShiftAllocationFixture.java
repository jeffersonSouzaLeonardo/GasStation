package com.br.manager.domain.shift_cachs.fixture;

import com.br.manager.domain.shift_cachs.dto.ShiftAllocationInputDTO;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.entity.ShiftAllocation;
import com.br.manager.domain.shift_cachs.enums.ShiftAllocationRoleEnum;

import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Alocacao de Turno.
 */
public final class ShiftAllocationFixture {

    public static final ShiftAllocationRoleEnum ROLE = ShiftAllocationRoleEnum.CASHIER;
    public static final ShiftAllocationRoleEnum OTHER_ROLE = ShiftAllocationRoleEnum.PUMP_ATTENDANT;

    private ShiftAllocationFixture() {
    }

    public static ShiftAllocationInputDTO validInput(UUID userId) {
        return input(userId, ROLE);
    }

    public static ShiftAllocationInputDTO validInput() {
        return validInput(UUID.randomUUID());
    }

    public static ShiftAllocationInputDTO input(UUID userId, ShiftAllocationRoleEnum role) {
        ShiftAllocationInputDTO input = new ShiftAllocationInputDTO();
        input.setUserId(userId);
        input.setRole(role);
        return input;
    }

    public static ShiftAllocation persistedEntity(Shift shift, UUID userId) {
        ShiftAllocation allocation = new ShiftAllocation();
        allocation.setId(UUID.randomUUID());
        allocation.setShift(shift);
        allocation.setUserId(userId);
        allocation.setRole(ROLE);
        return allocation;
    }
}
