package com.br.manager.domain.operational.controller;

import com.br.manager.domain.common.entity.SynchronizeNameTableEnum;
import com.br.manager.domain.operational.fixture.ProductFixture;
import com.br.manager.domain.operational.support.OperationalAIntegrationTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

class ProductSynchronizeControllerIT extends OperationalAIntegrationTest {

    private static final String PATH = "/product-synchronize";
    private static final String TABLE = "products";
    private static final SynchronizeNameTableEnum SYNC_TABLE = SynchronizeNameTableEnum.PRODUCT;

    @Test
    void shouldReturnAllRecordsWhenTableWasNeverSynchronized() {
        String id = createProduct(ProductFixture.fuelInput());

        request().when().get(PATH)
                .then().statusCode(200).body("$", hasSize(1)).body("[0].id", equalTo(id));
    }

    @Test
    void shouldReturnOnlyRecordsChangedSinceLastSynchronize() {
        LocalDateTime now = LocalDateTime.now();
        String changed = createProduct(ProductFixture.fuelInput());
        String unchanged = createProduct(ProductFixture.convenienceInput());
        testData.backdate(TABLE, unchanged, now.minusDays(2));
        testData.setLastSynchronize(SYNC_TABLE, now.minusDays(1));

        request().when().get(PATH)
                .then().statusCode(200).body("id", contains(changed));
    }

    @Test
    void shouldReturnRecordsDeletedSinceLastSynchronize() {
        LocalDateTime now = LocalDateTime.now();
        String deleted = createProduct(ProductFixture.fuelInput());
        testData.backdate(TABLE, deleted, now.minusDays(2));
        testData.setLastSynchronize(SYNC_TABLE, now.minusDays(1));
        delete(PRODUCTS, deleted);

        request().when().get(PATH)
                .then().statusCode(200).body("id", contains(deleted)).body("[0].active", equalTo(false));
    }

    @Test
    void shouldReturnEmptyWhenNothingChangedAfterLastSynchronize() {
        createProduct(ProductFixture.fuelInput());
        testData.setLastSynchronize(SYNC_TABLE, LocalDateTime.now().plusDays(1));

        request().when().get(PATH)
                .then().statusCode(200).body("$", hasSize(0));
    }
}
