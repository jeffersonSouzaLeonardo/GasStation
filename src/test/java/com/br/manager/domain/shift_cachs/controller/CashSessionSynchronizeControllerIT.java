package com.br.manager.domain.shift_cachs.controller;

import com.br.manager.domain.shift_cachs.dto.CashSessionSyncDTO;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.CashSessionStatusEnum;
import com.br.manager.domain.shift_cachs.enums.CashierRoleEnum;
import com.br.manager.domain.shift_cachs.fixture.CashSessionFixture;
import com.br.manager.domain.shift_cachs.support.ShiftBTestData;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;

@Import(ShiftBTestData.class)
class CashSessionSynchronizeControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/cash-session-synchronize";
    private static final String SESSION_PATH = "/api/v1/cash-sessions/{id}";
    private static final String SESSIONS_BY_SHIFT_PATH = "/api/v1/cash-sessions";
    private static final String UPDATED_NOTES = "Sessao reenviada pelo PDV";

    @Autowired
    private ShiftBTestData testData;

    private Shift shift;
    private CashSessionSyncDTO sync;

    @BeforeEach
    void setUp() {
        shift = testData.createOpenShift();
        sync = CashSessionFixture.closedSyncInput(UUID.randomUUID(), shift.getId(), testData.createUser());
    }

    private void synchronize(List<CashSessionSyncDTO> sessions) {
        request().body(sessions).when().post(PATH).then().statusCode(200);
    }

    @Test
    void shouldPersistSessionsReceivedFromPdv() {
        synchronize(List.of(sync));

        request().config(ShiftBTestData.DECIMAL_JSON)
        .when()
                .get(SESSION_PATH, sync.getId())
        .then()
                .statusCode(200)
                .body("id", equalTo(sync.getId().toString()))
                .body("shiftId", equalTo(shift.getId().toString()))
                .body("cashierUserId", equalTo(sync.getCashierUserId().toString()))
                .body("role", equalTo(CashierRoleEnum.PUMP_ATTENDANT.name()))
                .body("status", equalTo(CashSessionStatusEnum.CLOSED.name()))
                .body("openedAt", equalTo(CashSessionFixture.OPENED_AT.toString() + ":00"))
                .body("closedAt", equalTo(CashSessionFixture.CLOSED_AT.toString() + ":00"))
                .body("openingCash", comparesEqualTo(CashSessionFixture.OPENING_CASH))
                .body("totalCash", comparesEqualTo(CashSessionFixture.TOTAL_CASH))
                .body("totalCard", comparesEqualTo(CashSessionFixture.TOTAL_CARD))
                .body("totalPix", comparesEqualTo(CashSessionFixture.TOTAL_PIX))
                .body("totalWithdrawal", comparesEqualTo(CashSessionFixture.TOTAL_WITHDRAWAL))
                .body("notes", equalTo(CashSessionFixture.SYNC_NOTES));
    }

    @Test
    void shouldUpdateExistingSessionWhenBatchIsResent() {
        synchronize(List.of(sync));
        sync.setNotes(UPDATED_NOTES);
        sync.setTotalPix(null);

        synchronize(List.of(sync));

        request().queryParam("shiftId", shift.getId())
        .when()
                .get(SESSIONS_BY_SHIFT_PATH)
        .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].notes", equalTo(UPDATED_NOTES))
                .body("[0].totalPix", nullValue());
    }

    @Test
    void shouldAcceptEmptyBatch() {
        synchronize(List.of());
    }

    @Test
    void shouldReturnBadRequestWhenShiftOfSessionDoesNotExist() {
        sync.setShiftId(UUID.randomUUID());

        request().body(List.of(sync)).when().post(PATH).then().statusCode(400);

        request().when().get(SESSION_PATH, sync.getId()).then().statusCode(404);
    }

    @Test
    void shouldRollbackWholeBatchWhenOneSessionIsInvalid() {
        CashSessionSyncDTO orphan = CashSessionFixture.closedSyncInput(UUID.randomUUID(), UUID.randomUUID(), testData.createUser());

        request().body(List.of(sync, orphan)).when().post(PATH).then().statusCode(400);

        request().when().get(SESSION_PATH, sync.getId()).then().statusCode(404);
    }

    @Test
    void shouldReturnBadRequestWhenRequiredFieldIsMissing() {
        sync.setId(null);

        request().body(List.of(sync)).when().post(PATH).then().statusCode(400);
    }
}
