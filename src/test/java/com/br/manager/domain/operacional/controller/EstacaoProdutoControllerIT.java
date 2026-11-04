package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.operacional.dto.EstacaoProdutoInputDTO;
import com.br.manager.domain.operacional.fixture.EstacaoProdutoFixture;
import com.br.manager.domain.operacional.support.OperacionalAIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class EstacaoProdutoControllerIT extends OperacionalAIntegrationTest {

    @Test
    void shouldCreateEstacaoProduto() {
        UUID produtoId = createCombustivelProduto();

        request()
                .body(EstacaoProdutoFixture.validoInput(produtoId))
        .when()
                .post(PRODUTOS_ESTACAO)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("produtoId", equalTo(produtoId.toString()))
                .body("estoqueMinimo", equalTo(EstacaoProdutoFixture.MINIMO_ESTOQUE.floatValue()))
                .body("pontoReposicao", equalTo(EstacaoProdutoFixture.PONTO_REPOSICAO.floatValue()))
                .body("ativo", equalTo(true));
    }

    @Test
    void shouldReturnBadRequestWhenProdutoDoesNotExist() {
        request()
                .body(EstacaoProdutoFixture.validoInput(UUID.randomUUID()))
        .when()
                .post(PRODUTOS_ESTACAO)
        .then()
                .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenProdutoIsMissing() {
        request()
                .body(EstacaoProdutoFixture.validoInput(null))
        .when()
                .post(PRODUTOS_ESTACAO)
        .then()
                .statusCode(400);
    }

    @Test
    void shouldListAndFindEstacaoProdutos() {
        String id = create(PRODUTOS_ESTACAO, EstacaoProdutoFixture.validoInput(createCombustivelProduto()));

        request().when().get(PRODUTOS_ESTACAO).then().statusCode(200).body("$", hasSize(1));
        request().when().get(PRODUTOS_ESTACAO + "/{id}", id).then().statusCode(200)
                .body("id", equalTo(id))
                .body("estoqueMinimo", equalTo(EstacaoProdutoFixture.MINIMO_ESTOQUE.floatValue()));
    }

    @Test
    void shouldBuscaEstacaoProdutosByProdutoId() {
        UUID combustivelId = createCombustivelProduto();
        create(PRODUTOS_ESTACAO, EstacaoProdutoFixture.validoInput(combustivelId));
        create(PRODUTOS_ESTACAO, EstacaoProdutoFixture.validoInput(createConvenienciaProduto()));

        request().queryParam("descricao", combustivelId.toString()).when().get(PRODUTOS_ESTACAO + "/busca")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].produtoId", equalTo(combustivelId.toString()));
        request().queryParam("descricao", "nao-e-uuid").when().get(PRODUTOS_ESTACAO + "/busca")
                .then().statusCode(200).body("$", hasSize(0));
        request().queryParam("descricao", "").when().get(PRODUTOS_ESTACAO + "/busca")
                .then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldUpdateEstacaoProduto() {
        UUID produtoId = createCombustivelProduto();
        String id = create(PRODUTOS_ESTACAO, EstacaoProdutoFixture.validoInput(produtoId));
        EstacaoProdutoInputDTO update = EstacaoProdutoFixture.validoInput(produtoId);
        update.setId(UUID.fromString(id));
        update.setEstoqueMinimo(EstacaoProdutoFixture.PONTO_REPOSICAO);

        request()
                .body(update)
        .when()
                .put(PRODUTOS_ESTACAO)
        .then()
                .statusCode(200)
                .body("estoqueMinimo", equalTo(EstacaoProdutoFixture.PONTO_REPOSICAO.floatValue()));
    }

    @Test
    void shouldInactivateEstacaoProdutoOnDelete() {
        String id = create(PRODUTOS_ESTACAO, EstacaoProdutoFixture.validoInput(createCombustivelProduto()));

        request().when().delete(PRODUTOS_ESTACAO + "/{id}", id).then().statusCode(204);

        request().when().get(PRODUTOS_ESTACAO + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundForUnknownEstacaoProduto() {
        request().when().get(PRODUTOS_ESTACAO + "/{id}", UUID.randomUUID()).then().statusCode(404);
        request().when().delete(PRODUTOS_ESTACAO + "/{id}", UUID.randomUUID()).then().statusCode(404);

        EstacaoProdutoInputDTO update = EstacaoProdutoFixture.validoInput(UUID.randomUUID());
        update.setId(UUID.randomUUID());
        request().body(update).when().put(PRODUTOS_ESTACAO).then().statusCode(404);
    }
}
