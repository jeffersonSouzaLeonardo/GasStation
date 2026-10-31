package com.br.manager.domain.operational.controller;

import com.br.manager.domain.operational.dto.TankInputDTO;
import com.br.manager.domain.operational.fixture.TankFixture;
import com.br.manager.domain.operational.support.OperationalAIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class TankControllerIT extends OperationalAIntegrationTest {

    @Test
    void shouldCreateTankForFuelProduct() {
        UUID productId = createFuelProduct();

        request()
                .body(TankFixture.validInput(productId))
        .when()
                .post(TANKS)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("code", equalTo(TankFixture.CODE))
                .body("productId", equalTo(productId.toString()))
                .body("capacityLiters", equalTo(TankFixture.CAPACITY.floatValue()))
                .body("deadStockLiters", equalTo(TankFixture.DEAD_STOCK.floatValue()))
                .body("currentBookLiters", equalTo(TankFixture.CURRENT_BOOK.floatValue()))
                .body("active", equalTo(true));
    }

    @Test
    void shouldReturnBadRequestWhenProductIsNotFuel() {
        request()
                .body(TankFixture.validInput(createConvenienceProduct()))
        .when()
                .post(TANKS)
        .then()
                .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenCapacityIsMissing() {
        TankInputDTO input = TankFixture.validInput(createFuelProduct());
        input.setCapacityLiters(null);

        request().body(input).when().post(TANKS).then().statusCode(400);
    }

    @Test
    void shouldListAndFindTanks() {
        String id = createTank(TankFixture.validInput(createFuelProduct()));

        request().when().get(TANKS).then().statusCode(200).body("$", hasSize(1));
        request().when().get(TANKS + "/{id}", id).then().statusCode(200)
                .body("id", equalTo(id))
                .body("capacityLiters", equalTo(TankFixture.CAPACITY.floatValue()));
    }

    @Test
    void shouldSearchTanksByCode() {
        UUID productId = createFuelProduct();
        createTank(TankFixture.validInput(productId));
        createTank(TankFixture.validInput(productId, TankFixture.OTHER_CODE));

        request().queryParam("description", TankFixture.OTHER_CODE.toLowerCase()).when().get(TANKS + "/search")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].code", equalTo(TankFixture.OTHER_CODE));
        request().queryParam("description", "").when().get(TANKS + "/search")
                .then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldUpdateTank() {
        UUID productId = createFuelProduct();
        String id = createTank(TankFixture.validInput(productId));
        TankInputDTO update = TankFixture.validInput(productId, TankFixture.OTHER_CODE);
        update.setId(UUID.fromString(id));

        request()
                .body(update)
        .when()
                .put(TANKS)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("code", equalTo(TankFixture.OTHER_CODE));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingToNonFuelProduct() {
        String id = createTank(TankFixture.validInput(createFuelProduct()));
        TankInputDTO update = TankFixture.validInput(createConvenienceProduct());
        update.setId(UUID.fromString(id));

        request().body(update).when().put(TANKS).then().statusCode(400);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingToUnknownProduct() {
        String id = createTank(TankFixture.validInput(createFuelProduct()));
        TankInputDTO update = TankFixture.validInput(UUID.randomUUID());
        update.setId(UUID.fromString(id));

        request().body(update).when().put(TANKS).then().statusCode(404);
    }

    @Test
    void shouldInactivateTankOnDelete() {
        String id = createTank(TankFixture.validInput(createFuelProduct()));

        request().when().delete(TANKS + "/{id}", id).then().statusCode(204);

        request().when().get(TANKS + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundForUnknownTank() {
        request().when().get(TANKS + "/{id}", UUID.randomUUID()).then().statusCode(404);
        request().when().delete(TANKS + "/{id}", UUID.randomUUID()).then().statusCode(404);

        TankInputDTO update = TankFixture.validInput(UUID.randomUUID());
        update.setId(UUID.randomUUID());
        request().body(update).when().put(TANKS).then().statusCode(404);
    }
}
