package com.br.manager.domain.sale_receipt.mapper;

import com.br.manager.domain.sale_receipt.dto.SaleItemInputDTO;
import com.br.manager.domain.sale_receipt.dto.SaleItemResponseDTO;
import com.br.manager.domain.sale_receipt.entity.SaleItem;
import com.br.manager.domain.sale_receipt.fixture.SaleItemFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SaleItemMapperTest {

    private final SaleItemMapper mapper = new SaleItemMapper();

    @Test
    void shouldMapInputToEntity() {
        SaleItemInputDTO input = SaleItemFixture.fuelInput(UUID.randomUUID(), UUID.randomUUID());
        input.setId(UUID.randomUUID());
        input.setTankId(UUID.randomUUID());
        input.setQuantity(SaleItemFixture.FUEL_QUANTITY);

        SaleItem item = mapper.toEntity(input);

        assertThat(item)
                .usingRecursiveComparison()
                .ignoringFields("sale")
                .isEqualTo(input);
        assertThat(item.getSale()).isNull();
    }

    @Test
    void shouldKeepNullDiscountWhenInputHasNoDiscount() {
        SaleItemInputDTO input = SaleItemFixture.productInput(UUID.randomUUID());
        input.setDiscountAmount(null);

        SaleItem item = mapper.toEntity(input);

        assertThat(item.getDiscountAmount()).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        SaleItem item = SaleItemFixture.persistedFuelEntity();

        SaleItemResponseDTO response = mapper.toResponse(item);

        assertThat(response).usingRecursiveComparison().isEqualTo(item);
    }

    @Test
    void shouldMapEntityListToResponseListKeepingOrder() {
        List<SaleItem> items = List.of(SaleItemFixture.persistedEntity(UUID.randomUUID()), SaleItemFixture.persistedFuelEntity());

        List<SaleItemResponseDTO> responses = mapper.toResponseList(items);

        assertThat(responses)
                .extracting(SaleItemResponseDTO::id)
                .containsExactly(items.get(0).getId(), items.get(1).getId());
    }

    @Test
    void shouldMapEmptyListToEmptyResponseList() {
        assertThat(mapper.toResponseList(List.of())).isEmpty();
    }
}
