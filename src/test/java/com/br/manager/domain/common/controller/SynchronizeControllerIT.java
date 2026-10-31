package com.br.manager.domain.common.controller;

import com.br.manager.domain.common.entity.SynchronizeNameTableEnum;
import com.br.manager.domain.common.repository.SynchronizeRepository;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class SynchronizeControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/sincronize";
    private static final String TABLE = "PRODUCT";
    private static final String LAST_SINCRONIZE = "2026-07-15T10:30:00";

    @Autowired
    private SynchronizeRepository synchronizeRepository;

    @Test
    void shouldUpdateLastSynchronizeWithInformedDate() {
        request()
                .body(Map.of("nameTable", TABLE, "lastSincronize", LAST_SINCRONIZE))
        .when()
                .put(PATH)
        .then()
                .statusCode(200)
                .body("nameTable", equalTo(TABLE))
                .body("lastSynchronize", equalTo(LAST_SINCRONIZE));

        assertThat(persistedLastSynchronize(SynchronizeNameTableEnum.PRODUCT)).isEqualTo(LocalDateTime.parse(LAST_SINCRONIZE));
    }

    @Test
    void shouldAcceptLowerCaseTableNameAndUseCurrentDateWhenNotInformed() {
        request()
                .body(Map.of("nameTable", "cash_session"))
        .when()
                .put(PATH)
        .then()
                .statusCode(200)
                .body("nameTable", equalTo("CASH_SESSION"))
                .body("lastSynchronize", notNullValue());

        assertThat(persistedLastSynchronize(SynchronizeNameTableEnum.CASH_SESSION)).isNotNull();
    }

    @Test
    void shouldReturnNotFoundWhenTableNameIsInvalid() {
        request().body(Map.of("nameTable", "INEXISTENTE")).when().put(PATH).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenTableNameIsBlank() {
        request().body(Map.of("nameTable", " ")).when().put(PATH).then().statusCode(404);
    }

    private LocalDateTime persistedLastSynchronize(SynchronizeNameTableEnum table) {
        return synchronizeRepository.findByNameTable(table).orElseThrow().getLastSynchronize();
    }
}
