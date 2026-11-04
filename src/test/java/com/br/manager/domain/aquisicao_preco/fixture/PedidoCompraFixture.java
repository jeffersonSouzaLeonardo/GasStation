package com.br.manager.domain.aquisicao_preco.fixture;

import com.br.manager.domain.operacional.entity.Fornecedor;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.aquisicao_preco.dto.PedidoCompraInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemPedidoCompraInputDTO;
import com.br.manager.domain.aquisicao_preco.entity.PedidoCompra;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Pedido de Compra.
 */
public final class PedidoCompraFixture {

    public static final String NUMERO = "PC-2026-0001";
    public static final String ATUALIZADO_NUMERO = "PC-2026-0099";
    public static final LocalDateTime PEDIDO_EM = LocalDateTime.of(2026, 1, 5, 9, 30);
    public static final LocalDateTime PREVISTO_EM = LocalDateTime.of(2026, 1, 10, 14, 0);

    private PedidoCompraFixture() {
    }

    public static PedidoCompraInputDTO validoInput(UUID fornecedorId, UUID criadoPor, List<ItemPedidoCompraInputDTO> itens) {
        PedidoCompraInputDTO input = new PedidoCompraInputDTO();
        input.setFornecedorId(fornecedorId);
        input.setCriadoPor(criadoPor);
        input.setNumero(NUMERO);
        input.setPedidoEm(PEDIDO_EM);
        input.setPrevistoEm(PREVISTO_EM);
        input.setItens(itens);
        return input;
    }

    public static PedidoCompra persistedEntity(String status, Fornecedor fornecedor, Usuario criadoPor) {
        PedidoCompra pedidoCompra = new PedidoCompra();
        pedidoCompra.setId(UUID.randomUUID());
        pedidoCompra.setFornecedor(fornecedor);
        pedidoCompra.setCriadoPor(criadoPor);
        pedidoCompra.setNumero(NUMERO);
        pedidoCompra.setStatus(status);
        pedidoCompra.setPedidoEm(PEDIDO_EM);
        pedidoCompra.setPrevistoEm(PREVISTO_EM);
        return pedidoCompra;
    }

    public static PedidoCompra persistedEntity(String status) {
        return persistedEntity(status, AquisicaoPrecoReferenciaFixture.ativoFornecedor(), AquisicaoPrecoReferenciaFixture.ativoUsuario());
    }
}
