package com.br.manager.domain.shift_cachs.mapper;

import com.br.manager.domain.shift_cachs.dto.ShiftAllocationInputDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftAllocationResponseDTO;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.entity.ShiftAllocation;
import org.springframework.stereotype.Component;

@Component
public class ShiftAllocationMapper {

    public ShiftAllocation toEntity(ShiftAllocationInputDTO dto, Shift shift) {
        if (dto == null) {
            return null;
        }

        ShiftAllocation allocation = new ShiftAllocation();
        allocation.setShift(shift);
        allocation.setUserId(dto.getUserId());
        allocation.setRole(dto.getRole());
        return allocation;
    }

    public ShiftAllocationResponseDTO toResponse(ShiftAllocation allocation) {
        if (allocation == null) {
            return null;
        }

        ShiftAllocationResponseDTO dto = new ShiftAllocationResponseDTO();
        dto.setId(allocation.getId());
        dto.setShiftId(allocation.getShift() != null ? allocation.getShift().getId() : null);
        dto.setUserId(allocation.getUserId());
        dto.setRole(allocation.getRole());
        return dto;
    }
}
