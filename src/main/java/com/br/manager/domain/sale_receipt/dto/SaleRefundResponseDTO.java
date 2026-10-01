package com.br.manager.domain.sale_receipt.dto;

import java.time.Instant;
import java.util.UUID;

public record SaleRefundResponseDTO(
        UUID id,
        String reason,
        UUID requestedBy,
        UUID approvedBy,
        Instant refundedAt,
        String notes
) {
}
