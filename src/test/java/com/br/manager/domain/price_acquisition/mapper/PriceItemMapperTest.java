package com.br.manager.domain.price_acquisition.mapper;

import com.br.manager.domain.price_acquisition.dto.PriceItemInputDTO;
import com.br.manager.domain.price_acquisition.dto.PriceItemResponseDTO;
import com.br.manager.domain.price_acquisition.entity.PriceItem;
import com.br.manager.domain.price_acquisition.entity.PriceTable;
import com.br.manager.domain.price_acquisition.fixture.PriceItemFixture;
import com.br.manager.domain.price_acquisition.fixture.PriceTableFixture;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PriceItemMapperTest {

    private final PriceItemMapper mapper = new PriceItemMapper();

    @Test
    void shouldMapInputToEntityWithoutPriceTable() {
        PriceItemInputDTO input = PriceItemFixture.validInput(UUID.randomUUID(), UUID.randomUUID());
        input.setId(UUID.randomUUID());
        input.setNozzleId(UUID.randomUUID());
        input.setActive(false);

        PriceItem entity = mapper.priceItemInputDTOToPriceItem(input);

        assertThat(entity.getId()).isEqualTo(input.getId());
        assertThat(entity.getProductId()).isEqualTo(input.getProductId());
        assertThat(entity.getNozzleId()).isEqualTo(input.getNozzleId());
        assertThat(entity.getUnitPrice()).isEqualTo(PriceItemFixture.UNIT_PRICE);
        assertThat(entity.getMinPrice()).isEqualTo(PriceItemFixture.MIN_PRICE);
        assertThat(entity.getMaxDiscountPercent()).isEqualTo(PriceItemFixture.MAX_DISCOUNT_PERCENT);
        assertThat(entity.getActive()).isFalse();
        assertThat(entity.getPriceTable()).isNull();
    }

    @Test
    void shouldDefaultActiveToTrueWhenInputActiveIsNull() {
        PriceItemInputDTO input = PriceItemFixture.validInput(UUID.randomUUID(), UUID.randomUUID());
        input.setActive(null);

        PriceItem entity = mapper.priceItemInputDTOToPriceItem(input);

        assertThat(entity.getActive()).isTrue();
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.priceItemInputDTOToPriceItem(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        PriceTable priceTable = PriceTableFixture.persistedEntity(null);
        PriceItem priceItem = PriceItemFixture.persistedEntity(priceTable);

        PriceItemResponseDTO response = mapper.priceItemToPriceItemResponseDTO(priceItem);

        assertThat(response.getId()).isEqualTo(priceItem.getId());
        assertThat(response.getPriceTableId()).isEqualTo(priceTable.getId());
        assertThat(response.getProductId()).isEqualTo(priceItem.getProductId());
        assertThat(response.getNozzleId()).isEqualTo(priceItem.getNozzleId());
        assertThat(response.getUnitPrice()).isEqualTo(PriceItemFixture.UNIT_PRICE);
        assertThat(response.getMinPrice()).isEqualTo(PriceItemFixture.MIN_PRICE);
        assertThat(response.getMaxDiscountPercent()).isEqualTo(PriceItemFixture.MAX_DISCOUNT_PERCENT);
        assertThat(response.getActive()).isTrue();
    }

    @Test
    void shouldMapEntityWithoutPriceTableToNullPriceTableId() {
        PriceItem priceItem = PriceItemFixture.persistedEntity(null);

        assertThat(mapper.priceItemToPriceItemResponseDTO(priceItem).getPriceTableId()).isNull();
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.priceItemToPriceItemResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListIgnoringNullElements() {
        PriceItem first = PriceItemFixture.persistedEntity(null);
        PriceItem second = PriceItemFixture.persistedEntity(null);

        assertThat(mapper.listPriceItemToListPriceItemResponseDTO(Arrays.asList(first, null, second)))
                .extracting(PriceItemResponseDTO::getId)
                .containsExactly(first.getId(), second.getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listPriceItemToListPriceItemResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapInputListIgnoringNullElements() {
        PriceItemInputDTO first = PriceItemFixture.validInput(UUID.randomUUID(), UUID.randomUUID());
        PriceItemInputDTO second = PriceItemFixture.validInput(UUID.randomUUID(), UUID.randomUUID());

        assertThat(mapper.listPriceItemInputDTOToListPriceItem(Arrays.asList(first, null, second)))
                .extracting(PriceItem::getProductId)
                .containsExactly(first.getProductId(), second.getProductId());
    }

    @Test
    void shouldReturnNullWhenInputListIsNull() {
        assertThat(mapper.listPriceItemInputDTOToListPriceItem(null)).isNull();
    }

    @Test
    void shouldUpdateEntityWithProvidedFields() {
        PriceTable priceTable = PriceTableFixture.persistedEntity(null);
        PriceItem priceItem = PriceItemFixture.persistedEntity(priceTable);
        UUID originalId = priceItem.getId();
        PriceItemInputDTO input = PriceItemFixture.validInput(UUID.randomUUID(), UUID.randomUUID());
        input.setId(UUID.randomUUID());
        input.setNozzleId(UUID.randomUUID());
        input.setUnitPrice(PriceItemFixture.UPDATED_UNIT_PRICE);
        input.setActive(false);

        mapper.updatePriceItemFromDto(input, priceItem);

        assertThat(priceItem.getId()).isEqualTo(originalId);
        assertThat(priceItem.getPriceTable()).isSameAs(priceTable);
        assertThat(priceItem.getProductId()).isEqualTo(input.getProductId());
        assertThat(priceItem.getNozzleId()).isEqualTo(input.getNozzleId());
        assertThat(priceItem.getUnitPrice()).isEqualTo(PriceItemFixture.UPDATED_UNIT_PRICE);
        assertThat(priceItem.getActive()).isFalse();
    }

    @Test
    void shouldKeepEntityValuesWhenInputFieldsAreNull() {
        PriceItem priceItem = PriceItemFixture.persistedEntity(null);
        UUID productId = priceItem.getProductId();
        UUID nozzleId = priceItem.getNozzleId();
        PriceItemInputDTO input = new PriceItemInputDTO();
        input.setActive(null);

        mapper.updatePriceItemFromDto(input, priceItem);

        assertThat(priceItem.getProductId()).isEqualTo(productId);
        assertThat(priceItem.getNozzleId()).isEqualTo(nozzleId);
        assertThat(priceItem.getUnitPrice()).isEqualTo(PriceItemFixture.UNIT_PRICE);
        assertThat(priceItem.getMinPrice()).isEqualTo(PriceItemFixture.MIN_PRICE);
        assertThat(priceItem.getMaxDiscountPercent()).isEqualTo(PriceItemFixture.MAX_DISCOUNT_PERCENT);
        assertThat(priceItem.getActive()).isTrue();
    }

    @Test
    void shouldIgnoreUpdateWhenInputOrEntityIsNull() {
        PriceItem priceItem = PriceItemFixture.persistedEntity(null);

        mapper.updatePriceItemFromDto(null, priceItem);
        mapper.updatePriceItemFromDto(PriceItemFixture.validInput(null, null), null);

        assertThat(priceItem.getUnitPrice()).isEqualTo(PriceItemFixture.UNIT_PRICE);
    }
}
