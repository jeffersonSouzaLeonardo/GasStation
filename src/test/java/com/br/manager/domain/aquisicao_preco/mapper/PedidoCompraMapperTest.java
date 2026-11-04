package com.br.manager.domain.aquisicao_preco.mapper;

import com.br.manager.domain.operacional.entity.Fornecedor;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.aquisicao_preco.dto.PedidoCompraInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemPedidoCompraResponseDTO;
import com.br.manager.domain.aquisicao_preco.dto.PedidoCompraResponseDTO;
import com.br.manager.domain.aquisicao_preco.entity.PedidoCompra;
import com.br.manager.domain.aquisicao_preco.entity.PedidoCompraItem;
import com.br.manager.domain.aquisicao_preco.fixture.AquisicaoPrecoReferenciaFixture;
import com.br.manager.domain.aquisicao_preco.fixture.PedidoCompraFixture;
import com.br.manager.domain.aquisicao_preco.fixture.PedidoCompraItemFixture;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PedidoCompraMapperTest {

    private final PedidoCompraMapper mapper = new PedidoCompraMapper();

    @Test
    void shouldMapInputToEntityWithCreatorReferenciaAndEmptyItens() {
        UUID creatorId = UUID.randomUUID();
        PedidoCompraInputDTO input = PedidoCompraFixture.validoInput(UUID.randomUUID(), creatorId, List.of());
        input.setId(UUID.randomUUID());
        input.setStatus(PedidoCompra.Situacao.APROVADO);

        PedidoCompra entity = mapper.toEntity(input);

        assertThat(entity.getId()).isEqualTo(input.getId());
        assertThat(entity.getNumero()).isEqualTo(PedidoCompraFixture.NUMERO);
        assertThat(entity.getStatus()).isEqualTo(PedidoCompra.Situacao.APROVADO);
        assertThat(entity.getPedidoEm()).isEqualTo(PedidoCompraFixture.PEDIDO_EM);
        assertThat(entity.getPrevistoEm()).isEqualTo(PedidoCompraFixture.PREVISTO_EM);
        assertThat(entity.getCriadoPor().getId()).isEqualTo(creatorId);
        assertThat(entity.getFornecedor()).isNull();
        assertThat(entity.getItens()).isEmpty();
    }

    @Test
    void shouldDefaultSituacaoToDraftAndSkipCreatorWhenNotInformed() {
        PedidoCompraInputDTO input = PedidoCompraFixture.validoInput(UUID.randomUUID(), null, List.of());

        PedidoCompra entity = mapper.toEntity(input);

        assertThat(entity.getStatus()).isEqualTo(PedidoCompra.Situacao.DRAFT);
        assertThat(entity.getCriadoPor()).isNull();
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponseWithItensAndTotalValor() {
        Fornecedor fornecedor = AquisicaoPrecoReferenciaFixture.ativoFornecedor();
        Usuario creator = AquisicaoPrecoReferenciaFixture.ativoUsuario();
        PedidoCompra pedidoCompra = PedidoCompraFixture.persistedEntity(PedidoCompra.Situacao.DRAFT, fornecedor, creator);
        PedidoCompraItem item = PedidoCompraItemFixture.persistedEntity(pedidoCompra);
        pedidoCompra.setItens(List.of(item));

        PedidoCompraResponseDTO response = mapper.toResponseDTO(pedidoCompra);

        assertThat(response.getId()).isEqualTo(pedidoCompra.getId());
        assertThat(response.getFornecedorId()).isEqualTo(fornecedor.getId());
        assertThat(response.getCriadoPor()).isEqualTo(creator.getId());
        assertThat(response.getNumero()).isEqualTo(PedidoCompraFixture.NUMERO);
        assertThat(response.getStatus()).isEqualTo(PedidoCompra.Situacao.DRAFT);
        assertThat(response.getPedidoEm()).isEqualTo(PedidoCompraFixture.PEDIDO_EM);
        assertThat(response.getPrevistoEm()).isEqualTo(PedidoCompraFixture.PREVISTO_EM);
        assertThat(response.getValorTotal()).isEqualByComparingTo(PedidoCompraItemFixture.CUSTO_TOTAL);
        assertThat(response.getItens()).singleElement()
                .satisfies(itemResponse -> {
                    assertThat(itemResponse.getId()).isEqualTo(item.getId());
                    assertThat(itemResponse.getCompraOrderId()).isEqualTo(pedidoCompra.getId());
                    assertThat(itemResponse.getCustoTotal()).isEqualByComparingTo(PedidoCompraItemFixture.CUSTO_TOTAL);
                });
    }

    @Test
    void shouldMapEntityWithoutRelationsToResponseWithNullIdsAndZeroTotal() {
        PedidoCompra pedidoCompra = new PedidoCompra();

        PedidoCompraResponseDTO response = mapper.toResponseDTO(pedidoCompra);

        assertThat(response.getFornecedorId()).isNull();
        assertThat(response.getCriadoPor()).isNull();
        assertThat(response.getValorTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getItens()).isEmpty();
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.toResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapItemToResponse() {
        PedidoCompra pedidoCompra = PedidoCompraFixture.persistedEntity(PedidoCompra.Situacao.DRAFT);
        PedidoCompraItem item = PedidoCompraItemFixture.persistedEntity(pedidoCompra);

        ItemPedidoCompraResponseDTO response = mapper.toItemResponseDTO(item);

        assertThat(response.getId()).isEqualTo(item.getId());
        assertThat(response.getCompraOrderId()).isEqualTo(pedidoCompra.getId());
        assertThat(response.getProdutoId()).isEqualTo(item.getProdutoId());
        assertThat(response.getQuantidade()).isEqualTo(PedidoCompraItemFixture.QUANTITY);
        assertThat(response.getCustoUnitario()).isEqualTo(PedidoCompraItemFixture.CUSTO_UNITARIO);
        assertThat(response.getCustoTotal()).isEqualByComparingTo(PedidoCompraItemFixture.CUSTO_TOTAL);
    }

    @Test
    void shouldMapItemWithoutPedidoCompraToNullPedidoCompraId() {
        PedidoCompraItem item = PedidoCompraItemFixture.persistedEntity(null);

        assertThat(mapper.toItemResponseDTO(item).getCompraOrderId()).isNull();
    }

    @Test
    void shouldReturnNullWhenItemIsNull() {
        assertThat(mapper.toItemResponseDTO(null)).isNull();
    }
}
