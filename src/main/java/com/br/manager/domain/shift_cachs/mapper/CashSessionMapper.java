package com.br.manager.domain.shift_cachs.mapper;

import com.br.manager.domain.shift_cachs.dto.CashSessionInputDTO;
import com.br.manager.domain.shift_cachs.dto.CashSessionResponseDTO;
import com.br.manager.domain.shift_cachs.dto.CashSessionSyncDTO;
import com.br.manager.domain.shift_cachs.entity.CashSession;
import com.br.manager.domain.shift_cachs.entity.Shift;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CashSessionMapper {

    public CashSession toEntity(CashSessionInputDTO dto, Shift shift) {
        if (dto == null) {
            return null;
        }

        CashSession session = new CashSession();
        session.setShift(shift);
        session.setCashierUserId(dto.getCashierUserId());
        session.setRole(dto.getRole());
        session.setOpeningCash(dto.getOpeningCash());
        session.setNotes(dto.getNotes());
        return session;
    }

    public CashSession toEntity(CashSessionSyncDTO dto, Shift shift) {
        if (dto == null) {
            return null;
        }

        CashSession session = new CashSession();
        session.setId(dto.getId());
        session.setShift(shift);
        session.setCashierUserId(dto.getCashierUserId());
        session.setRole(dto.getRole());
        session.setOpeningCash(dto.getOpeningCash());
        session.setStatus(dto.getStatus());
        session.setOpenedAt(dto.getOpenedAt());
        session.setClosedAt(dto.getClosedAt());
        session.setTotalCash(dto.getTotalCash());
        session.setTotalCard(dto.getTotalCard());
        session.setTotalPix(dto.getTotalPix());
        session.setTotalWithdrawal(dto.getTotalWithdrawal());
        session.setNotes(dto.getNotes());
        return session;
    }

    public void updateFromSync(CashSessionSyncDTO dto, CashSession session) {
        if (dto == null || session == null) {
            return;
        }

        session.setCashierUserId(dto.getCashierUserId());
        session.setRole(dto.getRole());
        session.setOpeningCash(dto.getOpeningCash());
        session.setStatus(dto.getStatus());
        session.setOpenedAt(dto.getOpenedAt());
        session.setClosedAt(dto.getClosedAt());
        session.setTotalCash(dto.getTotalCash());
        session.setTotalCard(dto.getTotalCard());
        session.setTotalPix(dto.getTotalPix());
        session.setTotalWithdrawal(dto.getTotalWithdrawal());
        session.setNotes(dto.getNotes());
    }

    public CashSessionResponseDTO toResponse(CashSession session) {
        if (session == null) {
            return null;
        }

        CashSessionResponseDTO dto = new CashSessionResponseDTO();
        dto.setId(session.getId());
        dto.setShiftId(session.getShift() != null ? session.getShift().getId() : null);
        dto.setCashierUserId(session.getCashierUserId());
        dto.setRole(session.getRole());
        dto.setOpeningCash(session.getOpeningCash());
        dto.setStatus(session.getStatus());
        dto.setOpenedAt(session.getOpenedAt());
        dto.setClosedAt(session.getClosedAt());
        dto.setTotalCash(session.getTotalCash());
        dto.setTotalCard(session.getTotalCard());
        dto.setTotalPix(session.getTotalPix());
        dto.setTotalWithdrawal(session.getTotalWithdrawal());
        dto.setNotes(session.getNotes());
        dto.setCreatedAt(session.getCreatedAt());
        return dto;
    }

    public List<CashSessionResponseDTO> toResponseList(List<CashSession> sessions) {
        if (sessions == null) {
            return List.of();
        }
        return sessions.stream().map(this::toResponse).toList();
    }
}
