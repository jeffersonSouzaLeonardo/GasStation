package com.br.manager.domain.organization.controller;

import com.br.manager.domain.organization.dto.StationInputDTO;
import com.br.manager.domain.organization.fixture.CompanyFixture;
import com.br.manager.domain.organization.fixture.StationFixture;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class StationControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/station";
    private static final String OTHER_NAME = "Posto Rodovia";

    @Test
    void shouldCreateStation() {
        UUID companyId = createCompany();

        request()
                .body(StationFixture.validInput(companyId))
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("companyId", equalTo(companyId.toString()))
                .body("name", equalTo(StationFixture.NAME))
                .body("cnpj", equalTo(StationFixture.CNPJ))
                .body("addressZipCode", equalTo(StationFixture.ZIP_CODE))
                .body("timezone", equalTo(StationFixture.TIMEZONE))
                .body("active", equalTo(true));
    }

    @Test
    void shouldApplyDefaultTimezoneWhenNotInformed() {
        StationInputDTO input = StationFixture.validInput(createCompany());
        input.setTimezone(null);

        request().body(input).when().post(PATH)
                .then().statusCode(200).body("timezone", equalTo(StationFixture.TIMEZONE));
    }

    @Test
    void shouldReturnBadRequestWhenCompanyDoesNotExist() {
        request()
                .body(StationFixture.validInput(UUID.randomUUID()))
        .when()
                .post(PATH)
        .then()
                .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenNameIsMissing() {
        StationInputDTO input = StationFixture.validInput(createCompany());
        input.setName(null);

        request().body(input).when().post(PATH).then().statusCode(400);
    }

    @Test
    void shouldListAndFindStations() {
        String id = createStation(StationFixture.validInput(createCompany()));

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(1));
        request().when().get(PATH + "/{id}", id).then().statusCode(200).body("id", equalTo(id));
    }

    @Test
    void shouldSearchStationsByName() {
        UUID companyId = createCompany();
        createStation(StationFixture.validInput(companyId));
        createStation(otherStation(companyId));

        request().queryParam("name", "rodovia").when().get(PATH + "/search")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].name", equalTo(OTHER_NAME));
        request().queryParam("description", "centro").when().get(PATH + "/search")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].name", equalTo(StationFixture.NAME));
        request().when().get(PATH + "/search")
                .then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldUpdateStation() {
        UUID companyId = createCompany();
        String id = createStation(StationFixture.validInput(companyId));
        StationInputDTO update = StationFixture.validInput(companyId);
        update.setId(UUID.fromString(id));
        update.setName(OTHER_NAME);
        update.setTimezone(StationFixture.OTHER_TIMEZONE);

        request()
                .body(update)
        .when()
                .put(PATH)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo(OTHER_NAME))
                .body("timezone", equalTo(StationFixture.OTHER_TIMEZONE));
    }

    @Test
    void shouldInactivateStationOnDelete() {
        String id = createStation(StationFixture.validInput(createCompany()));

        request().when().delete(PATH + "/{id}", id).then().statusCode(200);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
        request().when().get(PATH).then().statusCode(200).body("$", hasSize(0));
    }

    @Test
    void shouldReturnNotFoundForUnknownStation() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
        request().when().delete(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);

        StationInputDTO update = StationFixture.validInput();
        update.setId(UUID.randomUUID());
        request().body(update).when().put(PATH).then().statusCode(404);
    }

    private UUID createCompany() {
        String id = request().body(CompanyFixture.validInput()).when().post("/company")
                .then().statusCode(200).extract().path("id");
        return UUID.fromString(id);
    }

    private String createStation(StationInputDTO input) {
        return request().body(input).when().post(PATH).then().statusCode(200).extract().path("id");
    }

    private StationInputDTO otherStation(UUID companyId) {
        StationInputDTO input = StationFixture.validInput(companyId);
        input.setName(OTHER_NAME);
        return input;
    }
}
