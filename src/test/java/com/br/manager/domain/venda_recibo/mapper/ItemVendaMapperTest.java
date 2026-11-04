package com.br.manager.domain.venda_recibo.mapper;

import com.br.manager.domain.venda_recibo.dto.VendaItemInputDTO;
import com.br.manager.domain.venda_recibo.dto.VendaItemResponseDTO;
import com.br.manager.domain.venda_recibo.entity.VendaItem;
import com.br.manager.domain.venda_recibo.fixture.VendaItemFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ItemVendaMapperTest {

    private final ItemVendaMapper mapper = new ItemVendaMapper();

    @Test
    void shouldMapInputToEntity() {
        VendaItemInputDTO input = VendaItemFixture.combustivelInput(UUID.randomUUID(), UUID.randomUUID());
        input.setId(UUID.randomUUID());
        input.setTanqueId(UUID.randomUUID());
        input.setQuantidade(VendaItemFixture.COMBUSTIVEL_QUANTITY);

        VendaItem item = mapper.toEntity(input);

        assertThat(item)
                .usingRecursiveComparison()
                .ignoringFields("venda")
                .isEqualTo(input);
        assertThat(item.getVenda()).isNull();
    }

    @Test
    void shouldKeepNullDescontoWhenInputHasNoDesconto() {
        VendaItemInputDTO input = VendaItemFixture.produtoInput(UUID.randomUUID());
        input.setValorDesconto(null);

        VendaItem item = mapper.toEntity(input);

        assertThat(item.getValorDesconto()).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        VendaItem item = VendaItemFixture.persistedCombustivelEntity();

        VendaItemResponseDTO response = mapper.toResponse(item);

        assertThat(response).usingRecursiveComparison().isEqualTo(item);
    }

    @Test
    void shouldMapEntityListToResponseListKeepingOrder() {
        List<VendaItem> itens = List.of(VendaItemFixture.persistedEntity(UUID.randomUUID()), VendaItemFixture.persistedCombustivelEntity());

        List<VendaItemResponseDTO> responses = mapper.toResponseList(itens);

        assertThat(responses)
                .extracting(VendaItemResponseDTO::id)
                .containsExactly(itens.get(0).getId(), itens.get(1).getId());
    }

    @Test
    void shouldMapEmptyListToEmptyResponseList() {
        assertThat(mapper.toResponseList(List.of())).isEmpty();
    }
}
