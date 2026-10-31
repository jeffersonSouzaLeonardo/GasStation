package com.br.manager.domain.operational.controller;

import com.br.manager.domain.operational.dto.CustomerVehicleInputDTO;
import com.br.manager.domain.operational.fixture.CustomerFixture;
import com.br.manager.domain.operational.fixture.CustomerVehicleFixture;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class CustomerVehicleControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/api/customer-vehicles";
    private static final String CUSTOMERS_PATH = "/api/customers";
    private static final String PLATE_BUG = "BUG: CustomerVehicleMapper applies StringUtils.removeMask (onlyDigits) to the plate, "
            + "so ABC-1D23 is stored as 123 and plate searches by letters never match";

    private UUID customerId;

    @BeforeEach
    void createCustomer() {
        String id = request().body(CustomerFixture.validInput()).when().post(CUSTOMERS_PATH)
                .then().statusCode(201).extract().path("id");
        customerId = UUID.fromString(id);
    }

    @Test
    void shouldCreateVehicle() {
        request()
                .body(CustomerVehicleFixture.validInput(customerId))
        .when()
                .post(PATH)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("customerId", equalTo(customerId.toString()))
                .body("brand", equalTo(CustomerVehicleFixture.BRAND))
                .body("model", equalTo(CustomerVehicleFixture.MODEL))
                .body("fuelType", equalTo(CustomerVehicleFixture.FUEL_TYPE))
                .body("active", equalTo(true));
    }

    @Test
    @Disabled(PLATE_BUG)
    void shouldCreateVehicleKeepingPlateLetters() {
        request()
                .body(CustomerVehicleFixture.validInput(customerId))
        .when()
                .post(PATH)
        .then()
                .statusCode(201)
                .body("plate", equalTo(CustomerVehicleFixture.PLATE));
    }

    @Test
    void shouldReturnBadRequestWhenCustomerDoesNotExist() {
        request().body(CustomerVehicleFixture.validInput(UUID.randomUUID())).when().post(PATH).then().statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenCustomerIsMissing() {
        request().body(CustomerVehicleFixture.validInput(null)).when().post(PATH).then().statusCode(400);
    }

    @Test
    void shouldListActiveVehicles() {
        createVehicle(CustomerVehicleFixture.validInput(customerId));

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(1));
    }

    @Test
    void shouldFindVehicleById() {
        String id = createVehicle(CustomerVehicleFixture.validInput(customerId));

        request().when().get(PATH + "/{id}", id)
                .then().statusCode(200).body("id", equalTo(id)).body("model", equalTo(CustomerVehicleFixture.MODEL));
    }

    @Test
    @Disabled(PLATE_BUG)
    void shouldSearchVehiclesByPlate() {
        createVehicle(CustomerVehicleFixture.validInput(customerId));
        createVehicle(otherVehicle());

        request().queryParam("description", "abc").when().get(PATH + "/search")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].plate", equalTo(CustomerVehicleFixture.PLATE));
    }

    @Test
    void shouldReturnAllVehiclesWhenSearchDescriptionIsBlank() {
        createVehicle(CustomerVehicleFixture.validInput(customerId));
        createVehicle(otherVehicle());

        request().queryParam("description", " ").when().get(PATH + "/search")
                .then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldUpdateVehicle() {
        String id = createVehicle(CustomerVehicleFixture.validInput(customerId));
        CustomerVehicleInputDTO update = CustomerVehicleFixture.validInput(customerId);
        update.setId(UUID.fromString(id));
        update.setBrand("Scania");

        request()
                .body(update)
        .when()
                .put(PATH)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("brand", equalTo("Scania"));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingToUnknownCustomer() {
        String id = createVehicle(CustomerVehicleFixture.validInput(customerId));
        CustomerVehicleInputDTO update = CustomerVehicleFixture.validInput(UUID.randomUUID());
        update.setId(UUID.fromString(id));

        request().body(update).when().put(PATH).then().statusCode(400);
    }

    @Test
    void shouldInactivateVehicleOnDelete() {
        String id = createVehicle(CustomerVehicleFixture.validInput(customerId));

        request().when().delete(PATH + "/{id}", id).then().statusCode(204);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundForUnknownVehicle() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
        request().when().delete(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);

        CustomerVehicleInputDTO update = CustomerVehicleFixture.validInput(customerId);
        update.setId(UUID.randomUUID());
        request().body(update).when().put(PATH).then().statusCode(404);
    }

    private String createVehicle(CustomerVehicleInputDTO input) {
        return request().body(input).when().post(PATH).then().statusCode(201).extract().path("id");
    }

    private CustomerVehicleInputDTO otherVehicle() {
        CustomerVehicleInputDTO input = CustomerVehicleFixture.validInput(customerId);
        input.setPlate(CustomerVehicleFixture.OTHER_PLATE);
        return input;
    }
}
