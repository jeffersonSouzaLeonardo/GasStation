package com.br.manager.domain.price_acquisition.mapper;

import com.br.manager.domain.operational.entity.Supplier;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.price_acquisition.dto.PurchaseOrderInputDTO;
import com.br.manager.domain.price_acquisition.dto.PurchaseOrderItemResponseDTO;
import com.br.manager.domain.price_acquisition.dto.PurchaseOrderResponseDTO;
import com.br.manager.domain.price_acquisition.entity.PurchaseOrder;
import com.br.manager.domain.price_acquisition.entity.PurchaseOrderItem;
import com.br.manager.domain.price_acquisition.fixture.PriceAcquisitionReferenceFixture;
import com.br.manager.domain.price_acquisition.fixture.PurchaseOrderFixture;
import com.br.manager.domain.price_acquisition.fixture.PurchaseOrderItemFixture;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PurchaseOrderMapperTest {

    private final PurchaseOrderMapper mapper = new PurchaseOrderMapper();

    @Test
    void shouldMapInputToEntityWithCreatorReferenceAndEmptyItems() {
        UUID creatorId = UUID.randomUUID();
        PurchaseOrderInputDTO input = PurchaseOrderFixture.validInput(UUID.randomUUID(), creatorId, List.of());
        input.setId(UUID.randomUUID());
        input.setStatus(PurchaseOrder.Status.APPROVED);

        PurchaseOrder entity = mapper.toEntity(input);

        assertThat(entity.getId()).isEqualTo(input.getId());
        assertThat(entity.getNumber()).isEqualTo(PurchaseOrderFixture.NUMBER);
        assertThat(entity.getStatus()).isEqualTo(PurchaseOrder.Status.APPROVED);
        assertThat(entity.getOrderedAt()).isEqualTo(PurchaseOrderFixture.ORDERED_AT);
        assertThat(entity.getExpectedAt()).isEqualTo(PurchaseOrderFixture.EXPECTED_AT);
        assertThat(entity.getCreatedBy().getId()).isEqualTo(creatorId);
        assertThat(entity.getSupplier()).isNull();
        assertThat(entity.getItems()).isEmpty();
    }

    @Test
    void shouldDefaultStatusToDraftAndSkipCreatorWhenNotInformed() {
        PurchaseOrderInputDTO input = PurchaseOrderFixture.validInput(UUID.randomUUID(), null, List.of());

        PurchaseOrder entity = mapper.toEntity(input);

        assertThat(entity.getStatus()).isEqualTo(PurchaseOrder.Status.DRAFT);
        assertThat(entity.getCreatedBy()).isNull();
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponseWithItemsAndTotalAmount() {
        Supplier supplier = PriceAcquisitionReferenceFixture.activeSupplier();
        User creator = PriceAcquisitionReferenceFixture.activeUser();
        PurchaseOrder purchaseOrder = PurchaseOrderFixture.persistedEntity(PurchaseOrder.Status.DRAFT, supplier, creator);
        PurchaseOrderItem item = PurchaseOrderItemFixture.persistedEntity(purchaseOrder);
        purchaseOrder.setItems(List.of(item));

        PurchaseOrderResponseDTO response = mapper.toResponseDTO(purchaseOrder);

        assertThat(response.getId()).isEqualTo(purchaseOrder.getId());
        assertThat(response.getSupplierId()).isEqualTo(supplier.getId());
        assertThat(response.getCreatedBy()).isEqualTo(creator.getId());
        assertThat(response.getNumber()).isEqualTo(PurchaseOrderFixture.NUMBER);
        assertThat(response.getStatus()).isEqualTo(PurchaseOrder.Status.DRAFT);
        assertThat(response.getOrderedAt()).isEqualTo(PurchaseOrderFixture.ORDERED_AT);
        assertThat(response.getExpectedAt()).isEqualTo(PurchaseOrderFixture.EXPECTED_AT);
        assertThat(response.getTotalAmount()).isEqualByComparingTo(PurchaseOrderItemFixture.TOTAL_COST);
        assertThat(response.getItems()).singleElement()
                .satisfies(itemResponse -> {
                    assertThat(itemResponse.getId()).isEqualTo(item.getId());
                    assertThat(itemResponse.getPurchaseOrderId()).isEqualTo(purchaseOrder.getId());
                    assertThat(itemResponse.getTotalCost()).isEqualByComparingTo(PurchaseOrderItemFixture.TOTAL_COST);
                });
    }

    @Test
    void shouldMapEntityWithoutRelationsToResponseWithNullIdsAndZeroTotal() {
        PurchaseOrder purchaseOrder = new PurchaseOrder();

        PurchaseOrderResponseDTO response = mapper.toResponseDTO(purchaseOrder);

        assertThat(response.getSupplierId()).isNull();
        assertThat(response.getCreatedBy()).isNull();
        assertThat(response.getTotalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getItems()).isEmpty();
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.toResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapItemToResponse() {
        PurchaseOrder purchaseOrder = PurchaseOrderFixture.persistedEntity(PurchaseOrder.Status.DRAFT);
        PurchaseOrderItem item = PurchaseOrderItemFixture.persistedEntity(purchaseOrder);

        PurchaseOrderItemResponseDTO response = mapper.toItemResponseDTO(item);

        assertThat(response.getId()).isEqualTo(item.getId());
        assertThat(response.getPurchaseOrderId()).isEqualTo(purchaseOrder.getId());
        assertThat(response.getProductId()).isEqualTo(item.getProductId());
        assertThat(response.getQuantity()).isEqualTo(PurchaseOrderItemFixture.QUANTITY);
        assertThat(response.getUnitCost()).isEqualTo(PurchaseOrderItemFixture.UNIT_COST);
        assertThat(response.getTotalCost()).isEqualByComparingTo(PurchaseOrderItemFixture.TOTAL_COST);
    }

    @Test
    void shouldMapItemWithoutPurchaseOrderToNullPurchaseOrderId() {
        PurchaseOrderItem item = PurchaseOrderItemFixture.persistedEntity(null);

        assertThat(mapper.toItemResponseDTO(item).getPurchaseOrderId()).isNull();
    }

    @Test
    void shouldReturnNullWhenItemIsNull() {
        assertThat(mapper.toItemResponseDTO(null)).isNull();
    }
}
