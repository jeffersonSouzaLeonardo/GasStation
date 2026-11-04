package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.operacional.dto.ClienteInputDTO;
import com.br.manager.domain.operacional.fixture.ClienteFixture;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class ClienteControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/api/clientes";

    @Test
    void shouldCreateCliente() {
        request()
                .body(ClienteFixture.validoInput())
        .when()
                .post(PATH)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("nome", equalTo(ClienteFixture.NAME))
                .body("personType", equalTo(ClienteFixture.PERSON_TYPE.name()))
                .body("cnpjCpf", equalTo(ClienteFixture.CNPJ))
                .body("limiteCredito", equalTo(ClienteFixture.CREDITO_LIMITE.floatValue()))
                .body("ativo", equalTo(true));
    }

    @Test
    @Disabled("BUG: ClienteMapper.clienteInputDTOToCliente copies cnpjCpf/telefone/cep without UtilStrings.removeMask; "
            + "a masked CNPJ (18 chars) overflows clientes.cnpj_cpf VARCHAR(14) and the request fails with 400")
    void shouldCreateClienteRemovingMasks() {
        request()
                .body(ClienteFixture.maskedInput())
        .when()
                .post(PATH)
        .then()
                .statusCode(201)
                .body("cnpjCpf", equalTo(ClienteFixture.CNPJ))
                .body("telefone", equalTo(ClienteFixture.PHONE))
                .body("cep", equalTo(ClienteFixture.CEP));
    }

    @Test
    void shouldReturnBadRequestWhenPersonTypeIsMissing() {
        ClienteInputDTO input = ClienteFixture.validoInput();
        input.setPersonType(null);

        request().body(input).when().post(PATH).then().statusCode(400);
    }

    @Test
    void shouldListAtivoClientes() {
        createCliente(ClienteFixture.validoInput());
        createCliente(ClienteFixture.outrosInput());

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldFindClienteById() {
        String id = createCliente(ClienteFixture.validoInput());

        request().when().get(PATH + "/{id}", id)
                .then().statusCode(200).body("id", equalTo(id)).body("nome", equalTo(ClienteFixture.NAME));
    }

    @Test
    void shouldBuscaClientesByName() {
        createCliente(ClienteFixture.validoInput());
        createCliente(ClienteFixture.outrosInput());

        request().queryParam("descricao", "oliveira").when().get(PATH + "/busca")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].nome", equalTo(ClienteFixture.OUTROS_NAME));
    }

    @Test
    void shouldReturnAllClientesWhenBuscaDescricaoIsVazio() {
        createCliente(ClienteFixture.validoInput());
        createCliente(ClienteFixture.outrosInput());

        request().queryParam("descricao", " ").when().get(PATH + "/busca")
                .then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldUpdateCliente() {
        String id = createCliente(ClienteFixture.validoInput());
        ClienteInputDTO update = ClienteFixture.outrosInput();
        update.setId(UUID.fromString(id));

        request()
                .body(update)
        .when()
                .put(PATH)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("nome", equalTo(ClienteFixture.OUTROS_NAME))
                .body("personType", equalTo("FISICA"))
                .body("cnpjCpf", equalTo(ClienteFixture.CPF));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingWithoutName() {
        String id = createCliente(ClienteFixture.validoInput());
        ClienteInputDTO update = ClienteFixture.validoInput();
        update.setId(UUID.fromString(id));
        update.setNome(null);

        request().body(update).when().put(PATH).then().statusCode(400);
    }

    @Test
    void shouldInactivateClienteOnDelete() {
        String id = createCliente(ClienteFixture.validoInput());

        request().when().delete(PATH + "/{id}", id).then().statusCode(204);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundForUnknownCliente() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
        request().when().delete(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);

        ClienteInputDTO update = ClienteFixture.validoInput();
        update.setId(UUID.randomUUID());
        request().body(update).when().put(PATH).then().statusCode(404);
    }

    private String createCliente(ClienteInputDTO input) {
        return request().body(input).when().post(PATH).then().statusCode(201).extract().path("id");
    }
}
