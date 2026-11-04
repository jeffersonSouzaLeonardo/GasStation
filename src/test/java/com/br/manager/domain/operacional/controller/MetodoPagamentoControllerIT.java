package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.operacional.dto.MetodoPagamentoInputDTO;
import com.br.manager.domain.operacional.fixture.MetodoPagamentoFixture;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class MetodoPagamentoControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/api/metodos-pagamento";

    @Test
    void shouldCreateMetodoPagamento() {
        request()
                .body(MetodoPagamentoFixture.validoInput())
        .when()
                .post(PATH)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("nome", equalTo(MetodoPagamentoFixture.NAME))
                .body("tipo", equalTo(MetodoPagamentoFixture.TIPO.name()))
                .body("exigeReferencia", equalTo(true))
                .body("diasLiquidacao", equalTo(MetodoPagamentoFixture.LIQUIDACAO_DIAS))
                .body("ativo", equalTo(true));
    }

    @Test
    void shouldApplyDefaultsWhenOptionalFieldsAreOmitted() {
        request()
                .body(MetodoPagamentoFixture.outrosInput())
        .when()
                .post(PATH)
        .then()
                .statusCode(201)
                .body("exigeReferencia", equalTo(false))
                .body("diasLiquidacao", equalTo(0))
                .body("ativo", equalTo(true));
    }

    @Test
    void shouldReturnBadRequestWhenTipoIsMissing() {
        MetodoPagamentoInputDTO input = MetodoPagamentoFixture.validoInput();
        input.setTipo(null);

        request().body(input).when().post(PATH).then().statusCode(400);
    }

    @Test
    void shouldListAtivoMetodoPagamentos() {
        createMetodoPagamento(MetodoPagamentoFixture.validoInput());
        createMetodoPagamento(MetodoPagamentoFixture.outrosInput());

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldFindMetodoPagamentoById() {
        String id = createMetodoPagamento(MetodoPagamentoFixture.validoInput());

        request().when().get(PATH + "/{id}", id)
                .then().statusCode(200).body("id", equalTo(id)).body("nome", equalTo(MetodoPagamentoFixture.NAME));
    }

    @Test
    void shouldBuscaMetodoPagamentosByName() {
        createMetodoPagamento(MetodoPagamentoFixture.validoInput());
        createMetodoPagamento(MetodoPagamentoFixture.outrosInput());

        request().queryParam("descricao", "visa").when().get(PATH + "/busca")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].nome", equalTo(MetodoPagamentoFixture.NAME));
    }

    @Test
    void shouldReturnAllMetodoPagamentosWhenBuscaDescricaoIsVazio() {
        createMetodoPagamento(MetodoPagamentoFixture.validoInput());
        createMetodoPagamento(MetodoPagamentoFixture.outrosInput());

        request().queryParam("descricao", " ").when().get(PATH + "/busca")
                .then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldUpdateMetodoPagamento() {
        String id = createMetodoPagamento(MetodoPagamentoFixture.validoInput());
        MetodoPagamentoInputDTO update = MetodoPagamentoFixture.outrosInput();
        update.setId(UUID.fromString(id));

        request()
                .body(update)
        .when()
                .put(PATH)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("nome", equalTo(MetodoPagamentoFixture.OUTROS_NAME))
                .body("tipo", equalTo("DINHEIRO"));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingWithoutName() {
        String id = createMetodoPagamento(MetodoPagamentoFixture.validoInput());
        MetodoPagamentoInputDTO update = MetodoPagamentoFixture.validoInput();
        update.setId(UUID.fromString(id));
        update.setNome("");

        request().body(update).when().put(PATH).then().statusCode(400);
    }

    @Test
    void shouldInactivateMetodoPagamentoOnDelete() {
        String id = createMetodoPagamento(MetodoPagamentoFixture.validoInput());

        request().when().delete(PATH + "/{id}", id).then().statusCode(204);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundForUnknownMetodoPagamento() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
        request().when().delete(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);

        MetodoPagamentoInputDTO update = MetodoPagamentoFixture.validoInput();
        update.setId(UUID.randomUUID());
        request().body(update).when().put(PATH).then().statusCode(404);
    }

    private String createMetodoPagamento(MetodoPagamentoInputDTO input) {
        return request().body(input).when().post(PATH).then().statusCode(201).extract().path("id");
    }
}
