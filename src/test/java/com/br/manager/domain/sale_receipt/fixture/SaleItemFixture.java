package com.br.manager.domain.sale_receipt.fixture;

import com.br.manager.domain.sale_receipt.dto.SaleItemInputDTO;
import com.br.manager.domain.sale_receipt.entity.SaleItem;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Massa de dados padrao para itens de venda.
 */
public final class SaleItemFixture {

    public static final int LINE_NUMBER = 1;
    public static final int SECOND_LINE_NUMBER = 2;
    public static final BigDecimal QUANTITY = new BigDecimal("2.0000");
    public static final BigDecimal UNIT_PRICE = new BigDecimal("5.9900");
    public static final BigDecimal DISCOUNT = new BigDecimal("0.5000");
    public static final BigDecimal METER_START = new BigDecimal("1000.0000");
    public static final BigDecimal METER_END = new BigDecimal("1040.0000");
    public static final BigDecimal FUEL_QUANTITY = new BigDecimal("40.0000");

    private SaleItemFixture() {
    }

    public static SaleItemInputDTO productInput(UUID productId) {
        SaleItemInputDTO input = new SaleItemInputDTO();
        input.setLineNumber(LINE_NUMBER);
        input.setProductId(productId);
        input.setQuantity(QUANTITY);
        input.setUnitPrice(UNIT_PRICE);
        input.setDiscountAmount(DISCOUNT);
        return input;
    }

    public static SaleItemInputDTO productInput(UUID productId, int lineNumber) {
        SaleItemInputDTO input = productInput(productId);
        input.setLineNumber(lineNumber);
        return input;
    }

    public static SaleItemInputDTO fuelInput(UUID productId, UUID nozzleId) {
        SaleItemInputDTO input = productInput(productId);
        input.setNozzleId(nozzleId);
        input.setQuantity(null);
        input.setMeterStart(METER_START);
        input.setMeterEnd(METER_END);
        return input;
    }

    public static SaleItem persistedEntity(UUID productId) {
        SaleItem item = new SaleItem();
        item.setId(UUID.randomUUID());
        item.setLineNumber(LINE_NUMBER);
        item.setProductId(productId);
        item.setQuantity(QUANTITY);
        item.setUnitPrice(UNIT_PRICE);
        item.setDiscountAmount(DISCOUNT);
        return item;
    }

    public static SaleItem persistedFuelEntity() {
        SaleItem item = persistedEntity(UUID.randomUUID());
        item.setNozzleId(UUID.randomUUID());
        item.setTankId(UUID.randomUUID());
        item.setQuantity(FUEL_QUANTITY);
        item.setMeterStart(METER_START);
        item.setMeterEnd(METER_END);
        return item;
    }
}
