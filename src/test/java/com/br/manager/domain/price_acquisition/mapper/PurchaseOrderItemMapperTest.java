package com.br.manager.domain.price_acquisition.mapper;

import com.br.manager.domain.price_acquisition.dto.PurchaseOrderItemInputDTO;
import com.br.manager.domain.price_acquisition.dto.PurchaseOrderItemResponseDTO;
import com.br.manager.domain.price_acquisition.entity.PurchaseOrder;
import com.br.manager.domain.price_acquisition.entity.PurchaseOrderItem;
import com.br.manager.domain.price_acquisition.fixture.PurchaseOrderFixture;
import com.br.manager.domain.price_acquisition.fixture.PurchaseOrderItemFixture;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PurchaseOrderItemMapperTest {

    private final PurchaseOrderItemMapper mapper = new PurchaseOrderItemMapper();

    @Test
    void shouldMapInputToEntityWithPurchaseOrderReference() {
        UUID purchaseOrderId = UUID.randomUUID();
        PurchaseOrderItemInputDTO input = PurchaseOrderItemFixture.validInput(purchaseOrderId, UUID.randomUUID());
        input.setId(UUID.randomUUID());

        PurchaseOrderItem entity = mapper.toEntity(input);

        assertThat(entity.getId()).isEqualTo(input.getId());
        assertThat(entity.getPurchaseOrder().getId()).isEqualTo(purchaseOrderId);
        assertThat(entity.getProductId()).isEqualTo(input.getProductId());
        assertThat(entity.getQuantity()).isEqualTo(PurchaseOrderItemFixture.QUANTITY);
        assertThat(entity.getUnitCost()).isEqualTo(PurchaseOrderItemFixture.UNIT_COST);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponseWithTotalCost() {
        PurchaseOrder purchaseOrder = PurchaseOrderFixture.persistedEntity(PurchaseOrder.Status.DRAFT);
        PurchaseOrderItem item = PurchaseOrderItemFixture.persistedEntity(purchaseOrder);

        PurchaseOrderItemResponseDTO response = mapper.toResponseDTO(item);

        assertThat(response.getId()).isEqualTo(item.getId());
        assertThat(response.getPurchaseOrderId()).isEqualTo(purchaseOrder.getId());
        assertThat(response.getProductId()).isEqualTo(item.getProductId());
        assertThat(response.getQuantity()).isEqualTo(PurchaseOrderItemFixture.QUANTITY);
        assertThat(response.getUnitCost()).isEqualTo(PurchaseOrderItemFixture.UNIT_COST);
        assertThat(response.getTotalCost()).isEqualByComparingTo(PurchaseOrderItemFixture.TOTAL_COST);
    }

    @Test
    void shouldMapEntityWithoutCostDataToZeroTotalAndNullPurchaseOrderId() {
        PurchaseOrderItem item = new PurchaseOrderItem();

        PurchaseOrderItemResponseDTO response = mapper.toResponseDTO(item);

        assertThat(response.getPurchaseOrderId()).isNull();
        assertThat(response.getTotalCost()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.toResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListIgnoringNullElements() {
        PurchaseOrderItem first = PurchaseOrderItemFixture.persistedEntity(null);
        PurchaseOrderItem second = PurchaseOrderItemFixture.persistedEntity(null);

        assertThat(mapper.toResponseDTOList(Arrays.asList(first, null, second)))
                .extracting(PurchaseOrderItemResponseDTO::getId)
                .containsExactly(first.getId(), second.getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.toResponseDTOList(null)).isNull();
    }

    @Test
    void shouldUpdateEntityWithProvidedFieldsKeepingPurchaseOrder() {
        PurchaseOrder purchaseOrder = PurchaseOrderFixture.persistedEntity(PurchaseOrder.Status.DRAFT);
        PurchaseOrderItem item = PurchaseOrderItemFixture.persistedEntity(purchaseOrder);
        UUID originalId = item.getId();
        PurchaseOrderItemInputDTO input = PurchaseOrderItemFixture.validInput(UUID.randomUUID(), UUID.randomUUID());
        input.setId(UUID.randomUUID());
        input.setQuantity(PurchaseOrderItemFixture.UPDATED_QUANTITY);

        mapper.updateEntityFromDto(input, item);

        assertThat(item.getId()).isEqualTo(originalId);
        assertThat(item.getPurchaseOrder()).isSameAs(purchaseOrder);
        assertThat(item.getProductId()).isEqualTo(input.getProductId());
        assertThat(item.getQuantity()).isEqualTo(PurchaseOrderItemFixture.UPDATED_QUANTITY);
        assertThat(item.getTotalCost()).isEqualByComparingTo(PurchaseOrderItemFixture.UPDATED_TOTAL_COST);
    }

    @Test
    void shouldKeepEntityValuesWhenInputFieldsAreNull() {
        PurchaseOrderItem item = PurchaseOrderItemFixture.persistedEntity(null);
        UUID productId = item.getProductId();

        mapper.updateEntityFromDto(new PurchaseOrderItemInputDTO(), item);

        assertThat(item.getProductId()).isEqualTo(productId);
        assertThat(item.getQuantity()).isEqualTo(PurchaseOrderItemFixture.QUANTITY);
        assertThat(item.getUnitCost()).isEqualTo(PurchaseOrderItemFixture.UNIT_COST);
    }

    @Test
    void shouldIgnoreUpdateWhenInputOrEntityIsNull() {
        PurchaseOrderItem item = PurchaseOrderItemFixture.persistedEntity(null);

        mapper.updateEntityFromDto(null, item);
        mapper.updateEntityFromDto(PurchaseOrderItemFixture.validInput(null, null), null);

        assertThat(item.getQuantity()).isEqualTo(PurchaseOrderItemFixture.QUANTITY);
    }
}
