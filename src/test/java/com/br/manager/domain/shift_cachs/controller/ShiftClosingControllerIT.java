package com.br.manager.domain.shift_cachs.controller;

import com.br.manager.domain.shift_cachs.dto.ShiftCloseRequestDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftSyncDTO;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import com.br.manager.domain.shift_cachs.fixture.ShiftFixture;
import com.br.manager.domain.shift_cachs.repository.ShiftRepository;
import com.br.manager.domain.shift_cachs.support.ShiftATestData;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Import(ShiftATestData.class)
class ShiftClosingControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/api/v1/shifts/{id}/close";
    private static final String SYNC_PATH = "/shift-synchronize";

    @Autowired
    private ShiftATestData testData;

    @Autowired
    private ShiftRepository shiftRepository;

    private UUID stationId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        stationId = testData.createStation();
        userId = testData.createUser();
    }

    @Test
    void shouldCloseOpenShift() {
        UUID shiftId = syncShift(ShiftFixture.openSync(UUID.randomUUID(), stationId, userId));

        request()
                .body(ShiftFixture.closeRequest(userId))
        .when()
                .post(PATH, shiftId)
        .then()
                .statusCode(200)
                .body("id", equalTo(shiftId.toString()))
                .body("status", equalTo("CLOSED"))
                .body("closedBy", equalTo(userId.toString()))
                .body("closedAt", notNullValue());

        assertThat(shiftRepository.findById(shiftId).orElseThrow().getStatus()).isEqualTo(ShiftStatusEnum.CLOSED);
    }

    @Test
    void shouldReturnBadRequestWhenShiftIsAlreadyClosed() {
        UUID shiftId = syncShift(ShiftFixture.closedSync(UUID.randomUUID(), stationId, userId));

        request().body(ShiftFixture.closeRequest(userId)).when().post(PATH, shiftId).then().statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenClosedByIsMissing() {
        UUID shiftId = syncShift(ShiftFixture.openSync(UUID.randomUUID(), stationId, userId));

        request().body(new ShiftCloseRequestDTO()).when().post(PATH, shiftId).then().statusCode(400);
    }

    @Test
    void shouldReturnNotFoundWhenShiftIsUnknown() {
        request().body(ShiftFixture.closeRequest(userId)).when().post(PATH, UUID.randomUUID()).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenClosingUserIsUnknown() {
        UUID shiftId = syncShift(ShiftFixture.openSync(UUID.randomUUID(), stationId, userId));

        request().body(ShiftFixture.closeRequest(UUID.randomUUID())).when().post(PATH, shiftId).then().statusCode(404);

        assertThat(shiftRepository.findById(shiftId).orElseThrow().getStatus()).isEqualTo(ShiftStatusEnum.OPEN);
    }

    private UUID syncShift(ShiftSyncDTO dto) {
        request().body(List.of(dto)).when().post(SYNC_PATH).then().statusCode(200);
        return dto.getId();
    }
}
