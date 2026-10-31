package com.br.manager.domain.price_acquisition.fixture;

import com.br.manager.domain.price_acquisition.dto.GoodsReceiptItemInputDTO;
import com.br.manager.domain.price_acquisition.entity.GoodsReceipt;
import com.br.manager.domain.price_acquisition.entity.GoodsReceiptItem;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Item de Recebimento de Mercadorias.
 */
public final class GoodsReceiptItemFixture {

    public static final BigDecimal QUANTITY = new BigDecimal("5000.0000");
    public static final BigDecimal UPDATED_QUANTITY = new BigDecimal("4500.5000");
    public static final BigDecimal UNIT_COST = new BigDecimal("5.1000");

    private GoodsReceiptItemFixture() {
    }

    public static GoodsReceiptItemInputDTO validInput(UUID goodsReceiptId, UUID productId, UUID tankId) {
        GoodsReceiptItemInputDTO input = new GoodsReceiptItemInputDTO();
        input.setGoodsReceiptId(goodsReceiptId);
        input.setProductId(productId);
        input.setTankId(tankId);
        input.setQuantity(QUANTITY);
        input.setUnitCost(UNIT_COST);
        return input;
    }

    public static GoodsReceiptItem persistedEntity(GoodsReceipt goodsReceipt) {
        GoodsReceiptItem item = new GoodsReceiptItem();
        item.setId(UUID.randomUUID());
        item.setGoodsReceipt(goodsReceipt);
        item.setProductId(UUID.randomUUID());
        item.setTankId(UUID.randomUUID());
        item.setQuantity(QUANTITY);
        item.setUnitCost(UNIT_COST);
        return item;
    }
}
