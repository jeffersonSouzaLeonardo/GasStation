package com.br.manager.domain.operational.controller;

import com.br.manager.domain.common.entity.SynchronizeNameTableEnum;
import com.br.manager.domain.operational.fixture.PumpFixture;
import com.br.manager.domain.operational.support.OperationalAIntegrationTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

class PumpSynchronizeControllerIT extends OperationalAIntegrationTest {

    private static final String PATH = "/pump-synchronize";
    private static final String TABLE = "pumps";
    private static final SynchronizeNameTableEnum SYNC_TABLE = SynchronizeNameTableEnum.PUMP;

    @Test
    void shouldReturnAllRecordsWhenTableWasNeverSynchronized() {
        String id = createPump(PumpFixture.validInput());

        request().when().get(PATH)
                .then().statusCode(200).body("$", hasSize(1)).body("[0].id", equalTo(id));
    }

    @Test
    void shouldReturnOnlyRecordsChangedSinceLastSynchronize() {
        LocalDateTime now = LocalDateTime.now();
        String changed = createPump(PumpFixture.validInput());
        String unchanged = createPump(PumpFixture.validInput(PumpFixture.OTHER_CODE));
        testData.backdate(TABLE, unchanged, now.minusDays(2));
        testData.setLastSynchronize(SYNC_TABLE, now.minusDays(1));

        request().when().get(PATH)
                .then().statusCode(200).body("id", contains(changed));
    }

    @Test
    void shouldReturnRecordsDeletedSinceLastSynchronize() {
        LocalDateTime now = LocalDateTime.now();
        String deleted = createPump(PumpFixture.validInput());
        testData.backdate(TABLE, deleted, now.minusDays(2));
        testData.setLastSynchronize(SYNC_TABLE, now.minusDays(1));
        delete(PUMPS, deleted);

        request().when().get(PATH)
                .then().statusCode(200).body("id", contains(deleted)).body("[0].active", equalTo(false));
    }

    @Test
    void shouldReturnEmptyWhenNothingChangedAfterLastSynchronize() {
        createPump(PumpFixture.validInput());
        testData.setLastSynchronize(SYNC_TABLE, LocalDateTime.now().plusDays(1));

        request().when().get(PATH)
                .then().statusCode(200).body("$", hasSize(0));
    }
}
