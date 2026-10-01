package com.br.manager.domain.sale_receipt.dto;

import com.br.manager.domain.sale_receipt.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SalePaymentResponseDTO(
        UUID id,
        UUID paymentMethodId,
        BigDecimal amount,
        PaymentStatus status,
        String transactionReference,
        Instant authorizedAt,
        Instant receivedAt
) {
}
