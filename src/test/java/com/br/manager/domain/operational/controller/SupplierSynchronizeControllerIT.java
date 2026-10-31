package com.br.manager.domain.operational.controller;

import com.br.manager.domain.common.entity.SynchronizeNameTableEnum;
import com.br.manager.domain.operational.fixture.SupplierFixture;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

class SupplierSynchronizeControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/supplier-synchronize";
    private static final String SUPPLIERS_PATH = "/api/suppliers";
    private static final String SYNCHRONIZE_PATH = "/sincronize";

    @Test
    void shouldReturnSuppliersCreatedWhenNeverSynchronized() {
        String id = createSupplier();

        request().when().get(PATH)
                .then().statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id", equalTo(id))
                .body("[0].legalName", equalTo(SupplierFixture.LEGAL_NAME));
    }

    @Test
    void shouldReturnEmptyListWhenNothingChangedSinceLastSynchronize() {
        createSupplier();
        markSynchronizedAt(LocalDateTime.now().plusHours(1));

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(0));
    }

    @Test
    void shouldReturnSupplierDeletedAfterLastSynchronizeAsInactive() {
        markSynchronizedAt(LocalDateTime.now().minusHours(1));
        String id = createSupplier();
        request().when().delete(SUPPLIERS_PATH + "/{id}", id).then().statusCode(204);

        request().when().get(PATH)
                .then().statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id", equalTo(id))
                .body("[0].active", equalTo(false));
    }

    private String createSupplier() {
        return request().body(SupplierFixture.validInput()).when().post(SUPPLIERS_PATH)
                .then().statusCode(201).extract().path("id");
    }

    private void markSynchronizedAt(LocalDateTime lastSynchronize) {
        request()
                .body(Map.of(
                        "nameTable", SynchronizeNameTableEnum.SUPPLIER.name(),
                        "lastSincronize", lastSynchronize.withNano(0).toString()))
        .when()
                .put(SYNCHRONIZE_PATH)
        .then()
                .statusCode(200);
    }
}
