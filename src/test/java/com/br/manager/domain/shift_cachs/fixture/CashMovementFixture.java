package com.br.manager.domain.shift_cachs.fixture;

import com.br.manager.domain.shift_cachs.dto.CashMovementInputDTO;
import com.br.manager.domain.shift_cachs.entity.CashMovement;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.CashMovementTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Movimentacao de Caixa do turno.
 */
public final class CashMovementFixture {

    public static final CashMovementTypeEnum MOVEMENT_TYPE = CashMovementTypeEnum.WITHDRAWAL;
    public static final BigDecimal AMOUNT = new BigDecimal("150.00");
    public static final BigDecimal NEGATIVE_AMOUNT = new BigDecimal("-0.01");
    public static final LocalDateTime OCCURRED_AT = LocalDateTime.of(2026, 10, 31, 10, 30);
    public static final String REFERENCE = "SANGRIA-001";
    public static final String REASON = "Sangria para o cofre";

    private CashMovementFixture() {
    }

    public static CashMovementInputDTO validInput(UUID createdBy) {
        CashMovementInputDTO input = new CashMovementInputDTO();
        input.setMovementType(MOVEMENT_TYPE);
        input.setAmount(AMOUNT);
        input.setOccurredAt(OCCURRED_AT);
        input.setReference(REFERENCE);
        input.setReason(REASON);
        input.setCreatedBy(createdBy);
        return input;
    }

    public static CashMovementInputDTO completeInput(UUID createdBy, UUID approvedBy, UUID paymentMethodId) {
        CashMovementInputDTO input = validInput(createdBy);
        input.setApprovedBy(approvedBy);
        input.setPaymentMethodId(paymentMethodId);
        return input;
    }

    public static CashMovement persistedEntity(Shift shift) {
        CashMovement movement = new CashMovement();
        movement.setId(UUID.randomUUID());
        movement.setShift(shift);
        movement.setMovementType(MOVEMENT_TYPE);
        movement.setPaymentMethodId(UUID.randomUUID());
        movement.setAmount(AMOUNT);
        movement.setOccurredAt(OCCURRED_AT);
        movement.setReference(REFERENCE);
        movement.setReason(REASON);
        movement.setCreatedBy(UUID.randomUUID());
        movement.setApprovedBy(UUID.randomUUID());
        return movement;
    }
}
