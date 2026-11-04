package com.br.manager.domain.organizacao.controller;

import com.br.manager.domain.organizacao.dto.EstacaoInputDTO;
import com.br.manager.domain.organizacao.fixture.EmpresaFixture;
import com.br.manager.domain.organizacao.fixture.EstacaoFixture;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class EstacaoControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/estacao";
    private static final String OUTROS_NAME = "Posto Rodovia";

    @Test
    void shouldCreateEstacao() {
        UUID empresaId = createEmpresa();

        request()
                .body(EstacaoFixture.validoInput(empresaId))
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("empresaId", equalTo(empresaId.toString()))
                .body("nome", equalTo(EstacaoFixture.NAME))
                .body("cnpj", equalTo(EstacaoFixture.CNPJ))
                .body("cep", equalTo(EstacaoFixture.CEP))
                .body("fusoHorario", equalTo(EstacaoFixture.FusoHorario))
                .body("ativo", equalTo(true));
    }

    @Test
    void shouldApplyDefaultTimezoneWhenNotInformed() {
        EstacaoInputDTO input = EstacaoFixture.validoInput(createEmpresa());
        input.setFusoHorario(null);

        request().body(input).when().post(PATH)
                .then().statusCode(200).body("fusoHorario", equalTo(EstacaoFixture.FusoHorario));
    }

    @Test
    void shouldReturnBadRequestWhenEmpresaDoesNotExist() {
        request()
                .body(EstacaoFixture.validoInput(UUID.randomUUID()))
        .when()
                .post(PATH)
        .then()
                .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenNameIsMissing() {
        EstacaoInputDTO input = EstacaoFixture.validoInput(createEmpresa());
        input.setNome(null);

        request().body(input).when().post(PATH).then().statusCode(400);
    }

    @Test
    void shouldListAndFindEstacaos() {
        String id = createEstacao(EstacaoFixture.validoInput(createEmpresa()));

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(1));
        request().when().get(PATH + "/{id}", id).then().statusCode(200).body("id", equalTo(id));
    }

    @Test
    void shouldBuscaEstacaosByName() {
        UUID empresaId = createEmpresa();
        createEstacao(EstacaoFixture.validoInput(empresaId));
        createEstacao(outrosEstacao(empresaId));

        request().queryParam("nome", "rodovia").when().get(PATH + "/busca")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].nome", equalTo(OUTROS_NAME));
        request().queryParam("descricao", "centro").when().get(PATH + "/busca")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].nome", equalTo(EstacaoFixture.NAME));
        request().when().get(PATH + "/busca")
                .then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldUpdateEstacao() {
        UUID empresaId = createEmpresa();
        String id = createEstacao(EstacaoFixture.validoInput(empresaId));
        EstacaoInputDTO update = EstacaoFixture.validoInput(empresaId);
        update.setId(UUID.fromString(id));
        update.setNome(OUTROS_NAME);
        update.setFusoHorario(EstacaoFixture.OUTROS_TIMEZONE);

        request()
                .body(update)
        .when()
                .put(PATH)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("nome", equalTo(OUTROS_NAME))
                .body("fusoHorario", equalTo(EstacaoFixture.OUTROS_TIMEZONE));
    }

    @Test
    void shouldInactivateEstacaoOnDelete() {
        String id = createEstacao(EstacaoFixture.validoInput(createEmpresa()));

        request().when().delete(PATH + "/{id}", id).then().statusCode(200);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
        request().when().get(PATH).then().statusCode(200).body("$", hasSize(0));
    }

    @Test
    void shouldReturnNotFoundForUnknownEstacao() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
        request().when().delete(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);

        EstacaoInputDTO update = EstacaoFixture.validoInput();
        update.setId(UUID.randomUUID());
        request().body(update).when().put(PATH).then().statusCode(404);
    }

    private UUID createEmpresa() {
        String id = request().body(EmpresaFixture.validoInput()).when().post("/empresa")
                .then().statusCode(200).extract().path("id");
        return UUID.fromString(id);
    }

    private String createEstacao(EstacaoInputDTO input) {
        return request().body(input).when().post(PATH).then().statusCode(200).extract().path("id");
    }

    private EstacaoInputDTO outrosEstacao(UUID empresaId) {
        EstacaoInputDTO input = EstacaoFixture.validoInput(empresaId);
        input.setNome(OUTROS_NAME);
        return input;
    }
}
