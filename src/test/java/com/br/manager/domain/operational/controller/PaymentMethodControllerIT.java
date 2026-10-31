package com.br.manager.domain.operational.controller;

import com.br.manager.domain.operational.dto.PaymentMethodInputDTO;
import com.br.manager.domain.operational.fixture.PaymentMethodFixture;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class PaymentMethodControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/api/payment-methods";

    @Test
    void shouldCreatePaymentMethod() {
        request()
                .body(PaymentMethodFixture.validInput())
        .when()
                .post(PATH)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo(PaymentMethodFixture.NAME))
                .body("kind", equalTo(PaymentMethodFixture.KIND.name()))
                .body("requiresReference", equalTo(true))
                .body("settlementDays", equalTo(PaymentMethodFixture.SETTLEMENT_DAYS))
                .body("active", equalTo(true));
    }

    @Test
    void shouldApplyDefaultsWhenOptionalFieldsAreOmitted() {
        request()
                .body(PaymentMethodFixture.otherInput())
        .when()
                .post(PATH)
        .then()
                .statusCode(201)
                .body("requiresReference", equalTo(false))
                .body("settlementDays", equalTo(0))
                .body("active", equalTo(true));
    }

    @Test
    void shouldReturnBadRequestWhenKindIsMissing() {
        PaymentMethodInputDTO input = PaymentMethodFixture.validInput();
        input.setKind(null);

        request().body(input).when().post(PATH).then().statusCode(400);
    }

    @Test
    void shouldListActivePaymentMethods() {
        createPaymentMethod(PaymentMethodFixture.validInput());
        createPaymentMethod(PaymentMethodFixture.otherInput());

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldFindPaymentMethodById() {
        String id = createPaymentMethod(PaymentMethodFixture.validInput());

        request().when().get(PATH + "/{id}", id)
                .then().statusCode(200).body("id", equalTo(id)).body("name", equalTo(PaymentMethodFixture.NAME));
    }

    @Test
    void shouldSearchPaymentMethodsByName() {
        createPaymentMethod(PaymentMethodFixture.validInput());
        createPaymentMethod(PaymentMethodFixture.otherInput());

        request().queryParam("description", "visa").when().get(PATH + "/search")
                .then().statusCode(200).body("$", hasSize(1)).body("[0].name", equalTo(PaymentMethodFixture.NAME));
    }

    @Test
    void shouldReturnAllPaymentMethodsWhenSearchDescriptionIsBlank() {
        createPaymentMethod(PaymentMethodFixture.validInput());
        createPaymentMethod(PaymentMethodFixture.otherInput());

        request().queryParam("description", " ").when().get(PATH + "/search")
                .then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void shouldUpdatePaymentMethod() {
        String id = createPaymentMethod(PaymentMethodFixture.validInput());
        PaymentMethodInputDTO update = PaymentMethodFixture.otherInput();
        update.setId(UUID.fromString(id));

        request()
                .body(update)
        .when()
                .put(PATH)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo(PaymentMethodFixture.OTHER_NAME))
                .body("kind", equalTo("MONEY"));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingWithoutName() {
        String id = createPaymentMethod(PaymentMethodFixture.validInput());
        PaymentMethodInputDTO update = PaymentMethodFixture.validInput();
        update.setId(UUID.fromString(id));
        update.setName("");

        request().body(update).when().put(PATH).then().statusCode(400);
    }

    @Test
    void shouldInactivatePaymentMethodOnDelete() {
        String id = createPaymentMethod(PaymentMethodFixture.validInput());

        request().when().delete(PATH + "/{id}", id).then().statusCode(204);

        request().when().get(PATH + "/{id}", id).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundForUnknownPaymentMethod() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
        request().when().delete(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);

        PaymentMethodInputDTO update = PaymentMethodFixture.validInput();
        update.setId(UUID.randomUUID());
        request().body(update).when().put(PATH).then().statusCode(404);
    }

    private String createPaymentMethod(PaymentMethodInputDTO input) {
        return request().body(input).when().post(PATH).then().statusCode(201).extract().path("id");
    }
}
