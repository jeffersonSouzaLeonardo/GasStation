package com.br.manager.domain.organization.controller;

import com.br.manager.domain.organization.dto.CompanyInputDTO;
import com.br.manager.domain.organization.fixture.CompanyFixture;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class CompanyControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/company";

    @Test
    void shouldCreateCompany() {
        request()
                .body(CompanyFixture.validInput())
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("legalName", equalTo(CompanyFixture.LEGAL_NAME))
                .body("cnpj", equalTo(CompanyFixture.CNPJ))
                .body("phone", equalTo(CompanyFixture.PHONE))
                .body("active", equalTo(true));
    }

    @Test
    void shouldReturnBadRequestWhenCnpjIsDuplicated() {
        createCompany(CompanyFixture.validInput());

        request()
                .body(CompanyFixture.validInput())
        .when()
                .post(PATH)
        .then()
                .statusCode(400);
    }

    @Test
    void shouldListAndFindCompanies() {
        String id = createCompany(CompanyFixture.validInput());

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(1));
        request().when().get(PATH + "/{id}", id).then().statusCode(200).body("id", equalTo(id));
    }

    @Test
    void shouldSearchCompaniesByName() {
        createCompany(CompanyFixture.validInput());
        createCompany(otherCompany());

        request().queryParam("name", "outra").when().get(PATH + "/search")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].legalName", equalTo("Outra Empresa SA"));
        request().when().get(PATH + "/search")
                .then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldUpdateCompany() {
        String id = createCompany(CompanyFixture.validInput());
        CompanyInputDTO update = CompanyFixture.validInput();
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
    void shouldInactivateCompanyOnDelete() {
        String id = createCompany(CompanyFixture.validInput());

        request().when().delete(PATH + "/{id}", id).then().statusCode(200);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundForUnknownCompany() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
        request().when().delete(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);

        CompanyInputDTO update = CompanyFixture.validInput();
        update.setId(UUID.randomUUID());
        request().body(update).when().put(PATH).then().statusCode(404);
    }

    private String createCompany(CompanyInputDTO input) {
        return request().body(input).when().post(PATH).then().statusCode(200).extract().path("id");
    }

    private CompanyInputDTO otherCompany() {
        CompanyInputDTO input = CompanyFixture.validInput("98.765.432/0001-10");
        input.setLegalName("Outra Empresa SA");
        return input;
    }
}
