package com.br.manager.domain.shift_cachs.mapper;

import com.br.manager.domain.shift_cachs.dto.CashMovementInputDTO;
import com.br.manager.domain.shift_cachs.dto.CashMovementResponseDTO;
import com.br.manager.domain.shift_cachs.entity.CashMovement;
import com.br.manager.domain.shift_cachs.entity.Shift;
import org.springframework.stereotype.Component;

@Component
public class CashMovementMapper {

    public CashMovement toEntity(CashMovementInputDTO dto, Shift shift) {
        if (dto == null) {
            return null;
        }

        CashMovement movement = new CashMovement();
        movement.setShift(shift);
        movement.setMovementType(dto.getMovementType());
        movement.setPaymentMethodId(dto.getPaymentMethodId());
        movement.setAmount(dto.getAmount());
        movement.setOccurredAt(dto.getOccurredAt());
        movement.setReference(dto.getReference());
        movement.setReason(dto.getReason());
        movement.setCreatedBy(dto.getCreatedBy());
        movement.setApprovedBy(dto.getApprovedBy());
        return movement;
    }

    public CashMovementResponseDTO toResponse(CashMovement movement) {
        if (movement == null) {
            return null;
        }

        CashMovementResponseDTO dto = new CashMovementResponseDTO();
        dto.setId(movement.getId());
        dto.setShiftId(movement.getShift() != null ? movement.getShift().getId() : null);
        dto.setMovementType(movement.getMovementType());
        dto.setPaymentMethodId(movement.getPaymentMethodId());
        dto.setAmount(movement.getAmount());
        dto.setOccurredAt(movement.getOccurredAt());
        dto.setReference(movement.getReference());
        dto.setReason(movement.getReason());
        dto.setCreatedBy(movement.getCreatedBy());
        dto.setApprovedBy(movement.getApprovedBy());
        return dto;
    }
}
