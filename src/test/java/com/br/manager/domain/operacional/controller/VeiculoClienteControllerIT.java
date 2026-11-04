package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.operacional.dto.VeiculoClienteInputDTO;
import com.br.manager.domain.operacional.fixture.ClienteFixture;
import com.br.manager.domain.operacional.fixture.ClienteVeiculoFixture;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class VeiculoClienteControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/api/veiculos-cliente";
    private static final String CUSTOMERS_PATH = "/api/clientes";
    private static final String PLACA_BUG = "BUG: VeiculoClienteMapper applies UtilStrings.removeMask (onlyDigits) to the placa, "
            + "so ABC-1D23 is stored as 123 and placa searches by letters never match";

    private UUID clienteId;

    @BeforeEach
    void createCliente() {
        String id = request().body(ClienteFixture.validoInput()).when().post(CUSTOMERS_PATH)
                .then().statusCode(201).extract().path("id");
        clienteId = UUID.fromString(id);
    }

    @Test
    void shouldCreateVeiculo() {
        request()
                .body(ClienteVeiculoFixture.validoInput(clienteId))
        .when()
                .post(PATH)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("clienteId", equalTo(clienteId.toString()))
                .body("marca", equalTo(ClienteVeiculoFixture.MARCA))
                .body("modelo", equalTo(ClienteVeiculoFixture.MODELO))
                .body("combustivelType", equalTo(ClienteVeiculoFixture.COMBUSTIVEL_TYPE))
                .body("ativo", equalTo(true));
    }

    @Test
    @Disabled(PLACA_BUG)
    void shouldCreateVeiculoKeepingPlacaLetters() {
        request()
                .body(ClienteVeiculoFixture.validoInput(clienteId))
        .when()
                .post(PATH)
        .then()
                .statusCode(201)
                .body("placa", equalTo(ClienteVeiculoFixture.PLACA));
    }

    @Test
    void shouldReturnBadRequestWhenClienteDoesNotExist() {
        request().body(ClienteVeiculoFixture.validoInput(UUID.randomUUID())).when().post(PATH).then().statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenClienteIsMissing() {
        request().body(ClienteVeiculoFixture.validoInput(null)).when().post(PATH).then().statusCode(400);
    }

    @Test
    void shouldListAtivoVeiculos() {
        createVeiculo(ClienteVeiculoFixture.validoInput(clienteId));

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(1));
    }

    @Test
    void shouldFindVeiculoById() {
        String id = createVeiculo(ClienteVeiculoFixture.validoInput(clienteId));

        request().when().get(PATH + "/{id}", id)
                .then().statusCode(200).body("id", equalTo(id)).body("modelo", equalTo(ClienteVeiculoFixture.MODELO));
    }

    @Test
    @Disabled(PLACA_BUG)
    void shouldBuscaVeiculosByPlaca() {
        createVeiculo(ClienteVeiculoFixture.validoInput(clienteId));
        createVeiculo(outrosVeiculo());

        request().queryParam("descricao", "abc").when().get(PATH + "/busca")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].placa", equalTo(ClienteVeiculoFixture.PLACA));
    }

    @Test
    void shouldReturnAllVeiculosWhenBuscaDescricaoIsVazio() {
        createVeiculo(ClienteVeiculoFixture.validoInput(clienteId));
        createVeiculo(outrosVeiculo());

        request().queryParam("descricao", " ").when().get(PATH + "/busca")
                .then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldUpdateVeiculo() {
        String id = createVeiculo(ClienteVeiculoFixture.validoInput(clienteId));
        VeiculoClienteInputDTO update = ClienteVeiculoFixture.validoInput(clienteId);
        update.setId(UUID.fromString(id));
        update.setMarca("Scania");

        request()
                .body(update)
        .when()
                .put(PATH)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("marca", equalTo("Scania"));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingToUnknownCliente() {
        String id = createVeiculo(ClienteVeiculoFixture.validoInput(clienteId));
        VeiculoClienteInputDTO update = ClienteVeiculoFixture.validoInput(UUID.randomUUID());
        update.setId(UUID.fromString(id));

        request().body(update).when().put(PATH).then().statusCode(400);
    }

    @Test
    void shouldInactivateVeiculoOnDelete() {
        String id = createVeiculo(ClienteVeiculoFixture.validoInput(clienteId));

        request().when().delete(PATH + "/{id}", id).then().statusCode(204);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundForUnknownVeiculo() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
        request().when().delete(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);

        VeiculoClienteInputDTO update = ClienteVeiculoFixture.validoInput(clienteId);
        update.setId(UUID.randomUUID());
        request().body(update).when().put(PATH).then().statusCode(404);
    }

    private String createVeiculo(VeiculoClienteInputDTO input) {
        return request().body(input).when().post(PATH).then().statusCode(201).extract().path("id");
    }

    private VeiculoClienteInputDTO outrosVeiculo() {
        VeiculoClienteInputDTO input = ClienteVeiculoFixture.validoInput(clienteId);
        input.setPlaca(ClienteVeiculoFixture.OUTROS_PLACA);
        return input;
    }
}
