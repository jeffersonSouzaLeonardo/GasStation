package com.br.manager.domain.sale_receipt.dto;

import com.br.manager.domain.sale_receipt.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AddSalePaymentRequestDTO(
        @NotNull(message = "Payment method ID is required.") UUID paymentMethodId,
        @NotNull(message = "Amount is required.") BigDecimal amount,
        @NotNull(message = "Status is required.") PaymentStatus status,
        String transactionReference,
        Instant authorizedAt,
        Instant receivedAt
) {
}
