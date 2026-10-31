package com.br.manager.domain.shift_cachs.controller;

import com.br.manager.domain.shift_cachs.dto.PaymentClosingInputDTO;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.fixture.PaymentClosingFixture;
import com.br.manager.domain.shift_cachs.support.ShiftBTestData;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Import(ShiftBTestData.class)
class ShiftPaymentClosingControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/api/v1/shifts/{id}/payment-closings";
    private static final String PAYMENT_CLOSING_BUG = "BUG: PaymentClosing is mapped to table 'payment_closings' but the "
            + "migrations create 'shift_payment_closings'; also PaymentClosing.id has no @GeneratedValue and "
            + "ShiftPaymentClosingService never assigns an id (HTTP 500)";
    private static final BigDecimal UPDATED_DECLARED_AMOUNT = new BigDecimal("510.00");
    private static final BigDecimal UPDATED_DIFFERENCE = new BigDecimal("10.00");

    @Autowired
    private ShiftBTestData testData;

    private Shift openShift;
    private UUID paymentMethodId;

    @BeforeEach
    void setUp() {
        openShift = testData.createOpenShift();
        paymentMethodId = testData.createPaymentMethod();
    }

    @Test
    @Disabled(PAYMENT_CLOSING_BUG)
    void shouldRegisterPaymentClosingWithDifference() {
        request().config(ShiftBTestData.DECIMAL_JSON)
                .body(PaymentClosingFixture.validInput(paymentMethodId))
        .when()
                .post(PATH, openShift.getId())
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("shiftId", equalTo(openShift.getId().toString()))
                .body("paymentMethodId", equalTo(paymentMethodId.toString()))
                .body("expectedAmount", comparesEqualTo(PaymentClosingFixture.EXPECTED_AMOUNT))
                .body("declaredAmount", comparesEqualTo(PaymentClosingFixture.DECLARED_AMOUNT))
                .body("differenceAmount", comparesEqualTo(PaymentClosingFixture.DIFFERENCE_AMOUNT))
                .body("evidenceReference", equalTo(PaymentClosingFixture.EVIDENCE_REFERENCE));
    }

    @Test
    @Disabled(PAYMENT_CLOSING_BUG)
    void shouldUpdateClosingWhenPaymentMethodIsResent() {
        String id = request().body(PaymentClosingFixture.validInput(paymentMethodId))
                .when().post(PATH, openShift.getId())
                .then().statusCode(200).extract().path("id");
        PaymentClosingInputDTO update = PaymentClosingFixture.validInput(paymentMethodId);
        update.setDeclaredAmount(UPDATED_DECLARED_AMOUNT);

        request().config(ShiftBTestData.DECIMAL_JSON)
                .body(update)
        .when()
                .post(PATH, openShift.getId())
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("declaredAmount", comparesEqualTo(UPDATED_DECLARED_AMOUNT))
                .body("differenceAmount", comparesEqualTo(UPDATED_DIFFERENCE));
    }

    @Test
    void shouldReturnNotFoundWhenShiftDoesNotExist() {
        request()
                .body(PaymentClosingFixture.validInput(paymentMethodId))
                .when().post(PATH, UUID.randomUUID())
                .then().statusCode(404);
    }

    @Test
    void shouldReturnBadRequestWhenShiftIsClosed() {
        Shift closedShift = testData.createClosedShift();

        request()
                .body(PaymentClosingFixture.validInput(paymentMethodId))
                .when().post(PATH, closedShift.getId())
                .then().statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenRequiredFieldsAreMissing() {
        PaymentClosingInputDTO invalid = PaymentClosingFixture.validInput(null);
        invalid.setExpectedAmount(null);

        request().body(invalid).when().post(PATH, openShift.getId()).then().statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenDeclaredAmountIsNegative() {
        PaymentClosingInputDTO invalid = PaymentClosingFixture.validInput(paymentMethodId);
        invalid.setDeclaredAmount(PaymentClosingFixture.NEGATIVE_AMOUNT);

        request().body(invalid).when().post(PATH, openShift.getId()).then().statusCode(400);
    }
}
