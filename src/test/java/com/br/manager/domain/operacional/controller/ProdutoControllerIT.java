package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.operacional.dto.ProdutoInputDTO;
import com.br.manager.domain.operacional.fixture.ProdutoFixture;
import com.br.manager.domain.operacional.support.OperacionalAIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class ProdutoControllerIT extends OperacionalAIntegrationTest {

    @Test
    void shouldCreateProduto() {
        request()
                .body(ProdutoFixture.combustivelInput())
        .when()
                .post(PRODUTOS)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("sku", equalTo(ProdutoFixture.COMBUSTIVEL_SKU))
                .body("nome", equalTo(ProdutoFixture.COMBUSTIVEL_NAME))
                .body("produtoType", equalTo("COMBUSTIVEL"))
                .body("unidade", equalTo("L"))
                .body("anpCode", equalTo(ProdutoFixture.ANP_CODE))
                .body("ativo", equalTo(true));
    }

    @Test
    void shouldReturnBadRequestWhenNameIsMissing() {
        request()
                .body(ProdutoFixture.inputWithoutName())
        .when()
                .post(PRODUTOS)
        .then()
                .statusCode(400);
    }

    @Test
    void shouldListAndFindProdutos() {
        String id = createProduto(ProdutoFixture.combustivelInput());

        request().when().get(PRODUTOS).then().statusCode(200).body("$", hasSize(1));
        request().when().get(PRODUTOS + "/{id}", id).then().statusCode(200).body("id", equalTo(id));
    }

    @Test
    void shouldListOnlyCombustivelProdutos() {
        createProduto(ProdutoFixture.combustivelInput());
        createProduto(ProdutoFixture.convenienciaInput());

        request().when().get(PRODUTOS + "/combustivel")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].produtoType", equalTo("COMBUSTIVEL"));
    }

    @Test
    void shouldBuscaProdutosByName() {
        createProduto(ProdutoFixture.combustivelInput());
        createProduto(ProdutoFixture.convenienciaInput());

        request().queryParam("descricao", "refrigerante").when().get(PRODUTOS + "/busca")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].nome", equalTo(ProdutoFixture.CONVENIENCIA_NAME));
        request().queryParam("descricao", "").when().get(PRODUTOS + "/busca")
                .then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldUpdateProduto() {
        String id = createProduto(ProdutoFixture.combustivelInput());
        ProdutoInputDTO update = ProdutoFixture.combustivelInput();
        update.setId(UUID.fromString(id));
        update.setNome("Gasolina Aditivada");

        request()
                .body(update)
        .when()
                .put(PRODUTOS)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("nome", equalTo("Gasolina Aditivada"));
    }

    @Test
    void shouldInactivateProdutoOnDelete() {
        String id = createProduto(ProdutoFixture.combustivelInput());

        request().when().delete(PRODUTOS + "/{id}", id).then().statusCode(204);

        request().when().get(PRODUTOS + "/{id}", id).then().statusCode(404);
        request().when().get(PRODUTOS).then().statusCode(200).body("$", hasSize(0));
    }

    @Test
    void shouldReturnNotFoundForUnknownProduto() {
        request().when().get(PRODUTOS + "/{id}", UUID.randomUUID()).then().statusCode(404);
        request().when().delete(PRODUTOS + "/{id}", UUID.randomUUID()).then().statusCode(404);

        ProdutoInputDTO update = ProdutoFixture.combustivelInput();
        update.setId(UUID.randomUUID());
        request().body(update).when().put(PRODUTOS).then().statusCode(404);
    }
}
