package com.br.manager.domain.shift_cachs.controller;

import com.br.manager.domain.shift_cachs.dto.ShiftSyncDTO;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import com.br.manager.domain.shift_cachs.fixture.ShiftFixture;
import com.br.manager.domain.shift_cachs.repository.ShiftRepository;
import com.br.manager.domain.shift_cachs.support.ShiftATestData;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(ShiftATestData.class)
class ShiftSynchronizeControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/shift-synchronize";

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
    void shouldInsertReceivedShift() {
        UUID shiftId = UUID.randomUUID();

        request().body(List.of(ShiftFixture.openSync(shiftId, stationId, userId))).when().post(PATH)
                .then().statusCode(200);

        Shift saved = shiftRepository.findById(shiftId).orElseThrow();
        assertThat(saved.getStationId()).isEqualTo(stationId);
        assertThat(saved.getCashierUserId()).isEqualTo(userId);
        assertThat(saved.getName()).isEqualTo(ShiftFixture.NAME);
        assertThat(saved.getStatus()).isEqualTo(ShiftStatusEnum.OPEN);
        assertThat(saved.getOpeningCash()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldUpdateShiftWhenItIsResent() {
        UUID shiftId = UUID.randomUUID();
        sync(ShiftFixture.openSync(shiftId, stationId, userId));

        request().body(List.of(ShiftFixture.closedSync(shiftId, stationId, userId))).when().post(PATH)
                .then().statusCode(200);

        Shift saved = shiftRepository.findById(shiftId).orElseThrow();
        assertThat(shiftRepository.count()).isEqualTo(1);
        assertThat(saved.getStatus()).isEqualTo(ShiftStatusEnum.CLOSED);
        assertThat(saved.getClosedAt()).isEqualTo(ShiftFixture.CLOSED_AT);
        assertThat(saved.getClosedBy()).isEqualTo(userId);
    }

    @Test
    void shouldAcceptEmptyList() {
        request().body(List.of()).when().post(PATH).then().statusCode(200);

        assertThat(shiftRepository.count()).isZero();
    }

    @Test
    void shouldReturnBadRequestWhenRequiredFieldIsMissing() {
        ShiftSyncDTO invalid = ShiftFixture.openSync(UUID.randomUUID(), null, userId);

        request().body(List.of(invalid)).when().post(PATH).then().statusCode(400);

        assertThat(shiftRepository.count()).isZero();
    }

    private void sync(ShiftSyncDTO dto) {
        request().body(List.of(dto)).when().post(PATH).then().statusCode(200);
    }
}
