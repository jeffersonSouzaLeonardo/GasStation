package com.br.manager.domain.price_acquisition.mapper;

import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.price_acquisition.dto.PriceItemResponseDTO;
import com.br.manager.domain.price_acquisition.dto.PriceTableInputDTO;
import com.br.manager.domain.price_acquisition.dto.PriceTableResponseDTO;
import com.br.manager.domain.price_acquisition.entity.PriceItem;
import com.br.manager.domain.price_acquisition.entity.PriceTable;
import com.br.manager.domain.price_acquisition.fixture.PriceAcquisitionReferenceFixture;
import com.br.manager.domain.price_acquisition.fixture.PriceItemFixture;
import com.br.manager.domain.price_acquisition.fixture.PriceTableFixture;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PriceTableMapperTest {

    private final PriceTableMapper mapper = new PriceTableMapper();

    @Test
    void shouldMapInputToEntityWithCreatorReference() {
        UUID creatorId = UUID.randomUUID();
        PriceTableInputDTO input = PriceTableFixture.validInput(creatorId, List.of());
        input.setId(UUID.randomUUID());

        PriceTable entity = mapper.priceTableInputDTOToPriceTable(input);

        assertThat(entity.getId()).isEqualTo(input.getId());
        assertThat(entity.getName()).isEqualTo(PriceTableFixture.NAME);
        assertThat(entity.getValidFrom()).isEqualTo(PriceTableFixture.VALID_FROM);
        assertThat(entity.getValidTo()).isEqualTo(PriceTableFixture.VALID_TO);
        assertThat(entity.getStatus()).isEqualTo(PriceTableFixture.STATUS);
        assertThat(entity.getCreatedBy().getId()).isEqualTo(creatorId);
    }

    @Test
    void shouldNotSetCreatorWhenInputHasNoCreator() {
        PriceTableInputDTO input = PriceTableFixture.validInput(null, List.of());

        PriceTable entity = mapper.priceTableInputDTOToPriceTable(input);

        assertThat(entity.getCreatedBy()).isNull();
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.priceTableInputDTOToPriceTable(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponseFormattingDatesAndNestedItems() {
        User creator = PriceAcquisitionReferenceFixture.activeUser();
        PriceTable priceTable = PriceTableFixture.persistedEntity(creator);
        PriceItem priceItem = PriceItemFixture.persistedEntity(priceTable);
        priceTable.setPriceItems(Arrays.asList(priceItem, null));

        PriceTableResponseDTO response = mapper.priceTableToPriceTableResponseDTO(priceTable);

        assertThat(response.getId()).isEqualTo(priceTable.getId());
        assertThat(response.getName()).isEqualTo(PriceTableFixture.NAME);
        assertThat(response.getValidFrom()).isEqualTo(PriceTableFixture.VALID_FROM.toString());
        assertThat(response.getValidTo()).isEqualTo(PriceTableFixture.VALID_TO.toString());
        assertThat(response.getStatus()).isEqualTo(PriceTableFixture.STATUS);
        assertThat(response.getCreatedBy()).isEqualTo(creator.getId());
        assertThat(response.getPriceItems()).singleElement()
                .usingRecursiveComparison()
                .isEqualTo(expectedItemResponse(priceItem));
    }

    @Test
    void shouldMapEntityWithoutOptionalDataToResponseWithNulls() {
        PriceTable priceTable = new PriceTable();

        PriceTableResponseDTO response = mapper.priceTableToPriceTableResponseDTO(priceTable);

        assertThat(response.getValidFrom()).isNull();
        assertThat(response.getValidTo()).isNull();
        assertThat(response.getCreatedBy()).isNull();
        assertThat(response.getPriceItems()).isNull();
    }

    @Test
    void shouldMapNestedItemWithoutPriceTableToNullPriceTableId() {
        PriceTable priceTable = PriceTableFixture.persistedEntity(null);
        priceTable.setPriceItems(List.of(PriceItemFixture.persistedEntity(null)));

        PriceTableResponseDTO response = mapper.priceTableToPriceTableResponseDTO(priceTable);

        assertThat(response.getPriceItems()).singleElement()
                .extracting(PriceItemResponseDTO::getPriceTableId)
                .isNull();
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.priceTableToPriceTableResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListIgnoringNullElements() {
        PriceTable first = PriceTableFixture.persistedEntity(null);
        PriceTable second = PriceTableFixture.persistedEntity(null);

        List<PriceTableResponseDTO> responses = mapper.listPriceTableToListPriceTableResponseDTO(Arrays.asList(first, null, second));

        assertThat(responses).extracting(PriceTableResponseDTO::getId).containsExactly(first.getId(), second.getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listPriceTableToListPriceTableResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityWithProvidedFieldsKeepingIdAndCreator() {
        User creator = PriceAcquisitionReferenceFixture.activeUser();
        PriceTable priceTable = PriceTableFixture.persistedEntity(creator);
        UUID originalId = priceTable.getId();
        LocalDateTime newValidTo = PriceTableFixture.VALID_TO.plusMonths(1);
        PriceTableInputDTO input = new PriceTableInputDTO();
        input.setId(UUID.randomUUID());
        input.setName(PriceTableFixture.OTHER_NAME);
        input.setValidFrom(PriceTableFixture.VALID_FROM.plusDays(1));
        input.setValidTo(newValidTo);
        input.setStatus(PriceTableFixture.INACTIVE_STATUS);
        input.setCreatedBy(UUID.randomUUID());

        mapper.updatePriceTableFromDto(input, priceTable);

        assertThat(priceTable.getId()).isEqualTo(originalId);
        assertThat(priceTable.getName()).isEqualTo(PriceTableFixture.OTHER_NAME);
        assertThat(priceTable.getValidFrom()).isEqualTo(PriceTableFixture.VALID_FROM.plusDays(1));
        assertThat(priceTable.getValidTo()).isEqualTo(newValidTo);
        assertThat(priceTable.getStatus()).isEqualTo(PriceTableFixture.INACTIVE_STATUS);
        assertThat(priceTable.getCreatedBy()).isSameAs(creator);
    }

    @Test
    void shouldKeepEntityValuesWhenInputFieldsAreNull() {
        PriceTable priceTable = PriceTableFixture.persistedEntity(null);

        mapper.updatePriceTableFromDto(new PriceTableInputDTO(), priceTable);

        assertThat(priceTable.getName()).isEqualTo(PriceTableFixture.NAME);
        assertThat(priceTable.getValidFrom()).isEqualTo(PriceTableFixture.VALID_FROM);
        assertThat(priceTable.getValidTo()).isEqualTo(PriceTableFixture.VALID_TO);
        assertThat(priceTable.getStatus()).isEqualTo(PriceTableFixture.STATUS);
    }

    @Test
    void shouldIgnoreUpdateWhenInputOrEntityIsNull() {
        PriceTable priceTable = PriceTableFixture.persistedEntity(null);

        mapper.updatePriceTableFromDto(null, priceTable);
        mapper.updatePriceTableFromDto(PriceTableFixture.validInput(null, null), null);

        assertThat(priceTable.getName()).isEqualTo(PriceTableFixture.NAME);
    }

    private PriceItemResponseDTO expectedItemResponse(PriceItem priceItem) {
        PriceItemResponseDTO expected = new PriceItemResponseDTO();
        expected.setId(priceItem.getId());
        expected.setPriceTableId(priceItem.getPriceTable().getId());
        expected.setProductId(priceItem.getProductId());
        expected.setNozzleId(priceItem.getNozzleId());
        expected.setUnitPrice(PriceItemFixture.UNIT_PRICE);
        expected.setMinPrice(PriceItemFixture.MIN_PRICE);
        expected.setMaxDiscountPercent(PriceItemFixture.MAX_DISCOUNT_PERCENT);
        expected.setActive(true);
        return expected;
    }
}
