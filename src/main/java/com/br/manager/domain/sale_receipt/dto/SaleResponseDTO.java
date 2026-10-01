package com.br.manager.domain.sale_receipt.dto;

import com.br.manager.domain.sale_receipt.enums.FiscalStatus;
import com.br.manager.domain.sale_receipt.enums.SaleStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SaleResponseDTO(
        UUID id,
        UUID stationId,
        UUID shiftId,
        String saleNumber,
        SaleStatus status,
        Instant soldAt,
        UUID customerId,
        UUID vehicleId,
        UUID attendantUserId,
        UUID cashierUserId,
        BigDecimal subtotal,
        BigDecimal discountAmount,
        FiscalStatus fiscalStatus,
        String notes,
        List<SaleItemResponseDTO> items,
        List<SalePaymentResponseDTO> payments,
        List<SaleRefundResponseDTO> refunds
) {
}
