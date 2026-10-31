package com.br.manager.domain.price_acquisition.controller;

import com.br.manager.domain.price_acquisition.dto.GoodsReceiptInputDTO;
import com.br.manager.domain.price_acquisition.entity.GoodsReceipt;
import com.br.manager.domain.price_acquisition.fixture.GoodsReceiptFixture;
import com.br.manager.domain.price_acquisition.fixture.GoodsReceiptItemFixture;
import com.br.manager.domain.price_acquisition.fixture.PurchaseOrderFixture;
import com.br.manager.domain.price_acquisition.fixture.PurchaseOrderItemFixture;
import com.br.manager.domain.price_acquisition.support.PriceAcquisitionTestData;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.br.manager.domain.price_acquisition.support.PriceAcquisitionTestData.toJson;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@Import(PriceAcquisitionTestData.class)
class GoodsReceiptControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/goods-receipt";

    @Autowired
    private PriceAcquisitionTestData testData;

    private UUID supplierId;
    private UUID userId;
    private UUID productId;
    private UUID tankId;

    @BeforeEach
    void setUpPrerequisites() {
        supplierId = testData.createSupplier();
        userId = testData.createUser();
        productId = testData.createProduct();
        tankId = testData.createTank(productId);
    }

    @Test
    void shouldCreateGoodsReceiptWithItems() {
        request()
                .config(PriceAcquisitionTestData.DECIMAL_CONFIG)
                .body(toJson(validInput()))
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("supplierId", equalTo(supplierId.toString()))
                .body("receivedBy", equalTo(userId.toString()))
                .body("invoiceNumber", equalTo(GoodsReceiptFixture.INVOICE_NUMBER))
                .body("invoiceKey", equalTo(GoodsReceiptFixture.INVOICE_KEY))
                .body("receivedAt", equalTo("2026-01-10T15:45:00"))
                .body("status", equalTo(GoodsReceipt.Status.RECEIVED))
                .body("freightAmount", comparesEqualTo(GoodsReceiptFixture.FREIGHT_AMOUNT))
                .body("otherCosts", comparesEqualTo(GoodsReceiptFixture.OTHER_COSTS))
                .body("items", hasSize(1))
                .body("items[0].productId", equalTo(productId.toString()))
                .body("items[0].tankId", equalTo(tankId.toString()))
                .body("items[0].quantity", comparesEqualTo(GoodsReceiptItemFixture.QUANTITY));
    }

    @Test
    void shouldCreateGoodsReceiptAsDraftWhenStatusIsBlank() {
        GoodsReceiptInputDTO input = validInput();
        input.setStatus(null);

        request()
                .body(toJson(input))
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .body("status", equalTo(GoodsReceipt.Status.DRAFT));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWithoutItems() {
        request()
                .body(toJson(GoodsReceiptFixture.validInput(supplierId, userId, List.of())))
        .when()
                .post(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo("O recebimento de mercadorias deve conter pelo menos um item."));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWithoutInvoiceKey() {
        GoodsReceiptInputDTO input = validInput();
        input.setInvoiceKey(null);

        request()
                .body(toJson(input))
        .when()
                .post(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo("A chave da nota fiscal é obrigatória."));
    }

    @Test
    void shouldReturnNotFoundWhenCreatingWithInactiveSupplier() {
        GoodsReceiptInputDTO input = validInput();
        input.setSupplierId(testData.createInactiveSupplier());

        request().body(toJson(input)).when().post(PATH).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenCreatingWithInactiveUser() {
        GoodsReceiptInputDTO input = validInput();
        input.setReceivedBy(testData.createInactiveUser());

        request().body(toJson(input)).when().post(PATH).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenCreatingWithUnknownPurchaseOrder() {
        GoodsReceiptInputDTO input = validInput();
        input.setPurchaseOrderId(UUID.randomUUID());

        request().body(toJson(input)).when().post(PATH).then().statusCode(404);
    }

    @Test
    void shouldListGoodsReceipts() {
        createGoodsReceipt(validInput());

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(1));
    }

    @Test
    void shouldListGoodsReceiptsBySupplier() {
        createGoodsReceipt(validInput());

        request().when().get(PATH + "/by-supplier/{id}", supplierId)
                .then().statusCode(200).body("$", hasSize(1)).body("[0].supplierId", equalTo(supplierId.toString()));
        request().when().get(PATH + "/by-supplier/{id}", UUID.randomUUID())
                .then().statusCode(200).body("$", hasSize(0));
    }

    @Test
    void shouldListGoodsReceiptsByPurchaseOrder() {
        String purchaseOrderId = createPurchaseOrder();
        GoodsReceiptInputDTO input = validInput();
        input.setPurchaseOrderId(UUID.fromString(purchaseOrderId));
        createGoodsReceipt(input);

        request().when().get(PATH + "/by-purchase-order/{id}", purchaseOrderId)
                .then().statusCode(200).body("$", hasSize(1)).body("[0].purchaseOrderId", equalTo(purchaseOrderId));
    }

    @Test
    void shouldFindGoodsReceiptById() {
        String id = createGoodsReceipt(validInput());

        request().when().get(PATH + "/{id}", id)
                .then().statusCode(200).body("id", equalTo(id)).body("items", hasSize(1));
    }

    @Test
    void shouldReturnNotFoundWhenFindingUnknownGoodsReceipt() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
    }

    @Test
    void shouldUpdateGoodsReceiptAndReplaceItems() {
        String id = createGoodsReceipt(validInput());
        GoodsReceiptInputDTO update = validInput();
        update.setId(UUID.fromString(id));
        update.setInvoiceNumber(GoodsReceiptFixture.UPDATED_INVOICE_NUMBER);
        update.getItems().get(0).setQuantity(GoodsReceiptItemFixture.UPDATED_QUANTITY);

        request()
                .config(PriceAcquisitionTestData.DECIMAL_CONFIG)
                .body(toJson(update))
        .when()
                .patch(PATH)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("invoiceNumber", equalTo(GoodsReceiptFixture.UPDATED_INVOICE_NUMBER))
                .body("items", hasSize(1))
                .body("items[0].quantity", comparesEqualTo(GoodsReceiptItemFixture.UPDATED_QUANTITY));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingWithoutId() {
        request()
                .body(toJson(validInput()))
        .when()
                .patch(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo("O ID do recebimento de mercadorias é obrigatório para atualização."));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingUnknownGoodsReceipt() {
        GoodsReceiptInputDTO update = validInput();
        update.setId(UUID.randomUUID());

        request().body(toJson(update)).when().patch(PATH).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingWithUnknownSupplier() {
        String id = createGoodsReceipt(validInput());
        GoodsReceiptInputDTO update = validInput();
        update.setId(UUID.fromString(id));
        update.setSupplierId(UUID.randomUUID());

        request().body(toJson(update)).when().patch(PATH).then().statusCode(404);
    }

    @Test
    void shouldDeleteGoodsReceipt() {
        String id = createGoodsReceipt(validInput());

        request().when().delete(PATH + "/{id}", id).then().statusCode(200);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingUnknownGoodsReceipt() {
        request().when().delete(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
    }

    private GoodsReceiptInputDTO validInput() {
        return GoodsReceiptFixture.validInput(supplierId, userId,
                new ArrayList<>(List.of(GoodsReceiptItemFixture.validInput(null, productId, tankId))));
    }

    private String createGoodsReceipt(GoodsReceiptInputDTO input) {
        return request()
                .body(toJson(input))
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .extract().path("id");
    }

    private String createPurchaseOrder() {
        return request()
                .body(toJson(PurchaseOrderFixture.validInput(supplierId, userId,
                        List.of(PurchaseOrderItemFixture.validInput(null, productId)))))
        .when()
                .post("/purchase-order")
        .then()
                .statusCode(201)
                .extract().path("id");
    }
}
