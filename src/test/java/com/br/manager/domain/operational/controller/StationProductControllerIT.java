package com.br.manager.domain.operational.controller;

import com.br.manager.domain.operational.dto.StationProductInputDTO;
import com.br.manager.domain.operational.fixture.StationProductFixture;
import com.br.manager.domain.operational.support.OperationalAIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class StationProductControllerIT extends OperationalAIntegrationTest {

    @Test
    void shouldCreateStationProduct() {
        UUID productId = createFuelProduct();

        request()
                .body(StationProductFixture.validInput(productId))
        .when()
                .post(STATION_PRODUCTS)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("productId", equalTo(productId.toString()))
                .body("minStock", equalTo(StationProductFixture.MIN_STOCK.floatValue()))
                .body("reorderPoint", equalTo(StationProductFixture.REORDER_POINT.floatValue()))
                .body("active", equalTo(true));
    }

    @Test
    void shouldReturnBadRequestWhenProductDoesNotExist() {
        request()
                .body(StationProductFixture.validInput(UUID.randomUUID()))
        .when()
                .post(STATION_PRODUCTS)
        .then()
                .statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenProductIsMissing() {
        request()
                .body(StationProductFixture.validInput(null))
        .when()
                .post(STATION_PRODUCTS)
        .then()
                .statusCode(400);
    }

    @Test
    void shouldListAndFindStationProducts() {
        String id = create(STATION_PRODUCTS, StationProductFixture.validInput(createFuelProduct()));

        request().when().get(STATION_PRODUCTS).then().statusCode(200).body("$", hasSize(1));
        request().when().get(STATION_PRODUCTS + "/{id}", id).then().statusCode(200)
                .body("id", equalTo(id))
                .body("minStock", equalTo(StationProductFixture.MIN_STOCK.floatValue()));
    }

    @Test
    void shouldSearchStationProductsByProductId() {
        UUID fuelId = createFuelProduct();
        create(STATION_PRODUCTS, StationProductFixture.validInput(fuelId));
        create(STATION_PRODUCTS, StationProductFixture.validInput(createConvenienceProduct()));

        request().queryParam("description", fuelId.toString()).when().get(STATION_PRODUCTS + "/search")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].productId", equalTo(fuelId.toString()));
        request().queryParam("description", "nao-e-uuid").when().get(STATION_PRODUCTS + "/search")
                .then().statusCode(200).body("$", hasSize(0));
        request().queryParam("description", "").when().get(STATION_PRODUCTS + "/search")
                .then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldUpdateStationProduct() {
        UUID productId = createFuelProduct();
        String id = create(STATION_PRODUCTS, StationProductFixture.validInput(productId));
        StationProductInputDTO update = StationProductFixture.validInput(productId);
        update.setId(UUID.fromString(id));
        update.setMinStock(StationProductFixture.REORDER_POINT);

        request()
                .body(update)
        .when()
                .put(STATION_PRODUCTS)
        .then()
                .statusCode(200)
                .body("minStock", equalTo(StationProductFixture.REORDER_POINT.floatValue()));
    }

    @Test
    void shouldInactivateStationProductOnDelete() {
        String id = create(STATION_PRODUCTS, StationProductFixture.validInput(createFuelProduct()));

        request().when().delete(STATION_PRODUCTS + "/{id}", id).then().statusCode(204);

        request().when().get(STATION_PRODUCTS + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundForUnknownStationProduct() {
        request().when().get(STATION_PRODUCTS + "/{id}", UUID.randomUUID()).then().statusCode(404);
        request().when().delete(STATION_PRODUCTS + "/{id}", UUID.randomUUID()).then().statusCode(404);

        StationProductInputDTO update = StationProductFixture.validInput(UUID.randomUUID());
        update.setId(UUID.randomUUID());
        request().body(update).when().put(STATION_PRODUCTS).then().statusCode(404);
    }
}
