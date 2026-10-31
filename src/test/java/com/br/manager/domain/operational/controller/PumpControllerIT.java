package com.br.manager.domain.operational.controller;

import com.br.manager.domain.operational.dto.PumpInputDTO;
import com.br.manager.domain.operational.fixture.PumpFixture;
import com.br.manager.domain.operational.support.OperationalAIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class PumpControllerIT extends OperationalAIntegrationTest {

    @Test
    void shouldCreatePump() {
        request()
                .body(PumpFixture.validInput())
        .when()
                .post(PUMPS)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("code", equalTo(PumpFixture.CODE))
                .body("location", equalTo(PumpFixture.LOCATION))
                .body("active", equalTo(true));
    }

    @Test
    void shouldReturnBadRequestWhenCodeIsTooLong() {
        request()
                .body(PumpFixture.validInput(PumpFixture.TOO_LONG_CODE))
        .when()
                .post(PUMPS)
        .then()
                .statusCode(400);
    }

    @Test
    void shouldListAndFindPumps() {
        String id = createPump(PumpFixture.validInput());

        request().when().get(PUMPS).then().statusCode(200).body("$", hasSize(1));
        request().when().get(PUMPS + "/{id}", id).then().statusCode(200).body("id", equalTo(id));
    }

    @Test
    void shouldSearchPumpsByCode() {
        createPump(PumpFixture.validInput());
        createPump(PumpFixture.validInput(PumpFixture.OTHER_CODE));

        request().queryParam("description", PumpFixture.OTHER_CODE.toLowerCase()).when().get(PUMPS + "/search")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].code", equalTo(PumpFixture.OTHER_CODE));
        request().queryParam("description", "").when().get(PUMPS + "/search")
                .then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldUpdatePump() {
        String id = createPump(PumpFixture.validInput());
        PumpInputDTO update = PumpFixture.validInput(PumpFixture.OTHER_CODE);
        update.setId(UUID.fromString(id));

        request()
                .body(update)
        .when()
                .put(PUMPS)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("code", equalTo(PumpFixture.OTHER_CODE));
    }

    @Test
    void shouldInactivatePumpOnDelete() {
        String id = createPump(PumpFixture.validInput());

        request().when().delete(PUMPS + "/{id}", id).then().statusCode(204);

        request().when().get(PUMPS + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundForUnknownPump() {
        request().when().get(PUMPS + "/{id}", UUID.randomUUID()).then().statusCode(404);
        request().when().delete(PUMPS + "/{id}", UUID.randomUUID()).then().statusCode(404);

        PumpInputDTO update = PumpFixture.validInput();
        update.setId(UUID.randomUUID());
        request().body(update).when().put(PUMPS).then().statusCode(404);
    }
}
