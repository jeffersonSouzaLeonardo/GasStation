package com.br.manager.domain.operational.controller;

import com.br.manager.domain.operational.dto.SupplyRequestDTO;
import com.br.manager.domain.operational.entity.Supply;
import com.br.manager.domain.operational.fixture.SupplyFixture;
import com.br.manager.domain.operational.repository.SupplyRepository;
import com.br.manager.domain.operational.support.OperationalBTestData;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(OperationalBTestData.class)
class SupplySynchronizeControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/supply-synchronize";

    @Autowired
    private OperationalBTestData testData;

    @Autowired
    private SupplyRepository supplyRepository;

    private UUID tankId;
    private UUID pumpId;

    @BeforeEach
    void createPrerequisites() {
        tankId = testData.createTank();
        pumpId = testData.createPump();
    }

    @Test
    void shouldPersistReceivedSuppliesKeepingPdvIds() {
        SupplyRequestDTO first = SupplyFixture.validRequest(tankId, pumpId);
        SupplyRequestDTO second = SupplyFixture.validRequest(tankId, pumpId);

        request().body(List.of(asJson(first), asJson(second))).when().post(PATH).then().statusCode(200);

        assertThat(supplyRepository.findAll())
                .extracting(Supply::getId)
                .containsExactlyInAnyOrder(first.getId(), second.getId());
    }

    @Test
    void shouldStoreSupplyValues() {
        SupplyRequestDTO supply = SupplyFixture.validRequest(tankId, pumpId);

        request().body(List.of(asJson(supply))).when().post(PATH).then().statusCode(200);

        Supply stored = supplyRepository.findById(supply.getId()).orElseThrow();
        assertThat(stored.getTankId()).isEqualTo(tankId);
        assertThat(stored.getPumpId()).isEqualTo(pumpId);
        assertThat(stored.getDataTime()).isEqualTo(SupplyFixture.DATA_TIME);
        assertThat(stored.getVolume()).isEqualByComparingTo(SupplyFixture.VOLUME);
        assertThat(stored.getTotalValue()).isEqualByComparingTo(SupplyFixture.TOTAL_VALUE);
    }

    @Test
    void shouldBeIdempotentWhenBatchIsResent() {
        SupplyRequestDTO supply = SupplyFixture.validRequest(tankId, pumpId);
        request().body(List.of(asJson(supply))).when().post(PATH).then().statusCode(200);
        supply.setTotalValue(SupplyFixture.UPDATED_TOTAL_VALUE);

        request().body(List.of(asJson(supply))).when().post(PATH).then().statusCode(200);

        assertThat(supplyRepository.count()).isEqualTo(1);
        assertThat(supplyRepository.findById(supply.getId()).orElseThrow().getTotalValue())
                .isEqualByComparingTo(SupplyFixture.UPDATED_TOTAL_VALUE);
    }

    @Test
    void shouldAcceptEmptyBatch() {
        request().body(List.of()).when().post(PATH).then().statusCode(200);

        assertThat(supplyRepository.count()).isZero();
    }

    @Test
    void shouldReturnBadRequestWhenMandatoryFieldIsMissing() {
        SupplyRequestDTO supply = SupplyFixture.validRequest(tankId, pumpId);
        supply.setVolume(null);

        request().body(List.of(asJson(supply))).when().post(PATH).then().statusCode(400);

        assertThat(supplyRepository.count()).isZero();
    }

    @Test
    void shouldReturnBadRequestWhenTankDoesNotExist() {
        SupplyRequestDTO supply = SupplyFixture.validRequest(UUID.randomUUID(), pumpId);

        request().body(List.of(asJson(supply))).when().post(PATH).then().statusCode(400);

        assertThat(supplyRepository.count()).isZero();
    }

    private Map<String, Object> asJson(SupplyRequestDTO supply) {
        Map<String, Object> json = new HashMap<>();
        json.put("id", supply.getId());
        json.put("dataTime", supply.getDataTime().toString());
        json.put("tankId", supply.getTankId());
        json.put("pumpId", supply.getPumpId());
        json.put("volume", supply.getVolume());
        json.put("totalValue", supply.getTotalValue());
        return json;
    }
}
