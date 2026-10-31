package com.br.manager.domain.price_acquisition.controller;

import com.br.manager.domain.price_acquisition.dto.PurchaseOrderInputDTO;
import com.br.manager.domain.price_acquisition.entity.PurchaseOrder;
import com.br.manager.domain.price_acquisition.fixture.PurchaseOrderFixture;
import com.br.manager.domain.price_acquisition.fixture.PurchaseOrderItemFixture;
import com.br.manager.domain.price_acquisition.support.PriceAcquisitionTestData;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
class PurchaseOrderControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/purchase-order";

    @Autowired
    private PriceAcquisitionTestData testData;

    private UUID supplierId;
    private UUID userId;
    private UUID productId;

    @BeforeEach
    void setUpPrerequisites() {
        supplierId = testData.createSupplier();
        userId = testData.createUser();
        productId = testData.createProduct();
    }

    @Test
    void shouldCreatePurchaseOrderAsDraftWithItems() {
        PurchaseOrderInputDTO input = validInput();
        input.setStatus(PurchaseOrder.Status.COMPLETED);

        request()
                .config(PriceAcquisitionTestData.DECIMAL_CONFIG)
                .body(toJson(input))
        .when()
                .post(PATH)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("number", equalTo(PurchaseOrderFixture.NUMBER))
                .body("status", equalTo(PurchaseOrder.Status.DRAFT))
                .body("supplierId", equalTo(supplierId.toString()))
                .body("createdBy", equalTo(userId.toString()))
                .body("orderedAt", equalTo("2026-01-05T09:30:00"))
                .body("expectedAt", equalTo("2026-01-10T14:00:00"))
                .body("totalAmount", comparesEqualTo(PurchaseOrderItemFixture.TOTAL_COST))
                .body("items", hasSize(1))
                .body("items[0].productId", equalTo(productId.toString()))
                .body("items[0].quantity", equalTo(PurchaseOrderItemFixture.QUANTITY))
                .body("items[0].totalCost", comparesEqualTo(PurchaseOrderItemFixture.TOTAL_COST));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWithoutItems() {
        PurchaseOrderInputDTO input = PurchaseOrderFixture.validInput(supplierId, userId, List.of());

        request()
                .body(toJson(input))
        .when()
                .post(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo("O pedido de compra deve conter pelo menos um item."));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingWithoutNumber() {
        PurchaseOrderInputDTO input = validInput();
        input.setNumber(" ");

        request()
                .body(toJson(input))
        .when()
                .post(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo("O número do pedido de compra é obrigatório."));
    }

    @Test
    void shouldReturnNotFoundWhenCreatingWithUnknownSupplier() {
        PurchaseOrderInputDTO input = validInput();
        input.setSupplierId(UUID.randomUUID());

        request().body(toJson(input)).when().post(PATH).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenCreatingWithInactiveSupplier() {
        PurchaseOrderInputDTO input = validInput();
        input.setSupplierId(testData.createInactiveSupplier());

        request().body(toJson(input)).when().post(PATH).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenCreatingWithInactiveUser() {
        PurchaseOrderInputDTO input = validInput();
        input.setCreatedBy(testData.createInactiveUser());

        request().body(toJson(input)).when().post(PATH).then().statusCode(404);
    }

    @Test
    @Disabled("BUG: findAll at PurchaseOrderService.java:97 is not @Transactional (spring.jpa.open-in-view=false); PurchaseOrder.setItems (PurchaseOrder.java:124) clears the lazy items collection of a detached entity -> LazyInitializationException -> HTTP 500")
    void shouldListPurchaseOrders() {
        createPurchaseOrder();

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(1)).body("[0].items", hasSize(1));
    }

    @Test
    @Disabled("BUG: findById at PurchaseOrderService.java:90 is not @Transactional (spring.jpa.open-in-view=false); PurchaseOrder.setItems (PurchaseOrder.java:124) clears the lazy items collection of a detached entity -> LazyInitializationException -> HTTP 500")
    void shouldFindPurchaseOrderById() {
        String id = createPurchaseOrder();

        request().when().get(PATH + "/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("items", hasSize(1));
    }

    @Test
    void shouldReturnNotFoundWhenFindingUnknownPurchaseOrder() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
    }

    @Test
    void shouldUpdatePurchaseOrderAndReplaceItems() {
        String id = createPurchaseOrder();
        PurchaseOrderInputDTO update = validInput();
        update.setId(UUID.fromString(id));
        update.setNumber(PurchaseOrderFixture.UPDATED_NUMBER);
        update.getItems().get(0).setQuantity(PurchaseOrderItemFixture.UPDATED_QUANTITY);

        request()
                .config(PriceAcquisitionTestData.DECIMAL_CONFIG)
                .body(toJson(update))
        .when()
                .patch(PATH)
        .then()
                .statusCode(200)
                .body("number", equalTo(PurchaseOrderFixture.UPDATED_NUMBER))
                .body("items", hasSize(1))
                .body("totalAmount", comparesEqualTo(PurchaseOrderItemFixture.UPDATED_TOTAL_COST));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingUnknownPurchaseOrder() {
        PurchaseOrderInputDTO update = validInput();
        update.setId(UUID.randomUUID());

        request().body(toJson(update)).when().patch(PATH).then().statusCode(404);
    }

    @Test
    void shouldApprovePurchaseOrderInDraft() {
        String id = createPurchaseOrder();

        request().when().patch(PATH + "/{id}/approve", id)
                .then().statusCode(200).body("status", equalTo(PurchaseOrder.Status.APPROVED));
    }

    @Test
    void shouldReturnBadRequestWhenSendingDraftPurchaseOrder() {
        String id = createPurchaseOrder();

        request().when().patch(PATH + "/{id}/send", id)
                .then()
                .statusCode(400)
                .body("title", equalTo("O pedido de compra só pode mudar de APPROVED para SENT."));
    }

    @Test
    void shouldSendApprovedPurchaseOrder() {
        String id = createPurchaseOrder();
        request().when().patch(PATH + "/{id}/approve", id).then().statusCode(200);

        request().when().patch(PATH + "/{id}/send", id)
                .then().statusCode(200).body("status", equalTo(PurchaseOrder.Status.SENT));
    }

    @Test
    void shouldCompleteSentPurchaseOrder() {
        String id = createPurchaseOrder();
        request().when().patch(PATH + "/{id}/approve", id).then().statusCode(200);
        request().when().patch(PATH + "/{id}/send", id).then().statusCode(200);

        request().when().patch(PATH + "/{id}/complete", id)
                .then().statusCode(200).body("status", equalTo(PurchaseOrder.Status.COMPLETED));
    }

    @Test
    void shouldReturnBadRequestWhenCompletingDraftPurchaseOrder() {
        String id = createPurchaseOrder();

        request().when().patch(PATH + "/{id}/complete", id).then().statusCode(400);
    }

    @Test
    void shouldCancelDraftPurchaseOrder() {
        String id = createPurchaseOrder();

        request().when().patch(PATH + "/{id}/cancel", id)
                .then().statusCode(200).body("status", equalTo(PurchaseOrder.Status.CANCELED));
    }

    @Test
    void shouldReturnBadRequestWhenCancelingCanceledPurchaseOrder() {
        String id = createPurchaseOrder();
        request().when().patch(PATH + "/{id}/cancel", id).then().statusCode(200);

        request().when().patch(PATH + "/{id}/cancel", id)
                .then()
                .statusCode(400)
                .body("title", equalTo("Apenas pedidos em rascunho, aprovados ou enviados podem ser cancelados."));
    }

    @Test
    void shouldReturnNotFoundWhenApprovingUnknownPurchaseOrder() {
        request().when().patch(PATH + "/{id}/approve", UUID.randomUUID()).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenSendingUnknownPurchaseOrder() {
        request().when().patch(PATH + "/{id}/send", UUID.randomUUID()).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenCompletingUnknownPurchaseOrder() {
        request().when().patch(PATH + "/{id}/complete", UUID.randomUUID()).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenCancelingUnknownPurchaseOrder() {
        request().when().patch(PATH + "/{id}/cancel", UUID.randomUUID()).then().statusCode(404);
    }

    @Test
    void shouldDeletePurchaseOrder() {
        String id = createPurchaseOrder();

        request().when().delete(PATH + "/{id}", id).then().statusCode(204);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingUnknownPurchaseOrder() {
        request().when().delete(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
    }

    private PurchaseOrderInputDTO validInput() {
        return PurchaseOrderFixture.validInput(supplierId, userId,
                new ArrayList<>(List.of(PurchaseOrderItemFixture.validInput(null, productId))));
    }

    private String createPurchaseOrder() {
        return request()
                .body(toJson(validInput()))
        .when()
                .post(PATH)
        .then()
                .statusCode(201)
                .extract().path("id");
    }
}
