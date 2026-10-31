package com.br.manager.domain.operational.controller;

import com.br.manager.domain.common.entity.SynchronizeNameTableEnum;
import com.br.manager.domain.operational.dto.NozzleInputDTO;
import com.br.manager.domain.operational.fixture.NozzleFixture;
import com.br.manager.domain.operational.support.OperationalAIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

class NozzleSynchronizeControllerIT extends OperationalAIntegrationTest {

    private static final String PATH = "/nozzle-synchronize";
    private static final String TABLE = "nozzles";
    private static final SynchronizeNameTableEnum SYNC_TABLE = SynchronizeNameTableEnum.NOZZLE;

    private UUID fuelId;
    private UUID tankId;
    private UUID pumpId;

    @BeforeEach
    void createPrerequisites() {
        fuelId = createFuelProduct();
        tankId = createTank(fuelId);
        pumpId = createPump();
    }

    @Test
    void shouldReturnAllRecordsWhenTableWasNeverSynchronized() {
        String id = create(NOZZLES, NozzleFixture.validInput(pumpId, tankId, fuelId));

        request().when().get(PATH)
                .then().statusCode(200).body("$", hasSize(1)).body("[0].id", equalTo(id));
    }

    @Test
    void shouldReturnOnlyRecordsChangedSinceLastSynchronize() {
        LocalDateTime now = LocalDateTime.now();
        String changed = create(NOZZLES, NozzleFixture.validInput(pumpId, tankId, fuelId));
        String unchanged = create(NOZZLES, otherNozzle());
        testData.backdate(TABLE, unchanged, now.minusDays(2));
        testData.setLastSynchronize(SYNC_TABLE, now.minusDays(1));

        request().when().get(PATH)
                .then().statusCode(200).body("id", contains(changed));
    }

    @Test
    void shouldReturnRecordsDeletedSinceLastSynchronize() {
        LocalDateTime now = LocalDateTime.now();
        String deleted = create(NOZZLES, NozzleFixture.validInput(pumpId, tankId, fuelId));
        testData.backdate(TABLE, deleted, now.minusDays(2));
        testData.setLastSynchronize(SYNC_TABLE, now.minusDays(1));
        delete(NOZZLES, deleted);

        request().when().get(PATH)
                .then().statusCode(200).body("id", contains(deleted)).body("[0].active", equalTo(false));
    }

    @Test
    void shouldReturnEmptyWhenNothingChangedAfterLastSynchronize() {
        create(NOZZLES, NozzleFixture.validInput(pumpId, tankId, fuelId));
        testData.setLastSynchronize(SYNC_TABLE, LocalDateTime.now().plusDays(1));

        request().when().get(PATH)
                .then().statusCode(200).body("$", hasSize(0));
    }

    private NozzleInputDTO otherNozzle() {
        NozzleInputDTO input = NozzleFixture.validInput(pumpId, tankId, fuelId);
        input.setCode(NozzleFixture.OTHER_CODE);
        return input;
    }
}
