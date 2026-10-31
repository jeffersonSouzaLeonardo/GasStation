package com.br.manager.domain.shift_cachs.fixture;

import com.br.manager.domain.shift_cachs.dto.PaymentClosingInputDTO;
import com.br.manager.domain.shift_cachs.entity.PaymentClosing;
import com.br.manager.domain.shift_cachs.entity.Shift;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Fechamento por Forma de Pagamento do turno.
 */
public final class PaymentClosingFixture {

    public static final BigDecimal EXPECTED_AMOUNT = new BigDecimal("500.00");
    public static final BigDecimal DECLARED_AMOUNT = new BigDecimal("480.00");
    public static final BigDecimal DIFFERENCE_AMOUNT = new BigDecimal("-20.00");
    public static final BigDecimal NEGATIVE_AMOUNT = new BigDecimal("-1.00");
    public static final String EVIDENCE_REFERENCE = "COMPROVANTE-123";

    private PaymentClosingFixture() {
    }

    public static PaymentClosingInputDTO validInput(UUID paymentMethodId) {
        PaymentClosingInputDTO input = new PaymentClosingInputDTO();
        input.setPaymentMethodId(paymentMethodId);
        input.setExpectedAmount(EXPECTED_AMOUNT);
        input.setDeclaredAmount(DECLARED_AMOUNT);
        input.setEvidenceReference(EVIDENCE_REFERENCE);
        return input;
    }

    public static PaymentClosing persistedEntity(Shift shift, UUID paymentMethodId) {
        PaymentClosing closing = new PaymentClosing();
        closing.setId(UUID.randomUUID());
        closing.setShift(shift);
        closing.setPaymentMethodId(paymentMethodId);
        closing.setExpectedAmount(EXPECTED_AMOUNT);
        closing.setDeclaredAmount(DECLARED_AMOUNT);
        closing.setDifferenceAmount(DIFFERENCE_AMOUNT);
        closing.setEvidenceReference(EVIDENCE_REFERENCE);
        return closing;
    }
}
