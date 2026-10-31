package com.br.manager.domain.price_acquisition.controller;

import com.br.manager.domain.price_acquisition.dto.PriceItemInputDTO;
import com.br.manager.domain.price_acquisition.fixture.PriceItemFixture;
import com.br.manager.domain.price_acquisition.fixture.PriceTableFixture;
import com.br.manager.domain.price_acquisition.support.PriceAcquisitionTestData;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static com.br.manager.domain.price_acquisition.support.PriceAcquisitionTestData.toJson;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@Import(PriceAcquisitionTestData.class)
class PriceItemControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/price-item";

    @Autowired
    private PriceAcquisitionTestData testData;

    private UUID productId;
    private String priceTableId;

    @BeforeEach
    void setUpPrerequisites() {
        UUID userId = testData.createUser();
        productId = testData.createProduct();
        priceTableId = request()
                .body(toJson(PriceTableFixture.validInput(userId, List.of(PriceItemFixture.validInput(null, productId)))))
        .when()
                .post("/price-table")
        .then()
                .statusCode(200)
                .extract().path("id");
    }

    @Test
    void shouldCreatePriceItem() {
        request()
                .config(PriceAcquisitionTestData.DECIMAL_CONFIG)
                .body(toJson(validInput()))
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("priceTableId", equalTo(priceTableId))
                .body("productId", equalTo(productId.toString()))
                .body("unitPrice", comparesEqualTo(PriceItemFixture.UNIT_PRICE))
                .body("minPrice", comparesEqualTo(PriceItemFixture.MIN_PRICE))
                .body("maxDiscountPercent", comparesEqualTo(PriceItemFixture.MAX_DISCOUNT_PERCENT))
                .body("active", equalTo(true));
    }

    @Test
    void shouldReturnNotFoundWhenCreatingWithUnknownPriceTable() {
        request()
                .body(toJson(PriceItemFixture.validInput(UUID.randomUUID(), productId)))
        .when()
                .post(PATH)
        .then()
                .statusCode(404);
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWithoutUnitPrice() {
        PriceItemInputDTO input = validInput();
        input.setUnitPrice(null);

        request().body(toJson(input)).when().post(PATH).then().statusCode(400);
    }

    @Test
    void shouldSaveItemsByPriceTable() {
        request()
                .body(toJson(List.of(validInput(), validInput())))
        .when()
                .post(PATH + "/price-table/{priceTableId}", priceTableId)
        .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].priceTableId", equalTo(priceTableId));
    }

    @Test
    void shouldReturnNotFoundWhenSavingItemsForUnknownPriceTable() {
        request()
                .body(toJson(List.of(validInput())))
        .when()
                .post(PATH + "/price-table/{priceTableId}", UUID.randomUUID())
        .then()
                .statusCode(404);
    }

    @Test
    void shouldListActiveItemsOnly() {
        PriceItemInputDTO inactive = validInput();
        inactive.setActive(false);
        createPriceItem(inactive);

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(1));
    }

    @Test
    void shouldListItemsByPriceTable() {
        createPriceItem(validInput());

        request().when().get(PATH + "/by-price-table/{id}", priceTableId)
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].priceTableId", equalTo(priceTableId));
    }

    @Test
    void shouldFindPriceItemById() {
        String id = createPriceItem(validInput());

        request().when().get(PATH + "/{id}", id).then().statusCode(200).body("id", equalTo(id));
    }

    @Test
    void shouldReturnNotFoundWhenFindingInactivePriceItem() {
        PriceItemInputDTO inactive = validInput();
        inactive.setActive(false);
        String id = createPriceItem(inactive);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenFindingUnknownPriceItem() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
    }

    @Test
    void shouldUpdatePriceItem() {
        String id = createPriceItem(validInput());
        PriceItemInputDTO update = validInput();
        update.setId(UUID.fromString(id));
        update.setUnitPrice(PriceItemFixture.UPDATED_UNIT_PRICE);

        request()
                .config(PriceAcquisitionTestData.DECIMAL_CONFIG)
                .body(toJson(update))
        .when()
                .patch(PATH)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("unitPrice", comparesEqualTo(PriceItemFixture.UPDATED_UNIT_PRICE));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingUnknownPriceItem() {
        PriceItemInputDTO update = validInput();
        update.setId(UUID.randomUUID());

        request().body(toJson(update)).when().patch(PATH).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingWithUnknownPriceTable() {
        String id = createPriceItem(validInput());
        PriceItemInputDTO update = validInput();
        update.setId(UUID.fromString(id));
        update.setPriceTableId(UUID.randomUUID());

        request().body(toJson(update)).when().patch(PATH).then().statusCode(404);
    }

    @Test
    void shouldDeletePriceItem() {
        String id = createPriceItem(validInput());

        request().when().delete(PATH + "/{id}", id).then().statusCode(200);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingUnknownPriceItem() {
        request().when().delete(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
    }

    private PriceItemInputDTO validInput() {
        return PriceItemFixture.validInput(UUID.fromString(priceTableId), productId);
    }

    private String createPriceItem(PriceItemInputDTO input) {
        return request()
                .body(toJson(input))
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .extract().path("id");
    }
}
