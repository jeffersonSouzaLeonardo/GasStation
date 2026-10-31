package com.br.manager.domain.price_acquisition.controller;

import com.br.manager.domain.price_acquisition.dto.GoodsReceiptItemInputDTO;
import com.br.manager.domain.price_acquisition.fixture.GoodsReceiptFixture;
import com.br.manager.domain.price_acquisition.fixture.GoodsReceiptItemFixture;
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
class GoodsReceiptItemControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/goods-receipt-item";

    @Autowired
    private PriceAcquisitionTestData testData;

    private UUID productId;
    private UUID tankId;
    private String goodsReceiptId;

    @BeforeEach
    void setUpPrerequisites() {
        productId = testData.createProduct();
        tankId = testData.createTank(productId);
        goodsReceiptId = request()
                .body(toJson(GoodsReceiptFixture.validInput(testData.createSupplier(), testData.createUser(),
                        List.of(GoodsReceiptItemFixture.validInput(null, productId, tankId)))))
        .when()
                .post("/goods-receipt")
        .then()
                .statusCode(200)
                .extract().path("id");
    }

    @Test
    void shouldCreateGoodsReceiptItem() {
        request()
                .config(PriceAcquisitionTestData.DECIMAL_CONFIG)
                .body(toJson(validInput()))
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("goodsReceiptId", equalTo(goodsReceiptId))
                .body("productId", equalTo(productId.toString()))
                .body("tankId", equalTo(tankId.toString()))
                .body("quantity", comparesEqualTo(GoodsReceiptItemFixture.QUANTITY))
                .body("unitCost", comparesEqualTo(GoodsReceiptItemFixture.UNIT_COST));
    }

    @Test
    void shouldReturnNotFoundWhenCreatingWithUnknownGoodsReceipt() {
        request()
                .body(toJson(GoodsReceiptItemFixture.validInput(UUID.randomUUID(), productId, tankId)))
        .when()
                .post(PATH)
        .then()
                .statusCode(404);
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWithoutQuantity() {
        GoodsReceiptItemInputDTO input = validInput();
        input.setQuantity(null);

        request().body(toJson(input)).when().post(PATH).then().statusCode(400);
    }

    @Test
    void shouldSaveItemsByGoodsReceipt() {
        request()
                .body(toJson(List.of(validInput(), validInput())))
        .when()
                .post(PATH + "/goods-receipt/{goodsReceiptId}", goodsReceiptId)
        .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].goodsReceiptId", equalTo(goodsReceiptId));
    }

    @Test
    void shouldReturnNotFoundWhenSavingItemsForUnknownGoodsReceipt() {
        request()
                .body(toJson(List.of(validInput())))
        .when()
                .post(PATH + "/goods-receipt/{goodsReceiptId}", UUID.randomUUID())
        .then()
                .statusCode(404);
    }

    @Test
    void shouldListGoodsReceiptItems() {
        createItem();

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldListItemsByGoodsReceipt() {
        createItem();

        request().when().get(PATH + "/by-goods-receipt/{id}", goodsReceiptId)
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].goodsReceiptId", equalTo(goodsReceiptId));
    }

    @Test
    void shouldFindGoodsReceiptItemById() {
        String id = createItem();

        request().when().get(PATH + "/{id}", id).then().statusCode(200).body("id", equalTo(id));
    }

    @Test
    void shouldReturnNotFoundWhenFindingUnknownGoodsReceiptItem() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
    }

    @Test
    void shouldUpdateGoodsReceiptItem() {
        String id = createItem();
        GoodsReceiptItemInputDTO update = validInput();
        update.setId(UUID.fromString(id));
        update.setQuantity(GoodsReceiptItemFixture.UPDATED_QUANTITY);

        request()
                .config(PriceAcquisitionTestData.DECIMAL_CONFIG)
                .body(toJson(update))
        .when()
                .patch(PATH)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("quantity", comparesEqualTo(GoodsReceiptItemFixture.UPDATED_QUANTITY));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingUnknownGoodsReceiptItem() {
        GoodsReceiptItemInputDTO update = validInput();
        update.setId(UUID.randomUUID());

        request().body(toJson(update)).when().patch(PATH).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingWithUnknownGoodsReceipt() {
        String id = createItem();
        GoodsReceiptItemInputDTO update = validInput();
        update.setId(UUID.fromString(id));
        update.setGoodsReceiptId(UUID.randomUUID());

        request().body(toJson(update)).when().patch(PATH).then().statusCode(404);
    }

    @Test
    void shouldDeleteGoodsReceiptItem() {
        String id = createItem();

        request().when().delete(PATH + "/{id}", id).then().statusCode(200);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingUnknownGoodsReceiptItem() {
        request().when().delete(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
    }

    private GoodsReceiptItemInputDTO validInput() {
        return GoodsReceiptItemFixture.validInput(UUID.fromString(goodsReceiptId), productId, tankId);
    }

    private String createItem() {
        return request()
                .body(toJson(validInput()))
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .extract().path("id");
    }
}
