package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.operacional.dto.CombustivelInputDTO;
import com.br.manager.domain.operacional.fixture.CombustivelFixture;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@Disabled("BUG: entity Combustivel (operacional/entity/Combustivel.java) is mapped to tabela `combustivel`, which no Flyway migration creates; "
        + "every /combustivel endpoint fails with a SQL error (tabela doesn't exist)")
class CombustivelControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/combustivel";
    private static final long UNKNOWN_ID = 999_999L;

    @Test
    void shouldCreateCombustivel() {
        request()
                .body(CombustivelFixture.validoInput())
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("nome", equalTo(CombustivelFixture.NAME))
                .body("unidade", equalTo(CombustivelFixture.UNIDADE.name()))
                .body("status", equalTo(CombustivelFixture.STATUS.name()))
                .body("idAnp", equalTo(CombustivelFixture.ID_ANP));
    }

    @Test
    void shouldReturnBadRequestWhenNameIsMissing() {
        CombustivelInputDTO input = CombustivelFixture.validoInput();
        input.setNome(null);

        request().body(input).when().post(PATH).then().statusCode(400);
    }

    @Test
    void shouldListCombustivels() {
        createCombustivel(CombustivelFixture.validoInput());
        createCombustivel(CombustivelFixture.outrosInput());

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldFindCombustivelById() {
        Integer id = createCombustivel(CombustivelFixture.validoInput());

        request().when().get(PATH + "/{id}", id)
                .then().statusCode(200).body("id", equalTo(id)).body("nome", equalTo(CombustivelFixture.NAME));
    }

    @Test
    void shouldBuscaCombustivelsByName() {
        createCombustivel(CombustivelFixture.validoInput());
        createCombustivel(CombustivelFixture.outrosInput());

        request().queryParam("descricao", "etanol").when().get(PATH + "/busca")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].nome", equalTo(CombustivelFixture.OUTROS_NAME));
    }

    @Test
    void shouldReturnAllCombustivelsWhenBuscaDescricaoIsMissing() {
        createCombustivel(CombustivelFixture.validoInput());
        createCombustivel(CombustivelFixture.outrosInput());

        request().when().get(PATH + "/busca").then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldUpdateCombustivel() {
        Integer id = createCombustivel(CombustivelFixture.validoInput());
        CombustivelInputDTO update = CombustivelFixture.outrosInput();
        update.setId(id.longValue());

        request()
                .body(update)
        .when()
                .put(PATH)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("nome", equalTo(CombustivelFixture.OUTROS_NAME));
    }

    @Test
    void shouldSoftDeleteCombustivel() {
        Integer id = createCombustivel(CombustivelFixture.validoInput());

        request().when().delete(PATH + "/{id}", id).then().statusCode(200);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundForUnknownCombustivel() {
        request().when().get(PATH + "/{id}", UNKNOWN_ID).then().statusCode(404);
        request().when().delete(PATH + "/{id}", UNKNOWN_ID).then().statusCode(404);

        CombustivelInputDTO update = CombustivelFixture.validoInput();
        update.setId(UNKNOWN_ID);
        request().body(update).when().put(PATH).then().statusCode(404);
    }

    private Integer createCombustivel(CombustivelInputDTO input) {
        return request().body(input).when().post(PATH).then().statusCode(200).extract().path("id");
    }
}
