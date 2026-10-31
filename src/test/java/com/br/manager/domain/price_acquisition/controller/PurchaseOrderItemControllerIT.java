package com.br.manager.domain.price_acquisition.controller;

import com.br.manager.domain.price_acquisition.dto.PurchaseOrderItemInputDTO;
import com.br.manager.domain.price_acquisition.fixture.PurchaseOrderFixture;
import com.br.manager.domain.price_acquisition.fixture.PurchaseOrderItemFixture;
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
class PurchaseOrderItemControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/purchase-order-item";

    @Autowired
    private PriceAcquisitionTestData testData;

    private UUID productId;
    private String purchaseOrderId;

    @BeforeEach
    void setUpPrerequisites() {
        productId = testData.createProduct();
        purchaseOrderId = request()
                .body(toJson(PurchaseOrderFixture.validInput(testData.createSupplier(), testData.createUser(),
                        List.of(PurchaseOrderItemFixture.validInput(null, productId)))))
        .when()
                .post("/purchase-order")
        .then()
                .statusCode(201)
                .extract().path("id");
    }

    @Test
    void shouldCreatePurchaseOrderItem() {
        request()
                .config(PriceAcquisitionTestData.DECIMAL_CONFIG)
                .body(toJson(validInput()))
        .when()
                .post(PATH)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("purchaseOrderId", equalTo(purchaseOrderId))
                .body("productId", equalTo(productId.toString()))
                .body("unitCost", comparesEqualTo(PurchaseOrderItemFixture.UNIT_COST))
                .body("totalCost", comparesEqualTo(PurchaseOrderItemFixture.TOTAL_COST));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWithZeroQuantity() {
        PurchaseOrderItemInputDTO input = validInput();
        input.setQuantity(0);

        request()
                .body(toJson(input))
        .when()
                .post(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo("A quantidade deve ser maior que zero."));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWithoutProduct() {
        PurchaseOrderItemInputDTO input = validInput();
        input.setProductId(null);

        request()
                .body(toJson(input))
        .when()
                .post(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo("O ID do produto é obrigatório."));
    }

    @Test
    void shouldReturnNotFoundWhenCreatingWithUnknownPurchaseOrder() {
        request()
                .body(toJson(PurchaseOrderItemFixture.validInput(UUID.randomUUID(), productId)))
        .when()
                .post(PATH)
        .then()
                .statusCode(404);
    }

    @Test
    void shouldListPurchaseOrderItems() {
        createItem();

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldListItemsByPurchaseOrder() {
        createItem();

        request().when().get(PATH + "/by-purchase-order/{id}", purchaseOrderId)
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].purchaseOrderId", equalTo(purchaseOrderId));
    }

    @Test
    void shouldFindPurchaseOrderItemById() {
        String id = createItem();

        request().when().get(PATH + "/{id}", id).then().statusCode(200).body("id", equalTo(id));
    }

    @Test
    void shouldReturnNotFoundWhenFindingUnknownPurchaseOrderItem() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
    }

    @Test
    void shouldUpdatePurchaseOrderItem() {
        String id = createItem();
        PurchaseOrderItemInputDTO update = validInput();
        update.setId(UUID.fromString(id));
        update.setQuantity(PurchaseOrderItemFixture.UPDATED_QUANTITY);

        request()
                .config(PriceAcquisitionTestData.DECIMAL_CONFIG)
                .body(toJson(update))
        .when()
                .patch(PATH)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("quantity", equalTo(PurchaseOrderItemFixture.UPDATED_QUANTITY))
                .body("totalCost", comparesEqualTo(PurchaseOrderItemFixture.UPDATED_TOTAL_COST));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingWithoutId() {
        request()
                .body(toJson(validInput()))
        .when()
                .patch(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo("O ID do item do pedido de compra é obrigatório para atualização."));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingUnknownPurchaseOrderItem() {
        PurchaseOrderItemInputDTO update = validInput();
        update.setId(UUID.randomUUID());

        request().body(toJson(update)).when().patch(PATH).then().statusCode(404);
    }

    @Test
    void shouldDeletePurchaseOrderItem() {
        String id = createItem();

        request().when().delete(PATH + "/{id}", id).then().statusCode(204);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingUnknownPurchaseOrderItem() {
        request().when().delete(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
    }

    private PurchaseOrderItemInputDTO validInput() {
        return PurchaseOrderItemFixture.validInput(UUID.fromString(purchaseOrderId), productId);
    }

    private String createItem() {
        return request()
                .body(toJson(validInput()))
        .when()
                .post(PATH)
        .then()
                .statusCode(201)
                .extract().path("id");
    }
}
