package com.br.manager.domain.operacional.fixture;

import com.br.manager.domain.operacional.dto.EstacaoProdutoInputDTO;
import com.br.manager.domain.operacional.entity.EstacaoProduto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Produto do Posto.
 */
public final class EstacaoProdutoFixture {

    public static final BigDecimal MINIMO_ESTOQUE = new BigDecimal("500.25");
    public static final BigDecimal PONTO_REPOSICAO = new BigDecimal("1200.75");

    private EstacaoProdutoFixture() {
    }

    public static EstacaoProdutoInputDTO validoInput(UUID produtoId) {
        EstacaoProdutoInputDTO input = new EstacaoProdutoInputDTO();
        input.setProdutoId(produtoId);
        input.setEstoqueMinimo(MINIMO_ESTOQUE);
        input.setPontoReposicao(PONTO_REPOSICAO);
        input.setAtivo(true);
        return input;
    }

    public static EstacaoProduto persistedEntity() {
        EstacaoProduto entity = new EstacaoProduto();
        entity.setId(UUID.randomUUID());
        entity.setProdutoId(UUID.randomUUID());
        entity.setEstoqueMinimo(MINIMO_ESTOQUE);
        entity.setPontoReposicao(PONTO_REPOSICAO);
        entity.setAtivo(true);
        return entity;
    }
}
