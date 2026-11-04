package com.br.manager.domain.aquisicao_preco.mapper;

import com.br.manager.domain.aquisicao_preco.dto.ItemRecebimentoMercadoriaInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemRecebimentoMercadoriaResponseDTO;
import com.br.manager.domain.aquisicao_preco.entity.RecebimentoMercadoria;
import com.br.manager.domain.aquisicao_preco.entity.RecebimentoMercadoriaItem;
import com.br.manager.domain.aquisicao_preco.fixture.RecebimentoMercadoriaFixture;
import com.br.manager.domain.aquisicao_preco.fixture.RecebimentoMercadoriaItemFixture;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRecebimentoMercadoriaMapperTest {

    private final ItemRecebimentoMercadoriaMapper mapper = new ItemRecebimentoMercadoriaMapper();

    @Test
    void shouldMapInputToEntityWithoutRecebimentoMercadoria() {
        ItemRecebimentoMercadoriaInputDTO input = RecebimentoMercadoriaItemFixture.validoInput(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        input.setId(UUID.randomUUID());

        RecebimentoMercadoriaItem entity = mapper.mercadoriaReciboItemInputDTOToRecebimentoMercadoriaItem(input);

        assertThat(entity.getId()).isEqualTo(input.getId());
        assertThat(entity.getProdutoId()).isEqualTo(input.getProdutoId());
        assertThat(entity.getTanqueId()).isEqualTo(input.getTanqueId());
        assertThat(entity.getQuantidade()).isEqualTo(RecebimentoMercadoriaItemFixture.QUANTITY);
        assertThat(entity.getCustoUnitario()).isEqualTo(RecebimentoMercadoriaItemFixture.CUSTO_UNITARIO);
        assertThat(entity.getRecebimentoMercadoria()).isNull();
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.mercadoriaReciboItemInputDTOToRecebimentoMercadoriaItem(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        RecebimentoMercadoria recebimentoMercadoria = RecebimentoMercadoriaFixture.persistedEntity();
        RecebimentoMercadoriaItem item = RecebimentoMercadoriaItemFixture.persistedEntity(recebimentoMercadoria);

        ItemRecebimentoMercadoriaResponseDTO response = mapper.mercadoriaReciboItemToItemRecebimentoMercadoriaResponseDTO(item);

        assertThat(response.getId()).isEqualTo(item.getId());
        assertThat(response.getMercadoriaReciboId()).isEqualTo(recebimentoMercadoria.getId());
        assertThat(response.getProdutoId()).isEqualTo(item.getProdutoId());
        assertThat(response.getTanqueId()).isEqualTo(item.getTanqueId());
        assertThat(response.getQuantidade()).isEqualTo(RecebimentoMercadoriaItemFixture.QUANTITY);
        assertThat(response.getCustoUnitario()).isEqualTo(RecebimentoMercadoriaItemFixture.CUSTO_UNITARIO);
    }

    @Test
    void shouldMapEntityWithoutRecebimentoMercadoriaToNullRecebimentoMercadoriaId() {
        RecebimentoMercadoriaItem item = RecebimentoMercadoriaItemFixture.persistedEntity(null);

        assertThat(mapper.mercadoriaReciboItemToItemRecebimentoMercadoriaResponseDTO(item).getMercadoriaReciboId()).isNull();
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.mercadoriaReciboItemToItemRecebimentoMercadoriaResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListIgnoringNullElements() {
        RecebimentoMercadoriaItem first = RecebimentoMercadoriaItemFixture.persistedEntity(null);
        RecebimentoMercadoriaItem second = RecebimentoMercadoriaItemFixture.persistedEntity(null);

        assertThat(mapper.listRecebimentoMercadoriaItemToListItemRecebimentoMercadoriaResponseDTO(Arrays.asList(first, null, second)))
                .extracting(ItemRecebimentoMercadoriaResponseDTO::getId)
                .containsExactly(first.getId(), second.getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listRecebimentoMercadoriaItemToListItemRecebimentoMercadoriaResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityWithProvidedFieldsKeepingRecebimentoMercadoria() {
        RecebimentoMercadoria recebimentoMercadoria = RecebimentoMercadoriaFixture.persistedEntity();
        RecebimentoMercadoriaItem item = RecebimentoMercadoriaItemFixture.persistedEntity(recebimentoMercadoria);
        UUID originalId = item.getId();
        ItemRecebimentoMercadoriaInputDTO input = RecebimentoMercadoriaItemFixture.validoInput(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        input.setId(UUID.randomUUID());
        input.setQuantidade(RecebimentoMercadoriaItemFixture.QUANTIDADE_ATUALIZADA);

        mapper.updateRecebimentoMercadoriaItemFromDto(input, item);

        assertThat(item.getId()).isEqualTo(originalId);
        assertThat(item.getRecebimentoMercadoria()).isSameAs(recebimentoMercadoria);
        assertThat(item.getProdutoId()).isEqualTo(input.getProdutoId());
        assertThat(item.getTanqueId()).isEqualTo(input.getTanqueId());
        assertThat(item.getQuantidade()).isEqualTo(RecebimentoMercadoriaItemFixture.QUANTIDADE_ATUALIZADA);
        assertThat(item.getCustoUnitario()).isEqualTo(RecebimentoMercadoriaItemFixture.CUSTO_UNITARIO);
    }

    @Test
    void shouldKeepEntityValuesWhenInputFieldsAreNull() {
        RecebimentoMercadoriaItem item = RecebimentoMercadoriaItemFixture.persistedEntity(null);
        UUID produtoId = item.getProdutoId();
        UUID tanqueId = item.getTanqueId();

        mapper.updateRecebimentoMercadoriaItemFromDto(new ItemRecebimentoMercadoriaInputDTO(), item);

        assertThat(item.getProdutoId()).isEqualTo(produtoId);
        assertThat(item.getTanqueId()).isEqualTo(tanqueId);
        assertThat(item.getQuantidade()).isEqualTo(RecebimentoMercadoriaItemFixture.QUANTITY);
        assertThat(item.getCustoUnitario()).isEqualTo(RecebimentoMercadoriaItemFixture.CUSTO_UNITARIO);
    }

    @Test
    void shouldIgnoreUpdateWhenInputOrEntityIsNull() {
        RecebimentoMercadoriaItem item = RecebimentoMercadoriaItemFixture.persistedEntity(null);

        mapper.updateRecebimentoMercadoriaItemFromDto(null, item);
        mapper.updateRecebimentoMercadoriaItemFromDto(RecebimentoMercadoriaItemFixture.validoInput(null, null, null), null);

        assertThat(item.getQuantidade()).isEqualTo(RecebimentoMercadoriaItemFixture.QUANTITY);
    }
}
