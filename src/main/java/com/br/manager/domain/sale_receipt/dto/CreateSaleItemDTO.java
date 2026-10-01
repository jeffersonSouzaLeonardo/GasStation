package com.br.manager.domain.sale_receipt.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateSaleItemDTO(
        @NotNull(message = "Line number is required.") Integer lineNumber,
        @NotNull(message = "Product ID is required.") UUID productId,
        UUID nozzleId,
        UUID tankId,
        @NotNull(message = "Quantity is required.") BigDecimal quantity,
        @NotNull(message = "Unit price is required.") BigDecimal unitPrice,
        BigDecimal discountAmount,
        BigDecimal meterStart,
        BigDecimal meterEnd
) {
}
