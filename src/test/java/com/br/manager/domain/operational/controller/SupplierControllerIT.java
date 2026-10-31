package com.br.manager.domain.operational.controller;

import com.br.manager.domain.operational.dto.SupplierInputDTO;
import com.br.manager.domain.operational.fixture.SupplierFixture;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class SupplierControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/api/suppliers";

    @Test
    void shouldCreateSupplierRemovingMasks() {
        request()
                .body(SupplierFixture.validInput())
        .when()
                .post(PATH)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("legalName", equalTo(SupplierFixture.LEGAL_NAME))
                .body("cnpjCpf", equalTo(SupplierFixture.CNPJ))
                .body("phone", equalTo(SupplierFixture.PHONE))
                .body("addressZipCode", equalTo(SupplierFixture.ZIP_CODE))
                .body("active", equalTo(true));
    }

    @Test
    void shouldReturnBadRequestWhenLegalNameIsMissing() {
        SupplierInputDTO input = SupplierFixture.validInput();
        input.setLegalName(null);

        request().body(input).when().post(PATH).then().statusCode(400);
    }

    @Test
    void shouldListActiveSuppliers() {
        createSupplier(SupplierFixture.validInput());
        createSupplier(SupplierFixture.otherInput());

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldFindSupplierById() {
        String id = createSupplier(SupplierFixture.validInput());

        request().when().get(PATH + "/{id}", id)
                .then().statusCode(200).body("id", equalTo(id)).body("legalName", equalTo(SupplierFixture.LEGAL_NAME));
    }

    @Test
    void shouldSearchSuppliersByLegalName() {
        createSupplier(SupplierFixture.validInput());
        createSupplier(SupplierFixture.otherInput());

        request().queryParam("description", "lubrificantes").when().get(PATH + "/search")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].legalName", equalTo(SupplierFixture.OTHER_LEGAL_NAME));
    }

    @Test
    void shouldReturnAllSuppliersWhenSearchDescriptionIsBlank() {
        createSupplier(SupplierFixture.validInput());
        createSupplier(SupplierFixture.otherInput());

        request().queryParam("description", " ").when().get(PATH + "/search")
                .then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldReturnBadRequestWhenSearchDescriptionIsMissing() {
        request().when().get(PATH + "/search").then().statusCode(400);
    }

    @Test
    void shouldUpdateSupplier() {
        String id = createSupplier(SupplierFixture.validInput());
        SupplierInputDTO update = SupplierFixture.otherInput();
        update.setId(UUID.fromString(id));

        request()
                .body(update)
        .when()
                .put(PATH)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("legalName", equalTo(SupplierFixture.OTHER_LEGAL_NAME))
                .body("cnpjCpf", equalTo(SupplierFixture.OTHER_CNPJ));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingWithoutLegalName() {
        String id = createSupplier(SupplierFixture.validInput());
        SupplierInputDTO update = SupplierFixture.validInput();
        update.setId(UUID.fromString(id));
        update.setLegalName(" ");

        request().body(update).when().put(PATH).then().statusCode(400);
    }

    @Test
    void shouldInactivateSupplierOnDelete() {
        String id = createSupplier(SupplierFixture.validInput());

        request().when().delete(PATH + "/{id}", id).then().statusCode(204);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
        request().when().get(PATH).then().statusCode(200).body("$", hasSize(0));
    }

    @Test
    void shouldReturnNotFoundForUnknownSupplier() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
        request().when().delete(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);

        SupplierInputDTO update = SupplierFixture.validInput();
        update.setId(UUID.randomUUID());
        request().body(update).when().put(PATH).then().statusCode(404);
    }

    private String createSupplier(SupplierInputDTO input) {
        return request().body(input).when().post(PATH).then().statusCode(201).extract().path("id");
    }
}
