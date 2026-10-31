package com.br.manager.domain.operational.controller;

import com.br.manager.domain.operational.dto.FuelInputDTO;
import com.br.manager.domain.operational.fixture.FuelFixture;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@Disabled("BUG: entity Fuel (operational/entity/Fuel.java) is mapped to table `fuel`, which no Flyway migration creates; "
        + "every /fuel endpoint fails with a SQL error (table doesn't exist)")
class FuelControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/fuel";
    private static final long UNKNOWN_ID = 999_999L;

    @Test
    void shouldCreateFuel() {
        request()
                .body(FuelFixture.validInput())
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("name", equalTo(FuelFixture.NAME))
                .body("unit", equalTo(FuelFixture.UNIT.name()))
                .body("status", equalTo(FuelFixture.STATUS.name()))
                .body("idAnp", equalTo(FuelFixture.ID_ANP));
    }

    @Test
    void shouldReturnBadRequestWhenNameIsMissing() {
        FuelInputDTO input = FuelFixture.validInput();
        input.setName(null);

        request().body(input).when().post(PATH).then().statusCode(400);
    }

    @Test
    void shouldListFuels() {
        createFuel(FuelFixture.validInput());
        createFuel(FuelFixture.otherInput());

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldFindFuelById() {
        Integer id = createFuel(FuelFixture.validInput());

        request().when().get(PATH + "/{id}", id)
                .then().statusCode(200).body("id", equalTo(id)).body("name", equalTo(FuelFixture.NAME));
    }

    @Test
    void shouldSearchFuelsByName() {
        createFuel(FuelFixture.validInput());
        createFuel(FuelFixture.otherInput());

        request().queryParam("description", "etanol").when().get(PATH + "/search")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].name", equalTo(FuelFixture.OTHER_NAME));
    }

    @Test
    void shouldReturnAllFuelsWhenSearchDescriptionIsMissing() {
        createFuel(FuelFixture.validInput());
        createFuel(FuelFixture.otherInput());

        request().when().get(PATH + "/search").then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldUpdateFuel() {
        Integer id = createFuel(FuelFixture.validInput());
        FuelInputDTO update = FuelFixture.otherInput();
        update.setId(id.longValue());

        request()
                .body(update)
        .when()
                .put(PATH)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo(FuelFixture.OTHER_NAME));
    }

    @Test
    void shouldSoftDeleteFuel() {
        Integer id = createFuel(FuelFixture.validInput());

        request().when().delete(PATH + "/{id}", id).then().statusCode(200);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundForUnknownFuel() {
        request().when().get(PATH + "/{id}", UNKNOWN_ID).then().statusCode(404);
        request().when().delete(PATH + "/{id}", UNKNOWN_ID).then().statusCode(404);

        FuelInputDTO update = FuelFixture.validInput();
        update.setId(UNKNOWN_ID);
        request().body(update).when().put(PATH).then().statusCode(404);
    }

    private Integer createFuel(FuelInputDTO input) {
        return request().body(input).when().post(PATH).then().statusCode(200).extract().path("id");
    }
}
