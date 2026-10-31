package com.br.manager.domain.shift_cachs.fixture;

import com.br.manager.domain.shift_cachs.dto.ShiftCloseRequestDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftOpenRequestDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftSyncDTO;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Turno.
 */
public final class ShiftFixture {

    public static final BigDecimal OPENING_CASH = new BigDecimal("150.00");
    public static final String NOTES = "Turno da manha";
    public static final String NAME = "Turno 1";
    public static final String SYNC_NOTES = "Turno recebido do PDV";
    public static final LocalDateTime OPENED_AT = LocalDateTime.of(2026, 10, 30, 6, 0, 0);
    public static final LocalDateTime CLOSED_AT = LocalDateTime.of(2026, 10, 30, 14, 0, 0);

    private ShiftFixture() {
    }

    public static ShiftOpenRequestDTO openRequest(UUID stationId, UUID cashierUserId, UUID openedBy) {
        ShiftOpenRequestDTO request = new ShiftOpenRequestDTO();
        request.setStationId(stationId);
        request.setCashierUserId(cashierUserId);
        request.setOpenedBy(openedBy);
        request.setOpeningCash(OPENING_CASH);
        request.setNotes(NOTES);
        return request;
    }

    public static ShiftOpenRequestDTO openRequest() {
        return openRequest(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    }

    public static ShiftCloseRequestDTO closeRequest(UUID closedBy) {
        ShiftCloseRequestDTO request = new ShiftCloseRequestDTO();
        request.setClosedBy(closedBy);
        return request;
    }

    public static ShiftSyncDTO openSync(UUID id, UUID stationId, UUID openedBy) {
        ShiftSyncDTO dto = new ShiftSyncDTO();
        dto.setId(id);
        dto.setStationId(stationId);
        dto.setName(NAME);
        dto.setOpenedAt(OPENED_AT);
        dto.setStatus(ShiftStatusEnum.OPEN);
        dto.setOpenedBy(openedBy);
        dto.setNotes(SYNC_NOTES);
        return dto;
    }

    public static ShiftSyncDTO closedSync(UUID id, UUID stationId, UUID userId) {
        ShiftSyncDTO dto = openSync(id, stationId, userId);
        dto.setStatus(ShiftStatusEnum.CLOSED);
        dto.setClosedAt(CLOSED_AT);
        dto.setClosedBy(userId);
        return dto;
    }

    public static ShiftSyncDTO openSync() {
        return openSync(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    }

    public static Shift openShift() {
        Shift shift = new Shift();
        shift.setId(UUID.randomUUID());
        shift.setStationId(UUID.randomUUID());
        shift.setCashierUserId(UUID.randomUUID());
        shift.setOpenedBy(UUID.randomUUID());
        shift.setOpenedAt(OPENED_AT);
        shift.setOpeningCash(OPENING_CASH);
        shift.setStatus(ShiftStatusEnum.OPEN);
        shift.setName(NAME);
        shift.setNotes(NOTES);
        return shift;
    }

    public static Shift closedShift() {
        Shift shift = openShift();
        shift.setStatus(ShiftStatusEnum.CLOSED);
        shift.setClosedAt(CLOSED_AT);
        shift.setClosedBy(shift.getOpenedBy());
        return shift;
    }
}
