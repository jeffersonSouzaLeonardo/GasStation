package com.br.manager.domain.sale_receipt.fixture;

import com.br.manager.domain.sale_receipt.dto.SaleInputDTO;
import com.br.manager.domain.sale_receipt.dto.SaleItemInputDTO;
import com.br.manager.domain.sale_receipt.entity.Sale;
import com.br.manager.domain.sale_receipt.enums.FiscalStatus;
import com.br.manager.domain.sale_receipt.enums.SaleStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Massa de dados padrao para vendas.
 */
public final class SaleFixture {

    public static final String SALE_NUMBER = "VEN-0001";
    public static final String OTHER_SALE_NUMBER = "VEN-0002";
    public static final Instant SOLD_AT = Instant.parse("2026-10-30T12:00:00Z");
    public static final BigDecimal SUBTOTAL = new BigDecimal("100.0000");
    public static final BigDecimal DISCOUNT = new BigDecimal("2.5000");
    public static final String NOTES = "Venda no balcao";
    public static final String UPDATED_NOTES = "Venda revisada pelo caixa";
    public static final FiscalStatus FISCAL_STATUS = FiscalStatus.PENDING_EMISSION;

    private SaleFixture() {
    }

    public static SaleInputDTO input(UUID stationId, UUID shiftId, UUID cashierUserId, List<SaleItemInputDTO> items) {
        SaleInputDTO input = new SaleInputDTO();
        input.setStationId(stationId);
        input.setShiftId(shiftId);
        input.setCashierUserId(cashierUserId);
        input.setSaleNumber(SALE_NUMBER);
        input.setSoldAt(SOLD_AT);
        input.setDiscountAmount(DISCOUNT);
        input.setFiscalStatus(FISCAL_STATUS);
        input.setNotes(NOTES);
        input.setItems(items);
        return input;
    }

    public static SaleInputDTO updateInput(UUID saleId) {
        SaleInputDTO input = new SaleInputDTO();
        input.setId(saleId);
        input.setNotes(UPDATED_NOTES);
        return input;
    }

    public static Sale entity(UUID stationId, UUID shiftId, UUID cashierUserId, SaleStatus status) {
        Sale sale = new Sale();
        sale.setId(UUID.randomUUID());
        sale.setStationId(stationId);
        sale.setShiftId(shiftId);
        sale.setCashierUserId(cashierUserId);
        sale.setSaleNumber(SALE_NUMBER);
        sale.setStatus(status);
        sale.setSoldAt(SOLD_AT);
        sale.setSubtotal(SUBTOTAL);
        sale.setDiscountAmount(DISCOUNT);
        sale.setFiscalStatus(FISCAL_STATUS);
        sale.setNotes(NOTES);
        return sale;
    }

    public static Sale persistedEntity(SaleStatus status) {
        return entity(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), status);
    }
}
