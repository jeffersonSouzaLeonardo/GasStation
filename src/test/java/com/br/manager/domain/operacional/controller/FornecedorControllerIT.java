package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.operacional.dto.FornecedorInputDTO;
import com.br.manager.domain.operacional.fixture.FornecedorFixture;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class FornecedorControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/api/fornecedores";

    @Test
    void shouldCreateFornecedorRemovingMasks() {
        request()
                .body(FornecedorFixture.validoInput())
        .when()
                .post(PATH)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("legalName", equalTo(FornecedorFixture.LEGAL_NAME))
                .body("cnpjCpf", equalTo(FornecedorFixture.CNPJ))
                .body("telefone", equalTo(FornecedorFixture.PHONE))
                .body("cep", equalTo(FornecedorFixture.CEP))
                .body("ativo", equalTo(true));
    }

    @Test
    void shouldReturnBadRequestWhenLegalNameIsMissing() {
        FornecedorInputDTO input = FornecedorFixture.validoInput();
        input.setLegalName(null);

        request().body(input).when().post(PATH).then().statusCode(400);
    }

    @Test
    void shouldListAtivoFornecedors() {
        createFornecedor(FornecedorFixture.validoInput());
        createFornecedor(FornecedorFixture.outrosInput());

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldFindFornecedorById() {
        String id = createFornecedor(FornecedorFixture.validoInput());

        request().when().get(PATH + "/{id}", id)
                .then().statusCode(200).body("id", equalTo(id)).body("legalName", equalTo(FornecedorFixture.LEGAL_NAME));
    }

    @Test
    void shouldBuscaFornecedorsByLegalName() {
        createFornecedor(FornecedorFixture.validoInput());
        createFornecedor(FornecedorFixture.outrosInput());

        request().queryParam("descricao", "lubrificantes").when().get(PATH + "/busca")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].legalName", equalTo(FornecedorFixture.OUTROS_LEGAL_NAME));
    }

    @Test
    void shouldReturnAllFornecedorsWhenBuscaDescricaoIsVazio() {
        createFornecedor(FornecedorFixture.validoInput());
        createFornecedor(FornecedorFixture.outrosInput());

        request().queryParam("descricao", " ").when().get(PATH + "/busca")
                .then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldReturnBadRequestWhenBuscaDescricaoIsMissing() {
        request().when().get(PATH + "/busca").then().statusCode(400);
    }

    @Test
    void shouldUpdateFornecedor() {
        String id = createFornecedor(FornecedorFixture.validoInput());
        FornecedorInputDTO update = FornecedorFixture.outrosInput();
        update.setId(UUID.fromString(id));

        request()
                .body(update)
        .when()
                .put(PATH)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("legalName", equalTo(FornecedorFixture.OUTROS_LEGAL_NAME))
                .body("cnpjCpf", equalTo(FornecedorFixture.OUTROS_CNPJ));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingWithoutLegalName() {
        String id = createFornecedor(FornecedorFixture.validoInput());
        FornecedorInputDTO update = FornecedorFixture.validoInput();
        update.setId(UUID.fromString(id));
        update.setLegalName(" ");

        request().body(update).when().put(PATH).then().statusCode(400);
    }

    @Test
    void shouldInactivateFornecedorOnDelete() {
        String id = createFornecedor(FornecedorFixture.validoInput());

        request().when().delete(PATH + "/{id}", id).then().statusCode(204);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
        request().when().get(PATH).then().statusCode(200).body("$", hasSize(0));
    }

    @Test
    void shouldReturnNotFoundForUnknownFornecedor() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
        request().when().delete(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);

        FornecedorInputDTO update = FornecedorFixture.validoInput();
        update.setId(UUID.randomUUID());
        request().body(update).when().put(PATH).then().statusCode(404);
    }

    private String createFornecedor(FornecedorInputDTO input) {
        return request().body(input).when().post(PATH).then().statusCode(201).extract().path("id");
    }
}
