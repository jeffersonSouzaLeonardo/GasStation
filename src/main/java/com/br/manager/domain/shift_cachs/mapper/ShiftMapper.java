package com.br.manager.domain.shift_cachs.mapper;

import com.br.manager.domain.shift_cachs.dto.ShiftOpenRequestDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftResponseDTO;
import com.br.manager.domain.shift_cachs.entity.Shift;
import org.springframework.stereotype.Component;

@Component
public class ShiftMapper {

    public Shift toEntity(ShiftOpenRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Shift shift = new Shift();
        shift.setStationId(dto.getStationId());
        shift.setCashierUserId(dto.getCashierUserId());
        shift.setOpeningCash(dto.getOpeningCash());
        shift.setOpenedBy(dto.getOpenedBy());
        shift.setNotes(dto.getNotes());
        return shift;
    }

    public ShiftResponseDTO toResponse(Shift shift) {
        if (shift == null) {
            return null;
        }

        ShiftResponseDTO dto = new ShiftResponseDTO();
        dto.setId(shift.getId());
        dto.setStationId(shift.getStationId());
        dto.setCashierUserId(shift.getCashierUserId());
        dto.setOpenedAt(shift.getOpenedAt());
        dto.setClosedAt(shift.getClosedAt());
        dto.setOpeningCash(shift.getOpeningCash());
        dto.setStatus(shift.getStatus());
        dto.setOpenedBy(shift.getOpenedBy());
        dto.setClosedBy(shift.getClosedBy());
        dto.setNotes(shift.getNotes());
        return dto;
    }
}
