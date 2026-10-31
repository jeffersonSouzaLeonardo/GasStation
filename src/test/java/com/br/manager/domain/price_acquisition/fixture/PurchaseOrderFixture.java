package com.br.manager.domain.price_acquisition.fixture;

import com.br.manager.domain.operational.entity.Supplier;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.price_acquisition.dto.PurchaseOrderInputDTO;
import com.br.manager.domain.price_acquisition.dto.PurchaseOrderItemInputDTO;
import com.br.manager.domain.price_acquisition.entity.PurchaseOrder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Pedido de Compra.
 */
public final class PurchaseOrderFixture {

    public static final String NUMBER = "PC-2026-0001";
    public static final String UPDATED_NUMBER = "PC-2026-0099";
    public static final LocalDateTime ORDERED_AT = LocalDateTime.of(2026, 1, 5, 9, 30);
    public static final LocalDateTime EXPECTED_AT = LocalDateTime.of(2026, 1, 10, 14, 0);

    private PurchaseOrderFixture() {
    }

    public static PurchaseOrderInputDTO validInput(UUID supplierId, UUID createdBy, List<PurchaseOrderItemInputDTO> items) {
        PurchaseOrderInputDTO input = new PurchaseOrderInputDTO();
        input.setSupplierId(supplierId);
        input.setCreatedBy(createdBy);
        input.setNumber(NUMBER);
        input.setOrderedAt(ORDERED_AT);
        input.setExpectedAt(EXPECTED_AT);
        input.setItems(items);
        return input;
    }

    public static PurchaseOrder persistedEntity(String status, Supplier supplier, User createdBy) {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(UUID.randomUUID());
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setCreatedBy(createdBy);
        purchaseOrder.setNumber(NUMBER);
        purchaseOrder.setStatus(status);
        purchaseOrder.setOrderedAt(ORDERED_AT);
        purchaseOrder.setExpectedAt(EXPECTED_AT);
        return purchaseOrder;
    }

    public static PurchaseOrder persistedEntity(String status) {
        return persistedEntity(status, PriceAcquisitionReferenceFixture.activeSupplier(), PriceAcquisitionReferenceFixture.activeUser());
    }
}
