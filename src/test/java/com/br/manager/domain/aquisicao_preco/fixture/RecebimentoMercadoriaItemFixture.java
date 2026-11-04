package com.br.manager.domain.aquisicao_preco.fixture;

import com.br.manager.domain.aquisicao_preco.dto.ItemRecebimentoMercadoriaInputDTO;
import com.br.manager.domain.aquisicao_preco.entity.RecebimentoMercadoria;
import com.br.manager.domain.aquisicao_preco.entity.RecebimentoMercadoriaItem;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Item de Recebimento de Mercadorias.
 */
public final class RecebimentoMercadoriaItemFixture {

    public static final BigDecimal QUANTITY = new BigDecimal("5000.0000");
    public static final BigDecimal QUANTIDADE_ATUALIZADA = new BigDecimal("4500.5000");
    public static final BigDecimal CUSTO_UNITARIO = new BigDecimal("5.1000");

    private RecebimentoMercadoriaItemFixture() {
    }

    public static ItemRecebimentoMercadoriaInputDTO validoInput(UUID mercadoriaReciboId, UUID produtoId, UUID tanqueId) {
        ItemRecebimentoMercadoriaInputDTO input = new ItemRecebimentoMercadoriaInputDTO();
        input.setMercadoriaReciboId(mercadoriaReciboId);
        input.setProdutoId(produtoId);
        input.setTanqueId(tanqueId);
        input.setQuantidade(QUANTITY);
        input.setCustoUnitario(CUSTO_UNITARIO);
        return input;
    }

    public static RecebimentoMercadoriaItem persistedEntity(RecebimentoMercadoria recebimentoMercadoria) {
        RecebimentoMercadoriaItem item = new RecebimentoMercadoriaItem();
        item.setId(UUID.randomUUID());
        item.setRecebimentoMercadoria(recebimentoMercadoria);
        item.setProdutoId(UUID.randomUUID());
        item.setTanqueId(UUID.randomUUID());
        item.setQuantidade(QUANTITY);
        item.setCustoUnitario(CUSTO_UNITARIO);
        return item;
    }
}
