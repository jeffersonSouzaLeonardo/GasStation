package com.br.manager.domain.shift_cachs.mapper;

import com.br.manager.domain.shift_cachs.dto.ShiftOpenRequestDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftResponseDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftSyncDTO;
import com.br.manager.domain.shift_cachs.entity.Shift;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

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

    /**
     * Turno recebido do PDV. Como o schema exige cashier_user_id/opening_cash,
     * usa opened_by como caixa responsável e zero como fundo de troco padrão.
     */
    public Shift toEntity(ShiftSyncDTO dto) {
        if (dto == null) {
            return null;
        }

        Shift shift = new Shift();
        shift.setId(dto.getId());
        shift.setStationId(dto.getStationId());
        shift.setCashierUserId(dto.getOpenedBy());
        shift.setName(dto.getName());
        shift.setOpenedAt(dto.getOpenedAt());
        shift.setClosedAt(dto.getClosedAt());
        shift.setOpeningCash(BigDecimal.ZERO);
        shift.setStatus(dto.getStatus());
        shift.setOpenedBy(dto.getOpenedBy());
        shift.setClosedBy(dto.getClosedBy());
        shift.setNotes(dto.getNotes());
        return shift;
    }

    public void updateFromSync(ShiftSyncDTO dto, Shift shift) {
        if (dto == null || shift == null) {
            return;
        }

        shift.setStationId(dto.getStationId());
        shift.setName(dto.getName());
        shift.setOpenedAt(dto.getOpenedAt());
        shift.setClosedAt(dto.getClosedAt());
        shift.setStatus(dto.getStatus());
        shift.setOpenedBy(dto.getOpenedBy());
        shift.setClosedBy(dto.getClosedBy());
        shift.setNotes(dto.getNotes());
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
