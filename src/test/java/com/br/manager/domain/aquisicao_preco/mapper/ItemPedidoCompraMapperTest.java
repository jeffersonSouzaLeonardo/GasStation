package com.br.manager.domain.aquisicao_preco.mapper;

import com.br.manager.domain.aquisicao_preco.dto.ItemPedidoCompraInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemPedidoCompraResponseDTO;
import com.br.manager.domain.aquisicao_preco.entity.PedidoCompra;
import com.br.manager.domain.aquisicao_preco.entity.PedidoCompraItem;
import com.br.manager.domain.aquisicao_preco.fixture.PedidoCompraFixture;
import com.br.manager.domain.aquisicao_preco.fixture.PedidoCompraItemFixture;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ItemPedidoCompraMapperTest {

    private final ItemPedidoCompraMapper mapper = new ItemPedidoCompraMapper();

    @Test
    void shouldMapInputToEntityWithPedidoCompraReferencia() {
        UUID compraOrderId = UUID.randomUUID();
        ItemPedidoCompraInputDTO input = PedidoCompraItemFixture.validoInput(compraOrderId, UUID.randomUUID());
        input.setId(UUID.randomUUID());

        PedidoCompraItem entity = mapper.toEntity(input);

        assertThat(entity.getId()).isEqualTo(input.getId());
        assertThat(entity.getPedidoCompra().getId()).isEqualTo(compraOrderId);
        assertThat(entity.getProdutoId()).isEqualTo(input.getProdutoId());
        assertThat(entity.getQuantidade()).isEqualTo(PedidoCompraItemFixture.QUANTITY);
        assertThat(entity.getCustoUnitario()).isEqualTo(PedidoCompraItemFixture.CUSTO_UNITARIO);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponseWithTotalCusto() {
        PedidoCompra pedidoCompra = PedidoCompraFixture.persistedEntity(PedidoCompra.Situacao.DRAFT);
        PedidoCompraItem item = PedidoCompraItemFixture.persistedEntity(pedidoCompra);

        ItemPedidoCompraResponseDTO response = mapper.toResponseDTO(item);

        assertThat(response.getId()).isEqualTo(item.getId());
        assertThat(response.getCompraOrderId()).isEqualTo(pedidoCompra.getId());
        assertThat(response.getProdutoId()).isEqualTo(item.getProdutoId());
        assertThat(response.getQuantidade()).isEqualTo(PedidoCompraItemFixture.QUANTITY);
        assertThat(response.getCustoUnitario()).isEqualTo(PedidoCompraItemFixture.CUSTO_UNITARIO);
        assertThat(response.getCustoTotal()).isEqualByComparingTo(PedidoCompraItemFixture.CUSTO_TOTAL);
    }

    @Test
    void shouldMapEntityWithoutCustoDataToZeroTotalAndNullPedidoCompraId() {
        PedidoCompraItem item = new PedidoCompraItem();

        ItemPedidoCompraResponseDTO response = mapper.toResponseDTO(item);

        assertThat(response.getCompraOrderId()).isNull();
        assertThat(response.getCustoTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.toResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListIgnoringNullElements() {
        PedidoCompraItem first = PedidoCompraItemFixture.persistedEntity(null);
        PedidoCompraItem second = PedidoCompraItemFixture.persistedEntity(null);

        assertThat(mapper.toResponseDTOList(Arrays.asList(first, null, second)))
                .extracting(ItemPedidoCompraResponseDTO::getId)
                .containsExactly(first.getId(), second.getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.toResponseDTOList(null)).isNull();
    }

    @Test
    void shouldUpdateEntityWithProvidedFieldsKeepingPedidoCompra() {
        PedidoCompra pedidoCompra = PedidoCompraFixture.persistedEntity(PedidoCompra.Situacao.DRAFT);
        PedidoCompraItem item = PedidoCompraItemFixture.persistedEntity(pedidoCompra);
        UUID originalId = item.getId();
        ItemPedidoCompraInputDTO input = PedidoCompraItemFixture.validoInput(UUID.randomUUID(), UUID.randomUUID());
        input.setId(UUID.randomUUID());
        input.setQuantidade(PedidoCompraItemFixture.QUANTIDADE_ATUALIZADA);

        mapper.updateEntityFromDto(input, item);

        assertThat(item.getId()).isEqualTo(originalId);
        assertThat(item.getPedidoCompra()).isSameAs(pedidoCompra);
        assertThat(item.getProdutoId()).isEqualTo(input.getProdutoId());
        assertThat(item.getQuantidade()).isEqualTo(PedidoCompraItemFixture.QUANTIDADE_ATUALIZADA);
        assertThat(item.getCustoTotal()).isEqualByComparingTo(PedidoCompraItemFixture.ATUALIZADO_TOTAL_CUSTO);
    }

    @Test
    void shouldKeepEntityValuesWhenInputFieldsAreNull() {
        PedidoCompraItem item = PedidoCompraItemFixture.persistedEntity(null);
        UUID produtoId = item.getProdutoId();

        mapper.updateEntityFromDto(new ItemPedidoCompraInputDTO(), item);

        assertThat(item.getProdutoId()).isEqualTo(produtoId);
        assertThat(item.getQuantidade()).isEqualTo(PedidoCompraItemFixture.QUANTITY);
        assertThat(item.getCustoUnitario()).isEqualTo(PedidoCompraItemFixture.CUSTO_UNITARIO);
    }

    @Test
    void shouldIgnoreUpdateWhenInputOrEntityIsNull() {
        PedidoCompraItem item = PedidoCompraItemFixture.persistedEntity(null);

        mapper.updateEntityFromDto(null, item);
        mapper.updateEntityFromDto(PedidoCompraItemFixture.validoInput(null, null), null);

        assertThat(item.getQuantidade()).isEqualTo(PedidoCompraItemFixture.QUANTITY);
    }
}
