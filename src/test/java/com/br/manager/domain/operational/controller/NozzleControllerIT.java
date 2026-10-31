package com.br.manager.domain.operational.controller;

import com.br.manager.domain.operational.dto.NozzleInputDTO;
import com.br.manager.domain.operational.fixture.NozzleFixture;
import com.br.manager.domain.operational.support.OperationalAIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class NozzleControllerIT extends OperationalAIntegrationTest {

    private UUID pumpId;
    private UUID tankId;
    private UUID fuelId;

    @BeforeEach
    void createPrerequisites() {
        fuelId = createFuelProduct();
        tankId = createTank(fuelId);
        pumpId = createPump();
    }

    @Test
    void shouldCreateNozzle() {
        request()
                .body(validInput())
        .when()
                .post(NOZZLES)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("code", equalTo(NozzleFixture.CODE))
                .body("pumpId", equalTo(pumpId.toString()))
                .body("tankId", equalTo(tankId.toString()))
                .body("productId", equalTo(fuelId.toString()))
                .body("meterNumber", equalTo(NozzleFixture.METER_NUMBER.floatValue()))
                .body("active", equalTo(true));
    }

    @Test
    void shouldReturnBadRequestWhenProductIsNotFuel() {
        NozzleInputDTO input = validInput();
        input.setProductId(createConvenienceProduct());

        request().body(input).when().post(NOZZLES).then().statusCode(400);
    }

    @Test
    void shouldReturnNotFoundWhenProductDoesNotExist() {
        NozzleInputDTO input = validInput();
        input.setProductId(UUID.randomUUID());

        request().body(input).when().post(NOZZLES).then().statusCode(404);
    }

    @Test
    void shouldReturnBadRequestWhenCodeIsDuplicatedInSamePump() {
        create(NOZZLES, validInput());

        request().body(validInput()).when().post(NOZZLES).then().statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenPumpDoesNotExist() {
        NozzleInputDTO input = validInput();
        input.setPumpId(UUID.randomUUID());

        request().body(input).when().post(NOZZLES).then().statusCode(400);
    }

    @Test
    void shouldListAndFindNozzles() {
        String id = create(NOZZLES, validInput());

        request().when().get(NOZZLES).then().statusCode(200).body("$", hasSize(1));
        request().when().get(NOZZLES + "/{id}", id).then().statusCode(200)
                .body("id", equalTo(id))
                .body("meterNumber", equalTo(NozzleFixture.METER_NUMBER.floatValue()));
    }

    @Test
    void shouldSearchNozzlesByCode() {
        create(NOZZLES, validInput());
        create(NOZZLES, validInput(NozzleFixture.OTHER_CODE));

        request().queryParam("description", NozzleFixture.OTHER_CODE.toLowerCase()).when().get(NOZZLES + "/search")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].code", equalTo(NozzleFixture.OTHER_CODE));
        request().queryParam("description", "").when().get(NOZZLES + "/search")
                .then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldUpdateNozzle() {
        String id = create(NOZZLES, validInput());
        NozzleInputDTO update = validInput(NozzleFixture.OTHER_CODE);
        update.setId(UUID.fromString(id));

        request()
                .body(update)
        .when()
                .put(NOZZLES)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("code", equalTo(NozzleFixture.OTHER_CODE));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingToNonFuelProduct() {
        String id = create(NOZZLES, validInput());
        NozzleInputDTO update = validInput();
        update.setId(UUID.fromString(id));
        update.setProductId(createConvenienceProduct());

        request().body(update).when().put(NOZZLES).then().statusCode(400);
    }

    @Test
    void shouldInactivateNozzleOnDelete() {
        String id = create(NOZZLES, validInput());

        request().when().delete(NOZZLES + "/{id}", id).then().statusCode(204);

        request().when().get(NOZZLES + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundForUnknownNozzle() {
        request().when().get(NOZZLES + "/{id}", UUID.randomUUID()).then().statusCode(404);
        request().when().delete(NOZZLES + "/{id}", UUID.randomUUID()).then().statusCode(404);

        NozzleInputDTO update = validInput();
        update.setId(UUID.randomUUID());
        request().body(update).when().put(NOZZLES).then().statusCode(404);
    }

    private NozzleInputDTO validInput() {
        return NozzleFixture.validInput(pumpId, tankId, fuelId);
    }

    private NozzleInputDTO validInput(String code) {
        NozzleInputDTO input = validInput();
        input.setCode(code);
        return input;
    }
}
