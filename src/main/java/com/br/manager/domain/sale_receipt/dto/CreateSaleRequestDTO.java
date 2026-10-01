package com.br.manager.domain.sale_receipt.dto;

import com.br.manager.domain.sale_receipt.enums.FiscalStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CreateSaleRequestDTO(
        @NotNull(message = "Station ID is required.") UUID stationId,
        @NotNull(message = "Shift ID is required.") UUID shiftId,
        @NotBlank(message = "Sale number is required.") String saleNumber,
        @NotNull(message = "Sold at is required.") Instant soldAt,
        UUID customerId,
        UUID vehicleId,
        UUID attendantUserId,
        @NotNull(message = "Cashier user ID is required.") UUID cashierUserId,
        BigDecimal discountAmount,
        FiscalStatus fiscalStatus,
        String notes,
        @Valid @NotEmpty(message = "Sale must contain at least one item.") List<CreateSaleItemDTO> items
) {
}
