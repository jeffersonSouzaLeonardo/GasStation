package com.br.manager.domain.price_acquisition.mapper;

import com.br.manager.domain.price_acquisition.dto.GoodsReceiptItemInputDTO;
import com.br.manager.domain.price_acquisition.dto.GoodsReceiptItemResponseDTO;
import com.br.manager.domain.price_acquisition.entity.GoodsReceipt;
import com.br.manager.domain.price_acquisition.entity.GoodsReceiptItem;
import com.br.manager.domain.price_acquisition.fixture.GoodsReceiptFixture;
import com.br.manager.domain.price_acquisition.fixture.GoodsReceiptItemFixture;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GoodsReceiptItemMapperTest {

    private final GoodsReceiptItemMapper mapper = new GoodsReceiptItemMapper();

    @Test
    void shouldMapInputToEntityWithoutGoodsReceipt() {
        GoodsReceiptItemInputDTO input = GoodsReceiptItemFixture.validInput(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        input.setId(UUID.randomUUID());

        GoodsReceiptItem entity = mapper.goodsReceiptItemInputDTOToGoodsReceiptItem(input);

        assertThat(entity.getId()).isEqualTo(input.getId());
        assertThat(entity.getProductId()).isEqualTo(input.getProductId());
        assertThat(entity.getTankId()).isEqualTo(input.getTankId());
        assertThat(entity.getQuantity()).isEqualTo(GoodsReceiptItemFixture.QUANTITY);
        assertThat(entity.getUnitCost()).isEqualTo(GoodsReceiptItemFixture.UNIT_COST);
        assertThat(entity.getGoodsReceipt()).isNull();
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.goodsReceiptItemInputDTOToGoodsReceiptItem(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        GoodsReceipt goodsReceipt = GoodsReceiptFixture.persistedEntity();
        GoodsReceiptItem item = GoodsReceiptItemFixture.persistedEntity(goodsReceipt);

        GoodsReceiptItemResponseDTO response = mapper.goodsReceiptItemToGoodsReceiptItemResponseDTO(item);

        assertThat(response.getId()).isEqualTo(item.getId());
        assertThat(response.getGoodsReceiptId()).isEqualTo(goodsReceipt.getId());
        assertThat(response.getProductId()).isEqualTo(item.getProductId());
        assertThat(response.getTankId()).isEqualTo(item.getTankId());
        assertThat(response.getQuantity()).isEqualTo(GoodsReceiptItemFixture.QUANTITY);
        assertThat(response.getUnitCost()).isEqualTo(GoodsReceiptItemFixture.UNIT_COST);
    }

    @Test
    void shouldMapEntityWithoutGoodsReceiptToNullGoodsReceiptId() {
        GoodsReceiptItem item = GoodsReceiptItemFixture.persistedEntity(null);

        assertThat(mapper.goodsReceiptItemToGoodsReceiptItemResponseDTO(item).getGoodsReceiptId()).isNull();
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.goodsReceiptItemToGoodsReceiptItemResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListIgnoringNullElements() {
        GoodsReceiptItem first = GoodsReceiptItemFixture.persistedEntity(null);
        GoodsReceiptItem second = GoodsReceiptItemFixture.persistedEntity(null);

        assertThat(mapper.listGoodsReceiptItemToListGoodsReceiptItemResponseDTO(Arrays.asList(first, null, second)))
                .extracting(GoodsReceiptItemResponseDTO::getId)
                .containsExactly(first.getId(), second.getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listGoodsReceiptItemToListGoodsReceiptItemResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityWithProvidedFieldsKeepingGoodsReceipt() {
        GoodsReceipt goodsReceipt = GoodsReceiptFixture.persistedEntity();
        GoodsReceiptItem item = GoodsReceiptItemFixture.persistedEntity(goodsReceipt);
        UUID originalId = item.getId();
        GoodsReceiptItemInputDTO input = GoodsReceiptItemFixture.validInput(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        input.setId(UUID.randomUUID());
        input.setQuantity(GoodsReceiptItemFixture.UPDATED_QUANTITY);

        mapper.updateGoodsReceiptItemFromDto(input, item);

        assertThat(item.getId()).isEqualTo(originalId);
        assertThat(item.getGoodsReceipt()).isSameAs(goodsReceipt);
        assertThat(item.getProductId()).isEqualTo(input.getProductId());
        assertThat(item.getTankId()).isEqualTo(input.getTankId());
        assertThat(item.getQuantity()).isEqualTo(GoodsReceiptItemFixture.UPDATED_QUANTITY);
        assertThat(item.getUnitCost()).isEqualTo(GoodsReceiptItemFixture.UNIT_COST);
    }

    @Test
    void shouldKeepEntityValuesWhenInputFieldsAreNull() {
        GoodsReceiptItem item = GoodsReceiptItemFixture.persistedEntity(null);
        UUID productId = item.getProductId();
        UUID tankId = item.getTankId();

        mapper.updateGoodsReceiptItemFromDto(new GoodsReceiptItemInputDTO(), item);

        assertThat(item.getProductId()).isEqualTo(productId);
        assertThat(item.getTankId()).isEqualTo(tankId);
        assertThat(item.getQuantity()).isEqualTo(GoodsReceiptItemFixture.QUANTITY);
        assertThat(item.getUnitCost()).isEqualTo(GoodsReceiptItemFixture.UNIT_COST);
    }

    @Test
    void shouldIgnoreUpdateWhenInputOrEntityIsNull() {
        GoodsReceiptItem item = GoodsReceiptItemFixture.persistedEntity(null);

        mapper.updateGoodsReceiptItemFromDto(null, item);
        mapper.updateGoodsReceiptItemFromDto(GoodsReceiptItemFixture.validInput(null, null, null), null);

        assertThat(item.getQuantity()).isEqualTo(GoodsReceiptItemFixture.QUANTITY);
    }
}
