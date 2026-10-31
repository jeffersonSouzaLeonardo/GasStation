package com.br.manager.domain.operational.controller;

import com.br.manager.domain.operational.dto.ProductInputDTO;
import com.br.manager.domain.operational.fixture.ProductFixture;
import com.br.manager.domain.operational.support.OperationalAIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class ProductControllerIT extends OperationalAIntegrationTest {

    @Test
    void shouldCreateProduct() {
        request()
                .body(ProductFixture.fuelInput())
        .when()
                .post(PRODUCTS)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("sku", equalTo(ProductFixture.FUEL_SKU))
                .body("name", equalTo(ProductFixture.FUEL_NAME))
                .body("productType", equalTo("FUEL"))
                .body("unit", equalTo("L"))
                .body("anpCode", equalTo(ProductFixture.ANP_CODE))
                .body("active", equalTo(true));
    }

    @Test
    void shouldReturnBadRequestWhenNameIsMissing() {
        request()
                .body(ProductFixture.inputWithoutName())
        .when()
                .post(PRODUCTS)
        .then()
                .statusCode(400);
    }

    @Test
    void shouldListAndFindProducts() {
        String id = createProduct(ProductFixture.fuelInput());

        request().when().get(PRODUCTS).then().statusCode(200).body("$", hasSize(1));
        request().when().get(PRODUCTS + "/{id}", id).then().statusCode(200).body("id", equalTo(id));
    }

    @Test
    void shouldListOnlyFuelProducts() {
        createProduct(ProductFixture.fuelInput());
        createProduct(ProductFixture.convenienceInput());

        request().when().get(PRODUCTS + "/fuel")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].productType", equalTo("FUEL"));
    }

    @Test
    void shouldSearchProductsByName() {
        createProduct(ProductFixture.fuelInput());
        createProduct(ProductFixture.convenienceInput());

        request().queryParam("description", "refrigerante").when().get(PRODUCTS + "/search")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].name", equalTo(ProductFixture.CONVENIENCE_NAME));
        request().queryParam("description", "").when().get(PRODUCTS + "/search")
                .then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldUpdateProduct() {
        String id = createProduct(ProductFixture.fuelInput());
        ProductInputDTO update = ProductFixture.fuelInput();
        update.setId(UUID.fromString(id));
        update.setName("Gasolina Aditivada");

        request()
                .body(update)
        .when()
                .put(PRODUCTS)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo("Gasolina Aditivada"));
    }

    @Test
    void shouldInactivateProductOnDelete() {
        String id = createProduct(ProductFixture.fuelInput());

        request().when().delete(PRODUCTS + "/{id}", id).then().statusCode(204);

        request().when().get(PRODUCTS + "/{id}", id).then().statusCode(404);
        request().when().get(PRODUCTS).then().statusCode(200).body("$", hasSize(0));
    }

    @Test
    void shouldReturnNotFoundForUnknownProduct() {
        request().when().get(PRODUCTS + "/{id}", UUID.randomUUID()).then().statusCode(404);
        request().when().delete(PRODUCTS + "/{id}", UUID.randomUUID()).then().statusCode(404);

        ProductInputDTO update = ProductFixture.fuelInput();
        update.setId(UUID.randomUUID());
        request().body(update).when().put(PRODUCTS).then().statusCode(404);
    }
}
