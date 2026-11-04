package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.operacional.dto.BombaInputDTO;
import com.br.manager.domain.operacional.fixture.BombaFixture;
import com.br.manager.domain.operacional.support.OperacionalAIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class BombaControllerIT extends OperacionalAIntegrationTest {

    @Test
    void shouldCreateBomba() {
        request()
                .body(BombaFixture.validoInput())
        .when()
                .post(BOMBAS)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("codigo", equalTo(BombaFixture.CODE))
                .body("localizacao", equalTo(BombaFixture.LOCATION))
                .body("ativo", equalTo(true));
    }

    @Test
    void shouldReturnBadRequestWhenCodeIsTooLong() {
        request()
                .body(BombaFixture.validoInput(BombaFixture.TOO_LONG_CODE))
        .when()
                .post(BOMBAS)
        .then()
                .statusCode(400);
    }

    @Test
    void shouldListAndFindBombas() {
        String id = createBomba(BombaFixture.validoInput());

        request().when().get(BOMBAS).then().statusCode(200).body("$", hasSize(1));
        request().when().get(BOMBAS + "/{id}", id).then().statusCode(200).body("id", equalTo(id));
    }

    @Test
    void shouldBuscaBombasByCode() {
        createBomba(BombaFixture.validoInput());
        createBomba(BombaFixture.validoInput(BombaFixture.OUTROS_CODE));

        request().queryParam("descricao", BombaFixture.OUTROS_CODE.toLowerCase()).when().get(BOMBAS + "/busca")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].codigo", equalTo(BombaFixture.OUTROS_CODE));
        request().queryParam("descricao", "").when().get(BOMBAS + "/busca")
                .then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldUpdateBomba() {
        String id = createBomba(BombaFixture.validoInput());
        BombaInputDTO update = BombaFixture.validoInput(BombaFixture.OUTROS_CODE);
        update.setId(UUID.fromString(id));

        request()
                .body(update)
        .when()
                .put(BOMBAS)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("codigo", equalTo(BombaFixture.OUTROS_CODE));
    }

    @Test
    void shouldInactivateBombaOnDelete() {
        String id = createBomba(BombaFixture.validoInput());

        request().when().delete(BOMBAS + "/{id}", id).then().statusCode(204);

        request().when().get(BOMBAS + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundForUnknownBomba() {
        request().when().get(BOMBAS + "/{id}", UUID.randomUUID()).then().statusCode(404);
        request().when().delete(BOMBAS + "/{id}", UUID.randomUUID()).then().statusCode(404);

        BombaInputDTO update = BombaFixture.validoInput();
        update.setId(UUID.randomUUID());
        request().body(update).when().put(BOMBAS).then().statusCode(404);
    }
}
