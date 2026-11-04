package com.br.manager.domain.organizacao.controller;

import com.br.manager.domain.organizacao.dto.EmpresaInputDTO;
import com.br.manager.domain.organizacao.fixture.EmpresaFixture;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class EmpresaControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/empresa";

    @Test
    void shouldCreateEmpresa() {
        request()
                .body(EmpresaFixture.validoInput())
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("legalName", equalTo(EmpresaFixture.LEGAL_NAME))
                .body("cnpj", equalTo(EmpresaFixture.CNPJ))
                .body("telefone", equalTo(EmpresaFixture.PHONE))
                .body("ativo", equalTo(true));
    }

    @Test
    void shouldReturnBadRequestWhenCnpjIsDuplicated() {
        createEmpresa(EmpresaFixture.validoInput());

        request()
                .body(EmpresaFixture.validoInput())
        .when()
                .post(PATH)
        .then()
                .statusCode(400);
    }

    @Test
    void shouldListAndFindEmpresas() {
        String id = createEmpresa(EmpresaFixture.validoInput());

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(1));
        request().when().get(PATH + "/{id}", id).then().statusCode(200).body("id", equalTo(id));
    }

    @Test
    void shouldBuscaEmpresasByName() {
        createEmpresa(EmpresaFixture.validoInput());
        createEmpresa(outrosEmpresa());

        request().queryParam("nome", "outra").when().get(PATH + "/busca")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].legalName", equalTo("Outra Empresa SA"));
        request().when().get(PATH + "/busca")
                .then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldUpdateEmpresa() {
        String id = createEmpresa(EmpresaFixture.validoInput());
        EmpresaInputDTO update = EmpresaFixture.validoInput();
        update.setId(UUID.fromString(id));
        update.setTradeName("Nome Fantasia Novo");

        request()
                .body(update)
        .when()
                .put(PATH)
        .then()
                .statusCode(200)
                .body("tradeName", equalTo("Nome Fantasia Novo"));
    }

    @Test
    void shouldInactivateEmpresaOnDelete() {
        String id = createEmpresa(EmpresaFixture.validoInput());

        request().when().delete(PATH + "/{id}", id).then().statusCode(200);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundForUnknownEmpresa() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
        request().when().delete(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);

        EmpresaInputDTO update = EmpresaFixture.validoInput();
        update.setId(UUID.randomUUID());
        request().body(update).when().put(PATH).then().statusCode(404);
    }

    private String createEmpresa(EmpresaInputDTO input) {
        return request().body(input).when().post(PATH).then().statusCode(200).extract().path("id");
    }

    private EmpresaInputDTO outrosEmpresa() {
        EmpresaInputDTO input = EmpresaFixture.validoInput("98.765.432/0001-10");
        input.setLegalName("Outra Empresa SA");
        return input;
    }
}
