package com.br.manager.domain.price_acquisition.fixture;

import com.br.manager.domain.operational.entity.Supplier;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.price_acquisition.dto.GoodsReceiptInputDTO;
import com.br.manager.domain.price_acquisition.dto.GoodsReceiptItemInputDTO;
import com.br.manager.domain.price_acquisition.entity.GoodsReceipt;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Recebimento de Mercadorias.
 */
public final class GoodsReceiptFixture {

    public static final String INVOICE_NUMBER = "NF-000123";
    public static final String UPDATED_INVOICE_NUMBER = "NF-000999";
    public static final String INVOICE_KEY = "35260112345678000190550010000001231000001234";
    public static final LocalDateTime RECEIVED_AT = LocalDateTime.of(2026, 1, 10, 15, 45);
    public static final BigDecimal FREIGHT_AMOUNT = new BigDecimal("150.00");
    public static final BigDecimal OTHER_COSTS = new BigDecimal("25.00");

    private GoodsReceiptFixture() {
    }

    public static GoodsReceiptInputDTO validInput(UUID supplierId, UUID receivedBy, List<GoodsReceiptItemInputDTO> items) {
        GoodsReceiptInputDTO input = new GoodsReceiptInputDTO();
        input.setSupplierId(supplierId);
        input.setReceivedBy(receivedBy);
        input.setInvoiceNumber(INVOICE_NUMBER);
        input.setInvoiceKey(INVOICE_KEY);
        input.setReceivedAt(RECEIVED_AT);
        input.setStatus(GoodsReceipt.Status.RECEIVED);
        input.setFreightAmount(FREIGHT_AMOUNT);
        input.setOtherCosts(OTHER_COSTS);
        input.setItems(items);
        return input;
    }

    public static GoodsReceipt persistedEntity(Supplier supplier, User receivedBy) {
        GoodsReceipt goodsReceipt = new GoodsReceipt();
        goodsReceipt.setId(UUID.randomUUID());
        goodsReceipt.setSupplier(supplier);
        goodsReceipt.setReceivedBy(receivedBy);
        goodsReceipt.setInvoiceNumber(INVOICE_NUMBER);
        goodsReceipt.setInvoiceKey(INVOICE_KEY);
        goodsReceipt.setReceivedAt(RECEIVED_AT);
        goodsReceipt.setStatus(GoodsReceipt.Status.RECEIVED);
        goodsReceipt.setFreightAmount(FREIGHT_AMOUNT);
        goodsReceipt.setOtherCosts(OTHER_COSTS);
        return goodsReceipt;
    }

    public static GoodsReceipt persistedEntity() {
        return persistedEntity(PriceAcquisitionReferenceFixture.activeSupplier(), PriceAcquisitionReferenceFixture.activeUser());
    }
}
