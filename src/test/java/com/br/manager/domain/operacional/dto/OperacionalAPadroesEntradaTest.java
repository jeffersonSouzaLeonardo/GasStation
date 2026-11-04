package com.br.manager.domain.operacional.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Valores padrao dos DTOs de entrada de Produto/Produto do Posto/Tanque/Bomba/Bico.
 */
class OperacionalAPadroesEntradaTest {

    @Test
    void shouldCreateAtivoProdutoByDefault() {
        assertThat(new ProdutoInputDTO().getAtivo()).isTrue();
    }

    @Test
    void shouldCreateAtivoEstacaoProdutoWithZeroEstoqueLevelsByDefault() {
        EstacaoProdutoInputDTO input = new EstacaoProdutoInputDTO();

        assertThat(input.getAtivo()).isTrue();
        assertThat(input.getEstoqueMinimo()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(input.getPontoReposicao()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldCreateAtivoTanqueByDefault() {
        assertThat(new TanqueInputDTO().getAtivo()).isTrue();
    }

    @Test
    void shouldCreateAtivoBombaByDefault() {
        assertThat(new BombaInputDTO().getAtivo()).isTrue();
    }

    @Test
    void shouldCreateAtivoBicoWithZeroMedidorByDefault() {
        BicoInputDTO input = new BicoInputDTO();

        assertThat(input.getAtivo()).isTrue();
        assertThat(input.getNumeroMedidor()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
