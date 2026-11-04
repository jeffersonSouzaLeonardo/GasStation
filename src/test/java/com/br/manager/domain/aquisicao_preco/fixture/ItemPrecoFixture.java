package com.br.manager.domain.aquisicao_preco.fixture;

import com.br.manager.domain.aquisicao_preco.dto.ItemPrecoInputDTO;
import com.br.manager.domain.aquisicao_preco.entity.ItemPreco;
import com.br.manager.domain.aquisicao_preco.entity.TabelaPreco;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Item de Preco.
 */
public final class ItemPrecoFixture {

    public static final BigDecimal PRECO_UNITARIO = new BigDecimal("5.8900");
    public static final BigDecimal PRECO_UNITARIO_ATUALIZADO = new BigDecimal("6.1900");
    public static final BigDecimal PRECO_MINIMO = new BigDecimal("5.5000");
    public static final BigDecimal DESCONTO_MAXIMO_PERCENTUAL = new BigDecimal("3.00");

    private ItemPrecoFixture() {
    }

    public static ItemPrecoInputDTO validoInput(UUID precoTabelaId, UUID produtoId) {
        ItemPrecoInputDTO input = new ItemPrecoInputDTO();
        input.setPrecoTabelaId(precoTabelaId);
        input.setProdutoId(produtoId);
        input.setPrecoUnitario(PRECO_UNITARIO);
        input.setPrecoMinimo(PRECO_MINIMO);
        input.setDescontoMaximoPercentual(DESCONTO_MAXIMO_PERCENTUAL);
        input.setAtivo(true);
        return input;
    }

    public static ItemPreco persistedEntity(TabelaPreco tabelaPreco) {
        ItemPreco itemPreco = new ItemPreco();
        itemPreco.setId(UUID.randomUUID());
        itemPreco.setTabelaPreco(tabelaPreco);
        itemPreco.setProdutoId(UUID.randomUUID());
        itemPreco.setBicoId(UUID.randomUUID());
        itemPreco.setPrecoUnitario(PRECO_UNITARIO);
        itemPreco.setPrecoMinimo(PRECO_MINIMO);
        itemPreco.setDescontoMaximoPercentual(DESCONTO_MAXIMO_PERCENTUAL);
        itemPreco.setAtivo(true);
        return itemPreco;
    }
}
