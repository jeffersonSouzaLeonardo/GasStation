package com.br.manager.domain.aquisicao_preco.mapper;

import com.br.manager.domain.aquisicao_preco.dto.ItemPrecoInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemPrecoResponseDTO;
import com.br.manager.domain.aquisicao_preco.entity.ItemPreco;
import com.br.manager.domain.aquisicao_preco.entity.TabelaPreco;
import com.br.manager.domain.aquisicao_preco.fixture.ItemPrecoFixture;
import com.br.manager.domain.aquisicao_preco.fixture.TabelaPrecoFixture;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ItemPrecoMapperTest {

    private final ItemPrecoMapper mapper = new ItemPrecoMapper();

    @Test
    void shouldMapInputToEntityWithoutTabelaPreco() {
        ItemPrecoInputDTO input = ItemPrecoFixture.validoInput(UUID.randomUUID(), UUID.randomUUID());
        input.setId(UUID.randomUUID());
        input.setBicoId(UUID.randomUUID());
        input.setAtivo(false);

        ItemPreco entity = mapper.precoItemInputDTOToItemPreco(input);

        assertThat(entity.getId()).isEqualTo(input.getId());
        assertThat(entity.getProdutoId()).isEqualTo(input.getProdutoId());
        assertThat(entity.getBicoId()).isEqualTo(input.getBicoId());
        assertThat(entity.getPrecoUnitario()).isEqualTo(ItemPrecoFixture.PRECO_UNITARIO);
        assertThat(entity.getPrecoMinimo()).isEqualTo(ItemPrecoFixture.PRECO_MINIMO);
        assertThat(entity.getDescontoMaximoPercentual()).isEqualTo(ItemPrecoFixture.DESCONTO_MAXIMO_PERCENTUAL);
        assertThat(entity.getAtivo()).isFalse();
        assertThat(entity.getTabelaPreco()).isNull();
    }

    @Test
    void shouldDefaultAtivoToTrueWhenInputAtivoIsNull() {
        ItemPrecoInputDTO input = ItemPrecoFixture.validoInput(UUID.randomUUID(), UUID.randomUUID());
        input.setAtivo(null);

        ItemPreco entity = mapper.precoItemInputDTOToItemPreco(input);

        assertThat(entity.getAtivo()).isTrue();
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.precoItemInputDTOToItemPreco(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        TabelaPreco tabelaPreco = TabelaPrecoFixture.persistedEntity(null);
        ItemPreco itemPreco = ItemPrecoFixture.persistedEntity(tabelaPreco);

        ItemPrecoResponseDTO response = mapper.precoItemToItemPrecoResponseDTO(itemPreco);

        assertThat(response.getId()).isEqualTo(itemPreco.getId());
        assertThat(response.getPrecoTabelaId()).isEqualTo(tabelaPreco.getId());
        assertThat(response.getProdutoId()).isEqualTo(itemPreco.getProdutoId());
        assertThat(response.getBicoId()).isEqualTo(itemPreco.getBicoId());
        assertThat(response.getPrecoUnitario()).isEqualTo(ItemPrecoFixture.PRECO_UNITARIO);
        assertThat(response.getPrecoMinimo()).isEqualTo(ItemPrecoFixture.PRECO_MINIMO);
        assertThat(response.getDescontoMaximoPercentual()).isEqualTo(ItemPrecoFixture.DESCONTO_MAXIMO_PERCENTUAL);
        assertThat(response.getAtivo()).isTrue();
    }

    @Test
    void shouldMapEntityWithoutTabelaPrecoToNullTabelaPrecoId() {
        ItemPreco itemPreco = ItemPrecoFixture.persistedEntity(null);

        assertThat(mapper.precoItemToItemPrecoResponseDTO(itemPreco).getPrecoTabelaId()).isNull();
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.precoItemToItemPrecoResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListIgnoringNullElements() {
        ItemPreco first = ItemPrecoFixture.persistedEntity(null);
        ItemPreco second = ItemPrecoFixture.persistedEntity(null);

        assertThat(mapper.listItemPrecoToListItemPrecoResponseDTO(Arrays.asList(first, null, second)))
                .extracting(ItemPrecoResponseDTO::getId)
                .containsExactly(first.getId(), second.getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listItemPrecoToListItemPrecoResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapInputListIgnoringNullElements() {
        ItemPrecoInputDTO first = ItemPrecoFixture.validoInput(UUID.randomUUID(), UUID.randomUUID());
        ItemPrecoInputDTO second = ItemPrecoFixture.validoInput(UUID.randomUUID(), UUID.randomUUID());

        assertThat(mapper.listItemPrecoInputDTOToListItemPreco(Arrays.asList(first, null, second)))
                .extracting(ItemPreco::getProdutoId)
                .containsExactly(first.getProdutoId(), second.getProdutoId());
    }

    @Test
    void shouldReturnNullWhenInputListIsNull() {
        assertThat(mapper.listItemPrecoInputDTOToListItemPreco(null)).isNull();
    }

    @Test
    void shouldUpdateEntityWithProvidedFields() {
        TabelaPreco tabelaPreco = TabelaPrecoFixture.persistedEntity(null);
        ItemPreco itemPreco = ItemPrecoFixture.persistedEntity(tabelaPreco);
        UUID originalId = itemPreco.getId();
        ItemPrecoInputDTO input = ItemPrecoFixture.validoInput(UUID.randomUUID(), UUID.randomUUID());
        input.setId(UUID.randomUUID());
        input.setBicoId(UUID.randomUUID());
        input.setPrecoUnitario(ItemPrecoFixture.PRECO_UNITARIO_ATUALIZADO);
        input.setAtivo(false);

        mapper.updateItemPrecoFromDto(input, itemPreco);

        assertThat(itemPreco.getId()).isEqualTo(originalId);
        assertThat(itemPreco.getTabelaPreco()).isSameAs(tabelaPreco);
        assertThat(itemPreco.getProdutoId()).isEqualTo(input.getProdutoId());
        assertThat(itemPreco.getBicoId()).isEqualTo(input.getBicoId());
        assertThat(itemPreco.getPrecoUnitario()).isEqualTo(ItemPrecoFixture.PRECO_UNITARIO_ATUALIZADO);
        assertThat(itemPreco.getAtivo()).isFalse();
    }

    @Test
    void shouldKeepEntityValuesWhenInputFieldsAreNull() {
        ItemPreco itemPreco = ItemPrecoFixture.persistedEntity(null);
        UUID produtoId = itemPreco.getProdutoId();
        UUID bicoId = itemPreco.getBicoId();
        ItemPrecoInputDTO input = new ItemPrecoInputDTO();
        input.setAtivo(null);

        mapper.updateItemPrecoFromDto(input, itemPreco);

        assertThat(itemPreco.getProdutoId()).isEqualTo(produtoId);
        assertThat(itemPreco.getBicoId()).isEqualTo(bicoId);
        assertThat(itemPreco.getPrecoUnitario()).isEqualTo(ItemPrecoFixture.PRECO_UNITARIO);
        assertThat(itemPreco.getPrecoMinimo()).isEqualTo(ItemPrecoFixture.PRECO_MINIMO);
        assertThat(itemPreco.getDescontoMaximoPercentual()).isEqualTo(ItemPrecoFixture.DESCONTO_MAXIMO_PERCENTUAL);
        assertThat(itemPreco.getAtivo()).isTrue();
    }

    @Test
    void shouldIgnoreUpdateWhenInputOrEntityIsNull() {
        ItemPreco itemPreco = ItemPrecoFixture.persistedEntity(null);

        mapper.updateItemPrecoFromDto(null, itemPreco);
        mapper.updateItemPrecoFromDto(ItemPrecoFixture.validoInput(null, null), null);

        assertThat(itemPreco.getPrecoUnitario()).isEqualTo(ItemPrecoFixture.PRECO_UNITARIO);
    }
}
