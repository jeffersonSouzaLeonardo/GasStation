package com.br.manager.domain.shift_cachs.controller;

import com.br.manager.domain.shift_cachs.dto.CashSessionCloseInputDTO;
import com.br.manager.domain.shift_cachs.dto.CashSessionInputDTO;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.fixture.CashSessionFixture;
import com.br.manager.domain.shift_cachs.support.ShiftBTestData;
import com.br.manager.support.integration.AbstractIntegrationTest;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@Import(ShiftBTestData.class)
class CashSessionControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/api/v1/cash-sessions";
    private static final String OPEN_PATH = PATH + "/open";
    private static final String CLOSE_PATH = PATH + "/{id}/close";
    private static final String ID_PATH = PATH + "/{id}";

    @Autowired
    private ShiftBTestData testData;

    private Shift openShift;
    private UUID cashierUserId;

    @BeforeEach
    void setUp() {
        openShift = testData.createOpenShift();
        cashierUserId = testData.createUser();
    }

    private RequestSpecification decimalRequest() {
        return request().config(ShiftBTestData.DECIMAL_JSON);
    }

    private String openSession(UUID shiftId, UUID userId) {
        return request()
                .body(CashSessionFixture.validInput(shiftId, userId))
                .when().post(OPEN_PATH)
                .then().statusCode(201)
                .extract().path("id");
    }

    @Nested
    class Open {

        @Test
        void shouldOpenCashSession() {
            decimalRequest()
                    .body(CashSessionFixture.validInput(openShift.getId(), cashierUserId))
            .when()
                    .post(OPEN_PATH)
            .then()
                    .statusCode(201)
                    .body("id", notNullValue())
                    .body("shiftId", equalTo(openShift.getId().toString()))
                    .body("cashierUserId", equalTo(cashierUserId.toString()))
                    .body("role", equalTo(CashSessionFixture.ROLE.name()))
                    .body("status", equalTo("OPEN"))
                    .body("openingCash", comparesEqualTo(CashSessionFixture.OPENING_CASH))
                    .body("openedAt", notNullValue())
                    .body("closedAt", nullValue())
                    .body("notes", equalTo(CashSessionFixture.NOTES));
        }

        @Test
        void shouldReturnBadRequestWhenRequiredFieldsAreMissing() {
            CashSessionInputDTO input = CashSessionFixture.validInput(null, cashierUserId);
            input.setOpeningCash(null);

            request().body(input).when().post(OPEN_PATH).then().statusCode(400);
        }

        @Test
        void shouldReturnBadRequestWhenOpeningCashIsNegative() {
            CashSessionInputDTO input = CashSessionFixture.validInput(openShift.getId(), cashierUserId);
            input.setOpeningCash(new BigDecimal("-1.00"));

            request().body(input).when().post(OPEN_PATH).then().statusCode(400);
        }

        @Test
        void shouldReturnBadRequestWhenShiftIsClosed() {
            Shift closedShift = testData.createClosedShift();

            request()
                    .body(CashSessionFixture.validInput(closedShift.getId(), cashierUserId))
                    .when().post(OPEN_PATH)
                    .then().statusCode(400);
        }

        @Test
        void shouldReturnBadRequestWhenShiftDoesNotExist() {
            request()
                    .body(CashSessionFixture.validInput(UUID.randomUUID(), cashierUserId))
                    .when().post(OPEN_PATH)
                    .then().statusCode(400);
        }

        @Test
        void shouldReturnNotFoundWhenCashierUserDoesNotExist() {
            request()
                    .body(CashSessionFixture.validInput(openShift.getId(), UUID.randomUUID()))
                    .when().post(OPEN_PATH)
                    .then().statusCode(404);
        }

        @Test
        void shouldReturnNotFoundWhenCashierUserIsInactive() {
            request()
                    .body(CashSessionFixture.validInput(openShift.getId(), testData.createInactiveUser()))
                    .when().post(OPEN_PATH)
                    .then().statusCode(404);
        }

        @Test
        void shouldReturnBadRequestWhenCashierAlreadyHasOpenSession() {
            openSession(openShift.getId(), cashierUserId);

            request()
                    .body(CashSessionFixture.validInput(openShift.getId(), cashierUserId))
                    .when().post(OPEN_PATH)
                    .then().statusCode(400);
        }
    }

    @Nested
    class Close {

        @Test
        void shouldCloseSessionWithTotals() {
            String id = openSession(openShift.getId(), cashierUserId);

            decimalRequest()
                    .body(CashSessionFixture.closeInput())
            .when()
                    .post(CLOSE_PATH, id)
            .then()
                    .statusCode(200)
                    .body("id", equalTo(id))
                    .body("status", equalTo("CLOSED"))
                    .body("closedAt", notNullValue())
                    .body("totalCash", comparesEqualTo(CashSessionFixture.TOTAL_CASH))
                    .body("totalCard", comparesEqualTo(CashSessionFixture.TOTAL_CARD))
                    .body("totalPix", comparesEqualTo(CashSessionFixture.TOTAL_PIX))
                    .body("totalWithdrawal", comparesEqualTo(CashSessionFixture.TOTAL_WITHDRAWAL))
                    .body("notes", equalTo(CashSessionFixture.CLOSING_NOTES));
        }

        @Test
        void shouldCloseSessionWithoutBody() {
            String id = openSession(openShift.getId(), cashierUserId);

            request()
            .when()
                    .post(CLOSE_PATH, id)
            .then()
                    .statusCode(200)
                    .body("status", equalTo("CLOSED"))
                    .body("totalCash", nullValue())
                    .body("notes", equalTo(CashSessionFixture.NOTES));
        }

        @Test
        void shouldAllowCashierToOpenNewSessionAfterClosing() {
            String id = openSession(openShift.getId(), cashierUserId);
            request().when().post(CLOSE_PATH, id).then().statusCode(200);

            request()
                    .body(CashSessionFixture.validInput(openShift.getId(), cashierUserId))
                    .when().post(OPEN_PATH)
                    .then().statusCode(201);
        }

        @Test
        void shouldReturnNotFoundWhenSessionIsAlreadyClosed() {
            String id = openSession(openShift.getId(), cashierUserId);
            request().when().post(CLOSE_PATH, id).then().statusCode(200);

            request().when().post(CLOSE_PATH, id).then().statusCode(404);
        }

        @Test
        void shouldReturnNotFoundWhenSessionDoesNotExist() {
            request()
                    .body(CashSessionFixture.closeInput())
                    .when().post(CLOSE_PATH, UUID.randomUUID())
                    .then().statusCode(404);
        }

        @Test
        void shouldReturnBadRequestWhenTotalIsNegative() {
            String id = openSession(openShift.getId(), cashierUserId);
            CashSessionCloseInputDTO input = CashSessionFixture.closeInput();
            input.setTotalCash(new BigDecimal("-0.01"));

            request().body(input).when().post(CLOSE_PATH, id).then().statusCode(400);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldFindSessionById() {
            String id = openSession(openShift.getId(), cashierUserId);

            request()
            .when()
                    .get(ID_PATH, id)
            .then()
                    .statusCode(200)
                    .body("id", equalTo(id))
                    .body("shiftId", equalTo(openShift.getId().toString()))
                    .body("status", equalTo("OPEN"))
                    .body("createdAt", notNullValue());
        }

        @Test
        void shouldReturnNotFoundForUnknownSession() {
            request().when().get(ID_PATH, UUID.randomUUID()).then().statusCode(404);
        }

        @Test
        void shouldListSessionsOfShift() {
            String firstId = openSession(openShift.getId(), cashierUserId);
            String secondId = openSession(openShift.getId(), testData.createUser());
            openSession(testData.createOpenShift().getId(), testData.createUser());

            request()
                    .queryParam("shiftId", openShift.getId())
            .when()
                    .get(PATH)
            .then()
                    .statusCode(200)
                    .body("$", hasSize(2))
                    .body("id", containsInAnyOrder(firstId, secondId));
        }

        @Test
        void shouldReturnEmptyListForShiftWithoutSessions() {
            request()
                    .queryParam("shiftId", openShift.getId())
                    .when().get(PATH)
                    .then().statusCode(200).body("$", hasSize(0));
        }

        @Test
        void shouldReturnBadRequestWhenShiftIdParameterIsMissing() {
            request().when().get(PATH).then().statusCode(400);
        }
    }
}
