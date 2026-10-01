package com.br.manager.domain.sale_receipt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record RefundSaleRequestDTO(
        @NotBlank(message = "Reason is required.") String reason,
        @NotNull(message = "Requested by is required.") UUID requestedBy,
        @NotNull(message = "Approved by is required.") UUID approvedBy,
        Instant refundedAt,
        String notes
) {
}
