package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.operacional.dto.TanqueInputDTO;
import com.br.manager.domain.operacional.fixture.TanqueFixture;
import com.br.manager.domain.operacional.support.OperacionalAIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class TanqueControllerIT extends OperacionalAIntegrationTest {

    @Test
    void shouldCreateTanqueForCombustivelProduto() {
        UUID produtoId = createCombustivelProduto();

        request()
                .body(TanqueFixture.validoInput(produtoId))
        .when()
                .post(TANQUES)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("codigo", equalTo(TanqueFixture.CODE))
                .body("produtoId", equalTo(produtoId.toString()))
                .body("capacidadeLitros", equalTo(TanqueFixture.CAPACIDADE.floatValue()))
                .body("estoqueMortoLitros", equalTo(TanqueFixture.MORTO_ESTOQUE.floatValue()))
                .body("saldoContabilLitros", equalTo(TanqueFixture.CURRENT_CONTABIL.floatValue()))
                .body("ativo", equalTo(true));
    }

    @Test
    void shouldReturnBadRequestWhenProdutoIsNotCombustivel() {
        request()
                .body(TanqueFixture.validoInput(createConvenienciaProduto()))
        .when()
                .post(TANQUES)
        .then()
                .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenCapacidadeIsMissing() {
        TanqueInputDTO input = TanqueFixture.validoInput(createCombustivelProduto());
        input.setCapacidadeLitros(null);

        request().body(input).when().post(TANQUES).then().statusCode(400);
    }

    @Test
    void shouldListAndFindTanques() {
        String id = createTanque(TanqueFixture.validoInput(createCombustivelProduto()));

        request().when().get(TANQUES).then().statusCode(200).body("$", hasSize(1));
        request().when().get(TANQUES + "/{id}", id).then().statusCode(200)
                .body("id", equalTo(id))
                .body("capacidadeLitros", equalTo(TanqueFixture.CAPACIDADE.floatValue()));
    }

    @Test
    void shouldBuscaTanquesByCode() {
        UUID produtoId = createCombustivelProduto();
        createTanque(TanqueFixture.validoInput(produtoId));
        createTanque(TanqueFixture.validoInput(produtoId, TanqueFixture.OUTROS_CODE));

        request().queryParam("descricao", TanqueFixture.OUTROS_CODE.toLowerCase()).when().get(TANQUES + "/busca")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].codigo", equalTo(TanqueFixture.OUTROS_CODE));
        request().queryParam("descricao", "").when().get(TANQUES + "/busca")
                .then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldUpdateTanque() {
        UUID produtoId = createCombustivelProduto();
        String id = createTanque(TanqueFixture.validoInput(produtoId));
        TanqueInputDTO update = TanqueFixture.validoInput(produtoId, TanqueFixture.OUTROS_CODE);
        update.setId(UUID.fromString(id));

        request()
                .body(update)
        .when()
                .put(TANQUES)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("codigo", equalTo(TanqueFixture.OUTROS_CODE));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingToNonCombustivelProduto() {
        String id = createTanque(TanqueFixture.validoInput(createCombustivelProduto()));
        TanqueInputDTO update = TanqueFixture.validoInput(createConvenienciaProduto());
        update.setId(UUID.fromString(id));

        request().body(update).when().put(TANQUES).then().statusCode(400);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingToUnknownProduto() {
        String id = createTanque(TanqueFixture.validoInput(createCombustivelProduto()));
        TanqueInputDTO update = TanqueFixture.validoInput(UUID.randomUUID());
        update.setId(UUID.fromString(id));

        request().body(update).when().put(TANQUES).then().statusCode(404);
    }

    @Test
    void shouldInactivateTanqueOnDelete() {
        String id = createTanque(TanqueFixture.validoInput(createCombustivelProduto()));

        request().when().delete(TANQUES + "/{id}", id).then().statusCode(204);

        request().when().get(TANQUES + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundForUnknownTanque() {
        request().when().get(TANQUES + "/{id}", UUID.randomUUID()).then().statusCode(404);
        request().when().delete(TANQUES + "/{id}", UUID.randomUUID()).then().statusCode(404);

        TanqueInputDTO update = TanqueFixture.validoInput(UUID.randomUUID());
        update.setId(UUID.randomUUID());
        request().body(update).when().put(TANQUES).then().statusCode(404);
    }
}
