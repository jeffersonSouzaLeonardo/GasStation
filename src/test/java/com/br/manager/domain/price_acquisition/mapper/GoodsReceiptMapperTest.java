package com.br.manager.domain.price_acquisition.mapper;

import com.br.manager.domain.operational.entity.Supplier;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.price_acquisition.dto.GoodsReceiptInputDTO;
import com.br.manager.domain.price_acquisition.dto.GoodsReceiptItemResponseDTO;
import com.br.manager.domain.price_acquisition.dto.GoodsReceiptResponseDTO;
import com.br.manager.domain.price_acquisition.entity.GoodsReceipt;
import com.br.manager.domain.price_acquisition.entity.GoodsReceiptItem;
import com.br.manager.domain.price_acquisition.entity.PurchaseOrder;
import com.br.manager.domain.price_acquisition.fixture.GoodsReceiptFixture;
import com.br.manager.domain.price_acquisition.fixture.GoodsReceiptItemFixture;
import com.br.manager.domain.price_acquisition.fixture.PriceAcquisitionReferenceFixture;
import com.br.manager.domain.price_acquisition.fixture.PurchaseOrderFixture;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GoodsReceiptMapperTest {

    private final GoodsReceiptMapper mapper = new GoodsReceiptMapper();

    @Test
    void shouldMapInputToEntityWithReferences() {
        UUID supplierId = UUID.randomUUID();
        UUID receivedBy = UUID.randomUUID();
        UUID purchaseOrderId = UUID.randomUUID();
        GoodsReceiptInputDTO input = GoodsReceiptFixture.validInput(supplierId, receivedBy, List.of());
        input.setId(UUID.randomUUID());
        input.setPurchaseOrderId(purchaseOrderId);

        GoodsReceipt entity = mapper.goodsReceiptInputDTOToGoodsReceipt(input);

        assertThat(entity.getId()).isEqualTo(input.getId());
        assertThat(entity.getSupplier().getId()).isEqualTo(supplierId);
        assertThat(entity.getPurchaseOrder().getId()).isEqualTo(purchaseOrderId);
        assertThat(entity.getReceivedBy().getId()).isEqualTo(receivedBy);
        assertThat(entity.getInvoiceNumber()).isEqualTo(GoodsReceiptFixture.INVOICE_NUMBER);
        assertThat(entity.getInvoiceKey()).isEqualTo(GoodsReceiptFixture.INVOICE_KEY);
        assertThat(entity.getReceivedAt()).isEqualTo(GoodsReceiptFixture.RECEIVED_AT);
        assertThat(entity.getStatus()).isEqualTo(GoodsReceipt.Status.RECEIVED);
        assertThat(entity.getFreightAmount()).isEqualTo(GoodsReceiptFixture.FREIGHT_AMOUNT);
        assertThat(entity.getOtherCosts()).isEqualTo(GoodsReceiptFixture.OTHER_COSTS);
    }

    @Test
    void shouldNotSetReferencesWhenIdsAreNull() {
        GoodsReceiptInputDTO input = GoodsReceiptFixture.validInput(null, null, List.of());

        GoodsReceipt entity = mapper.goodsReceiptInputDTOToGoodsReceipt(input);

        assertThat(entity.getSupplier()).isNull();
        assertThat(entity.getPurchaseOrder()).isNull();
        assertThat(entity.getReceivedBy()).isNull();
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.goodsReceiptInputDTOToGoodsReceipt(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponseWithNestedItems() {
        Supplier supplier = PriceAcquisitionReferenceFixture.activeSupplier();
        User receivedBy = PriceAcquisitionReferenceFixture.activeUser();
        PurchaseOrder purchaseOrder = PurchaseOrderFixture.persistedEntity(PurchaseOrder.Status.SENT);
        GoodsReceipt goodsReceipt = GoodsReceiptFixture.persistedEntity(supplier, receivedBy);
        goodsReceipt.setPurchaseOrder(purchaseOrder);
        GoodsReceiptItem item = GoodsReceiptItemFixture.persistedEntity(null);
        goodsReceipt.setItems(List.of(item));

        GoodsReceiptResponseDTO response = mapper.goodsReceiptToGoodsReceiptResponseDTO(goodsReceipt);

        assertThat(response.getId()).isEqualTo(goodsReceipt.getId());
        assertThat(response.getSupplierId()).isEqualTo(supplier.getId());
        assertThat(response.getPurchaseOrderId()).isEqualTo(purchaseOrder.getId());
        assertThat(response.getReceivedBy()).isEqualTo(receivedBy.getId());
        assertThat(response.getInvoiceNumber()).isEqualTo(GoodsReceiptFixture.INVOICE_NUMBER);
        assertThat(response.getInvoiceKey()).isEqualTo(GoodsReceiptFixture.INVOICE_KEY);
        assertThat(response.getReceivedAt()).isEqualTo(GoodsReceiptFixture.RECEIVED_AT);
        assertThat(response.getStatus()).isEqualTo(GoodsReceipt.Status.RECEIVED);
        assertThat(response.getFreightAmount()).isEqualTo(GoodsReceiptFixture.FREIGHT_AMOUNT);
        assertThat(response.getOtherCosts()).isEqualTo(GoodsReceiptFixture.OTHER_COSTS);
        assertThat(response.getItems()).singleElement()
                .usingRecursiveComparison()
                .isEqualTo(expectedItemResponse(item, goodsReceipt.getId()));
    }

    @Test
    void shouldMapEntityWithoutRelationsToResponseWithNullIds() {
        GoodsReceipt goodsReceipt = new GoodsReceipt();
        goodsReceipt.setItems(null);

        GoodsReceiptResponseDTO response = mapper.goodsReceiptToGoodsReceiptResponseDTO(goodsReceipt);

        assertThat(response.getSupplierId()).isNull();
        assertThat(response.getPurchaseOrderId()).isNull();
        assertThat(response.getReceivedBy()).isNull();
        assertThat(response.getItems()).isEmpty();
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.goodsReceiptToGoodsReceiptResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListIgnoringNullElements() {
        GoodsReceipt first = GoodsReceiptFixture.persistedEntity();
        GoodsReceipt second = GoodsReceiptFixture.persistedEntity();

        assertThat(mapper.listGoodsReceiptToListGoodsReceiptResponseDTO(Arrays.asList(first, null, second)))
                .extracting(GoodsReceiptResponseDTO::getId)
                .containsExactly(first.getId(), second.getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listGoodsReceiptToListGoodsReceiptResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityWithProvidedFieldsKeepingRelations() {
        GoodsReceipt goodsReceipt = GoodsReceiptFixture.persistedEntity();
        Supplier supplier = goodsReceipt.getSupplier();
        UUID originalId = goodsReceipt.getId();
        GoodsReceiptInputDTO input = GoodsReceiptFixture.validInput(UUID.randomUUID(), UUID.randomUUID(), null);
        input.setId(UUID.randomUUID());
        input.setInvoiceNumber(GoodsReceiptFixture.UPDATED_INVOICE_NUMBER);
        input.setStatus(GoodsReceipt.Status.CANCELED);
        input.setReceivedAt(GoodsReceiptFixture.RECEIVED_AT.plusDays(1));
        input.setFreightAmount(BigDecimal.TEN);
        input.setOtherCosts(BigDecimal.ONE);

        mapper.updateGoodsReceiptFromDto(input, goodsReceipt);

        assertThat(goodsReceipt.getId()).isEqualTo(originalId);
        assertThat(goodsReceipt.getSupplier()).isSameAs(supplier);
        assertThat(goodsReceipt.getInvoiceNumber()).isEqualTo(GoodsReceiptFixture.UPDATED_INVOICE_NUMBER);
        assertThat(goodsReceipt.getStatus()).isEqualTo(GoodsReceipt.Status.CANCELED);
        assertThat(goodsReceipt.getReceivedAt()).isEqualTo(GoodsReceiptFixture.RECEIVED_AT.plusDays(1));
        assertThat(goodsReceipt.getFreightAmount()).isEqualTo(BigDecimal.TEN);
        assertThat(goodsReceipt.getOtherCosts()).isEqualTo(BigDecimal.ONE);
    }

    @Test
    void shouldKeepEntityValuesWhenInputFieldsAreNull() {
        GoodsReceipt goodsReceipt = GoodsReceiptFixture.persistedEntity();

        mapper.updateGoodsReceiptFromDto(new GoodsReceiptInputDTO(), goodsReceipt);

        assertThat(goodsReceipt.getInvoiceNumber()).isEqualTo(GoodsReceiptFixture.INVOICE_NUMBER);
        assertThat(goodsReceipt.getInvoiceKey()).isEqualTo(GoodsReceiptFixture.INVOICE_KEY);
        assertThat(goodsReceipt.getReceivedAt()).isEqualTo(GoodsReceiptFixture.RECEIVED_AT);
        assertThat(goodsReceipt.getStatus()).isEqualTo(GoodsReceipt.Status.RECEIVED);
        assertThat(goodsReceipt.getFreightAmount()).isEqualTo(GoodsReceiptFixture.FREIGHT_AMOUNT);
        assertThat(goodsReceipt.getOtherCosts()).isEqualTo(GoodsReceiptFixture.OTHER_COSTS);
    }

    @Test
    void shouldIgnoreUpdateWhenInputOrEntityIsNull() {
        GoodsReceipt goodsReceipt = GoodsReceiptFixture.persistedEntity();

        mapper.updateGoodsReceiptFromDto(null, goodsReceipt);
        mapper.updateGoodsReceiptFromDto(GoodsReceiptFixture.validInput(null, null, null), null);

        assertThat(goodsReceipt.getInvoiceNumber()).isEqualTo(GoodsReceiptFixture.INVOICE_NUMBER);
    }

    private GoodsReceiptItemResponseDTO expectedItemResponse(GoodsReceiptItem item, UUID goodsReceiptId) {
        GoodsReceiptItemResponseDTO expected = new GoodsReceiptItemResponseDTO();
        expected.setId(item.getId());
        expected.setGoodsReceiptId(goodsReceiptId);
        expected.setProductId(item.getProductId());
        expected.setTankId(item.getTankId());
        expected.setQuantity(GoodsReceiptItemFixture.QUANTITY);
        expected.setUnitCost(GoodsReceiptItemFixture.UNIT_COST);
        return expected;
    }
}
