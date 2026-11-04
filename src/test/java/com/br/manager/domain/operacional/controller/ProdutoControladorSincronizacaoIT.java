package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.comum.entity.NomeTabelaSincronizacaoEnum;
import com.br.manager.domain.operacional.fixture.ProdutoFixture;
import com.br.manager.domain.operacional.support.OperacionalAIntegrationTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

class ProdutoControladorSincronizacaoIT extends OperacionalAIntegrationTest {

    private static final String PATH = "/produto-sincronizacao";
    private static final String TABLE = "produtos";
    private static final NomeTabelaSincronizacaoEnum SINCRONIZACAO_TABELA = NomeTabelaSincronizacaoEnum.PRODUTO;

    @Test
    void shouldReturnAllRecordsWhenTabelaWasNeverSincronizacaod() {
        String id = createProduto(ProdutoFixture.combustivelInput());

        request().when().get(PATH)
                .then().statusCode(200).body("$", hasSize(1)).body("[0].id", equalTo(id));
    }

    @Test
    void shouldReturnOnlyRecordsChangedSinceUltimaSincronizacao() {
        LocalDateTime now = LocalDateTime.now();
        String changed = createProduto(ProdutoFixture.combustivelInput());
        String unchanged = createProduto(ProdutoFixture.convenienciaInput());
        testData.backdate(TABLE, unchanged, now.minusDays(2));
        testData.setUltimaSincronizacao(SINCRONIZACAO_TABELA, now.minusDays(1));

        request().when().get(PATH)
                .then().statusCode(200).body("id", contains(changed));
    }

    @Test
    void shouldReturnRecordsExcluidoSinceUltimaSincronizacao() {
        LocalDateTime now = LocalDateTime.now();
        String excluido = createProduto(ProdutoFixture.combustivelInput());
        testData.backdate(TABLE, excluido, now.minusDays(2));
        testData.setUltimaSincronizacao(SINCRONIZACAO_TABELA, now.minusDays(1));
        delete(PRODUTOS, excluido);

        request().when().get(PATH)
                .then().statusCode(200).body("id", contains(excluido)).body("[0].ativo", equalTo(false));
    }

    @Test
    void shouldReturnEmptyWhenNothingChangedAfterUltimaSincronizacao() {
        createProduto(ProdutoFixture.combustivelInput());
        testData.setUltimaSincronizacao(SINCRONIZACAO_TABELA, LocalDateTime.now().plusDays(1));

        request().when().get(PATH)
                .then().statusCode(200).body("$", hasSize(0));
    }
}
