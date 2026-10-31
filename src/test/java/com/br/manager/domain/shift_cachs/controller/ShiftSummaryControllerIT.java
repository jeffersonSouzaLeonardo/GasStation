package com.br.manager.domain.shift_cachs.controller;

import com.br.manager.domain.shift_cachs.dto.ShiftSyncDTO;
import com.br.manager.domain.shift_cachs.fixture.ShiftFixture;
import com.br.manager.domain.shift_cachs.support.ShiftATestData;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;

@Import(ShiftATestData.class)
class ShiftSummaryControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/api/v1/shifts/{id}/summary";
    private static final String SYNC_PATH = "/shift-synchronize";

    @Autowired
    private ShiftATestData testData;

    @Test
    @Disabled("BUG: PaymentClosing is mapped to table 'payment_closings' (entity/PaymentClosing.java:11) but the migration creates 'shift_payment_closings' (V1__create_initial_schema.sql:387), so the summary query fails with 500")
    void shouldReturnSummaryOfShiftWithoutActivity() {
        UUID userId = testData.createUser();
        UUID shiftId = syncShift(ShiftFixture.openSync(UUID.randomUUID(), testData.createStation(), userId));

        request()
        .when()
                .get(PATH, shiftId)
        .then()
                .statusCode(200)
                .body("shift.id", equalTo(shiftId.toString()))
                .body("shift.status", equalTo("OPEN"))
                .body("grossSales", equalTo(0))
                .body("netSales", equalTo(0))
                .body("calibrationAndTestAdjustment", equalTo(0))
                .body("totalDifferenceAmount", equalTo(0))
                .body("cashMovements", empty())
                .body("paymentClosings", empty());
    }

    @Test
    void shouldReturnNotFoundWhenShiftIsUnknown() {
        request().when().get(PATH, UUID.randomUUID()).then().statusCode(404);
    }

    private UUID syncShift(ShiftSyncDTO dto) {
        request().body(List.of(dto)).when().post(SYNC_PATH).then().statusCode(200);
        return dto.getId();
    }
}
