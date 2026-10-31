package com.br.manager.domain.organization.controller;

import com.br.manager.domain.organization.fixture.UserFixture;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

class UserControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/user-synchronize";
    private static final String USER_TABLE = "USER";
    private static final String FUTURE_SYNCHRONIZE = "2099-01-01T00:00:00";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void removeUserSynchronizeSeed() {
        jdbcTemplate.update("DELETE FROM synchronize WHERE name = ?", USER_TABLE);
    }

    @Test
    @Disabled("BUG: GET /user-synchronize always returns 404 on a fresh database because no migration seeds the "
            + "'USER' row in the synchronize table (V4 only creates the table; V5/V6 seed other tables only)")
    void shouldListChangedUsersUsingMigrationSeed() {
        createUser();

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(1));
    }

    @Test
    void shouldListUsersChangedWhenNeverSynchronized() {
        seedUserSynchronize();
        createUser();

        request()
        .when()
                .get(PATH)
        .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].email", equalTo(UserFixture.EMAIL));
    }

    @Test
    void shouldIncludeInactivatedUsersSoThePdvReceivesTheDeletion() {
        seedUserSynchronize();
        String id = createUser();
        request().when().delete("/user/{id}", id).then().statusCode(200);

        request().when().get(PATH).then().statusCode(200)
                .body("$", hasSize(1))
                .body("[0].active", equalTo(false));
    }

    @Test
    void shouldReturnEmptyListWhenNothingChangedSinceLastSynchronize() {
        seedUserSynchronize();
        createUser();
        request().body(Map.of("nameTable", USER_TABLE, "lastSincronize", FUTURE_SYNCHRONIZE))
                .when().put("/sincronize").then().statusCode(200);

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(0));
    }

    private void seedUserSynchronize() {
        jdbcTemplate.update("INSERT IGNORE INTO synchronize (name) VALUES (?)", USER_TABLE);
    }

    private String createUser() {
        return request().body(UserFixture.validInput()).when().post("/user")
                .then().statusCode(200).extract().path("id");
    }
}
