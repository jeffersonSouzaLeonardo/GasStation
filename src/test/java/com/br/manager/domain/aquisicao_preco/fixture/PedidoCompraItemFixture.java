package com.br.manager.domain.aquisicao_preco.fixture;

import com.br.manager.domain.aquisicao_preco.dto.ItemPedidoCompraInputDTO;
import com.br.manager.domain.aquisicao_preco.entity.PedidoCompra;
import com.br.manager.domain.aquisicao_preco.entity.PedidoCompraItem;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Item de Pedido de Compra.
 */
public final class PedidoCompraItemFixture {

    public static final int QUANTITY = 1000;
    public static final int QUANTIDADE_ATUALIZADA = 2000;
    public static final BigDecimal CUSTO_UNITARIO = new BigDecimal("5.2500");
    public static final BigDecimal CUSTO_TOTAL = new BigDecimal("5250.0000");
    public static final BigDecimal ATUALIZADO_TOTAL_CUSTO = new BigDecimal("10500.0000");

    private PedidoCompraItemFixture() {
    }

    public static ItemPedidoCompraInputDTO validoInput(UUID compraOrderId, UUID produtoId) {
        ItemPedidoCompraInputDTO input = new ItemPedidoCompraInputDTO();
        input.setCompraOrderId(compraOrderId);
        input.setProdutoId(produtoId);
        input.setQuantidade(QUANTITY);
        input.setCustoUnitario(CUSTO_UNITARIO);
        return input;
    }

    public static PedidoCompraItem persistedEntity(PedidoCompra pedidoCompra) {
        PedidoCompraItem item = new PedidoCompraItem();
        item.setId(UUID.randomUUID());
        item.setPedidoCompra(pedidoCompra);
        item.setProdutoId(UUID.randomUUID());
        item.setQuantidade(QUANTITY);
        item.setCustoUnitario(CUSTO_UNITARIO);
        return item;
    }
}
