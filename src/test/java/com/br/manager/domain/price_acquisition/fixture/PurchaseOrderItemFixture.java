package com.br.manager.domain.price_acquisition.fixture;

import com.br.manager.domain.price_acquisition.dto.PurchaseOrderItemInputDTO;
import com.br.manager.domain.price_acquisition.entity.PurchaseOrder;
import com.br.manager.domain.price_acquisition.entity.PurchaseOrderItem;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Item de Pedido de Compra.
 */
public final class PurchaseOrderItemFixture {

    public static final int QUANTITY = 1000;
    public static final int UPDATED_QUANTITY = 2000;
    public static final BigDecimal UNIT_COST = new BigDecimal("5.2500");
    public static final BigDecimal TOTAL_COST = new BigDecimal("5250.0000");
    public static final BigDecimal UPDATED_TOTAL_COST = new BigDecimal("10500.0000");

    private PurchaseOrderItemFixture() {
    }

    public static PurchaseOrderItemInputDTO validInput(UUID purchaseOrderId, UUID productId) {
        PurchaseOrderItemInputDTO input = new PurchaseOrderItemInputDTO();
        input.setPurchaseOrderId(purchaseOrderId);
        input.setProductId(productId);
        input.setQuantity(QUANTITY);
        input.setUnitCost(UNIT_COST);
        return input;
    }

    public static PurchaseOrderItem persistedEntity(PurchaseOrder purchaseOrder) {
        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setId(UUID.randomUUID());
        item.setPurchaseOrder(purchaseOrder);
        item.setProductId(UUID.randomUUID());
        item.setQuantity(QUANTITY);
        item.setUnitCost(UNIT_COST);
        return item;
    }
}
