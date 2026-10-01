package com.br.manager.domain.sale_receipt.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record SaleItemResponseDTO(
        UUID id,
        Integer lineNumber,
        UUID productId,
        UUID nozzleId,
        UUID tankId,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal discountAmount,
        BigDecimal meterStart,
        BigDecimal meterEnd
) {
}
