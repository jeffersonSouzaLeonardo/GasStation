package com.br.manager.domain.price_acquisition.fixture;

import com.br.manager.domain.price_acquisition.dto.PriceItemInputDTO;
import com.br.manager.domain.price_acquisition.entity.PriceItem;
import com.br.manager.domain.price_acquisition.entity.PriceTable;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Item de Preco.
 */
public final class PriceItemFixture {

    public static final BigDecimal UNIT_PRICE = new BigDecimal("5.8900");
    public static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal("6.1900");
    public static final BigDecimal MIN_PRICE = new BigDecimal("5.5000");
    public static final BigDecimal MAX_DISCOUNT_PERCENT = new BigDecimal("3.00");

    private PriceItemFixture() {
    }

    public static PriceItemInputDTO validInput(UUID priceTableId, UUID productId) {
        PriceItemInputDTO input = new PriceItemInputDTO();
        input.setPriceTableId(priceTableId);
        input.setProductId(productId);
        input.setUnitPrice(UNIT_PRICE);
        input.setMinPrice(MIN_PRICE);
        input.setMaxDiscountPercent(MAX_DISCOUNT_PERCENT);
        input.setActive(true);
        return input;
    }

    public static PriceItem persistedEntity(PriceTable priceTable) {
        PriceItem priceItem = new PriceItem();
        priceItem.setId(UUID.randomUUID());
        priceItem.setPriceTable(priceTable);
        priceItem.setProductId(UUID.randomUUID());
        priceItem.setNozzleId(UUID.randomUUID());
        priceItem.setUnitPrice(UNIT_PRICE);
        priceItem.setMinPrice(MIN_PRICE);
        priceItem.setMaxDiscountPercent(MAX_DISCOUNT_PERCENT);
        priceItem.setActive(true);
        return priceItem;
    }
}
