package com.br.manager.domain.sale_receipt.fixture;

import com.br.manager.domain.sale_receipt.dto.SalePaymentInputDTO;
import com.br.manager.domain.sale_receipt.entity.SalePayment;
import com.br.manager.domain.sale_receipt.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Massa de dados padrao para pagamentos de venda.
 */
public final class SalePaymentFixture {

    public static final BigDecimal AMOUNT = new BigDecimal("100.0000");
    public static final String TRANSACTION_REFERENCE = "NSU-123456";
    public static final Instant AUTHORIZED_AT = Instant.parse("2026-10-30T12:05:00Z");
    public static final Instant RECEIVED_AT = Instant.parse("2026-10-30T12:06:00Z");

    private SalePaymentFixture() {
    }

    public static SalePaymentInputDTO input(UUID saleId, UUID paymentMethodId) {
        SalePaymentInputDTO input = new SalePaymentInputDTO();
        input.setSaleId(saleId);
        input.setPaymentMethodId(paymentMethodId);
        input.setAmount(AMOUNT);
        input.setStatus(PaymentStatus.RECEIVED);
        input.setTransactionReference(TRANSACTION_REFERENCE);
        input.setAuthorizedAt(AUTHORIZED_AT);
        input.setReceivedAt(RECEIVED_AT);
        return input;
    }

    public static SalePayment persistedEntity(UUID paymentMethodId, PaymentStatus status) {
        SalePayment payment = new SalePayment();
        payment.setId(UUID.randomUUID());
        payment.setPaymentMethodId(paymentMethodId);
        payment.setAmount(AMOUNT);
        payment.setStatus(status);
        payment.setTransactionReference(TRANSACTION_REFERENCE);
        payment.setAuthorizedAt(AUTHORIZED_AT);
        payment.setReceivedAt(RECEIVED_AT);
        return payment;
    }

    public static SalePayment persistedEntity(PaymentStatus status) {
        return persistedEntity(UUID.randomUUID(), status);
    }
}
