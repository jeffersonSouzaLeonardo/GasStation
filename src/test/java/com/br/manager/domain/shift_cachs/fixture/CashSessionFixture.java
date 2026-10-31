package com.br.manager.domain.shift_cachs.fixture;

import com.br.manager.domain.shift_cachs.dto.CashSessionCloseInputDTO;
import com.br.manager.domain.shift_cachs.dto.CashSessionInputDTO;
import com.br.manager.domain.shift_cachs.dto.CashSessionSyncDTO;
import com.br.manager.domain.shift_cachs.entity.CashSession;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.CashSessionStatusEnum;
import com.br.manager.domain.shift_cachs.enums.CashierRoleEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Sessao de Caixa.
 */
public final class CashSessionFixture {

    public static final CashierRoleEnum ROLE = CashierRoleEnum.CASHIER;
    public static final BigDecimal OPENING_CASH = new BigDecimal("100.00");
    public static final String NOTES = "Caixa principal da pista";
    public static final BigDecimal TOTAL_CASH = new BigDecimal("350.00");
    public static final BigDecimal TOTAL_CARD = new BigDecimal("420.50");
    public static final BigDecimal TOTAL_PIX = new BigDecimal("130.25");
    public static final BigDecimal TOTAL_WITHDRAWAL = new BigDecimal("50.00");
    public static final String CLOSING_NOTES = "Fechamento sem divergencias";
    public static final String SYNC_NOTES = "Sessao recebida do PDV";
    public static final LocalDateTime OPENED_AT = LocalDateTime.of(2026, 10, 31, 6, 0);
    public static final LocalDateTime CLOSED_AT = LocalDateTime.of(2026, 10, 31, 14, 0);
    public static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 10, 31, 6, 1);

    private CashSessionFixture() {
    }

    public static CashSessionInputDTO validInput(UUID shiftId, UUID cashierUserId) {
        CashSessionInputDTO input = new CashSessionInputDTO();
        input.setShiftId(shiftId);
        input.setCashierUserId(cashierUserId);
        input.setRole(ROLE);
        input.setOpeningCash(OPENING_CASH);
        input.setNotes(NOTES);
        return input;
    }

    public static CashSessionCloseInputDTO closeInput() {
        CashSessionCloseInputDTO input = new CashSessionCloseInputDTO();
        input.setTotalCash(TOTAL_CASH);
        input.setTotalCard(TOTAL_CARD);
        input.setTotalPix(TOTAL_PIX);
        input.setTotalWithdrawal(TOTAL_WITHDRAWAL);
        input.setNotes(CLOSING_NOTES);
        return input;
    }

    public static CashSessionSyncDTO closedSyncInput(UUID id, UUID shiftId, UUID cashierUserId) {
        CashSessionSyncDTO input = new CashSessionSyncDTO();
        input.setId(id);
        input.setShiftId(shiftId);
        input.setCashierUserId(cashierUserId);
        input.setRole(CashierRoleEnum.PUMP_ATTENDANT);
        input.setOpeningCash(OPENING_CASH);
        input.setStatus(CashSessionStatusEnum.CLOSED);
        input.setOpenedAt(OPENED_AT);
        input.setClosedAt(CLOSED_AT);
        input.setTotalCash(TOTAL_CASH);
        input.setTotalCard(TOTAL_CARD);
        input.setTotalPix(TOTAL_PIX);
        input.setTotalWithdrawal(TOTAL_WITHDRAWAL);
        input.setNotes(SYNC_NOTES);
        return input;
    }

    public static CashSession openEntity(Shift shift) {
        CashSession session = new CashSession();
        session.setId(UUID.randomUUID());
        session.setShift(shift);
        session.setCashierUserId(UUID.randomUUID());
        session.setRole(ROLE);
        session.setOpeningCash(OPENING_CASH);
        session.setStatus(CashSessionStatusEnum.OPEN);
        session.setOpenedAt(OPENED_AT);
        session.setNotes(NOTES);
        session.setCreatedAt(CREATED_AT);
        return session;
    }

    public static CashSession closedEntity(Shift shift) {
        CashSession session = openEntity(shift);
        session.setStatus(CashSessionStatusEnum.CLOSED);
        session.setClosedAt(CLOSED_AT);
        session.setTotalCash(TOTAL_CASH);
        session.setTotalCard(TOTAL_CARD);
        session.setTotalPix(TOTAL_PIX);
        session.setTotalWithdrawal(TOTAL_WITHDRAWAL);
        return session;
    }
}
