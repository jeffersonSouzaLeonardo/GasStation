package com.br.manager.domain.operational.controller;

import com.br.manager.domain.common.entity.SynchronizeNameTableEnum;
import com.br.manager.domain.operational.fixture.TankFixture;
import com.br.manager.domain.operational.support.OperationalAIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

class TankSynchronizeControllerIT extends OperationalAIntegrationTest {

    private static final String PATH = "/tank-synchronize";
    private static final String TABLE = "tanks";
    private static final SynchronizeNameTableEnum SYNC_TABLE = SynchronizeNameTableEnum.TANK;

    private UUID fuelId;

    @BeforeEach
    void createFuel() {
        fuelId = createFuelProduct();
    }

    @Test
    void shouldReturnAllRecordsWhenTableWasNeverSynchronized() {
        String id = createTank(TankFixture.validInput(fuelId));

        request().when().get(PATH)
                .then().statusCode(200).body("$", hasSize(1)).body("[0].id", equalTo(id));
    }

    @Test
    void shouldReturnOnlyRecordsChangedSinceLastSynchronize() {
        LocalDateTime now = LocalDateTime.now();
        String changed = createTank(TankFixture.validInput(fuelId));
        String unchanged = createTank(TankFixture.validInput(fuelId, TankFixture.OTHER_CODE));
        testData.backdate(TABLE, unchanged, now.minusDays(2));
        testData.setLastSynchronize(SYNC_TABLE, now.minusDays(1));

        request().when().get(PATH)
                .then().statusCode(200).body("id", contains(changed));
    }

    @Test
    void shouldReturnRecordsDeletedSinceLastSynchronize() {
        LocalDateTime now = LocalDateTime.now();
        String deleted = createTank(TankFixture.validInput(fuelId));
        testData.backdate(TABLE, deleted, now.minusDays(2));
        testData.setLastSynchronize(SYNC_TABLE, now.minusDays(1));
        delete(TANKS, deleted);

        request().when().get(PATH)
                .then().statusCode(200).body("id", contains(deleted)).body("[0].active", equalTo(false));
    }

    @Test
    void shouldReturnEmptyWhenNothingChangedAfterLastSynchronize() {
        createTank(TankFixture.validInput(fuelId));
        testData.setLastSynchronize(SYNC_TABLE, LocalDateTime.now().plusDays(1));

        request().when().get(PATH)
                .then().statusCode(200).body("$", hasSize(0));
    }
}
