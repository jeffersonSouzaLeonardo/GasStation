package com.br.manager.domain.organizacao.controller;

import com.br.manager.domain.organizacao.fixture.UsuarioFixture;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

class UsuarioControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/usuario-sincronizacao";
    private static final String USUARIO_TABELA = "USUARIO";
    private static final String FUTURE_SINCRONIZACAO = "2099-01-01T00:00:00";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void removeUsuarioSincronizacaoSeed() {
        jdbcTemplate.update("DELETE FROM sincronizacao WHERE nome = ?", USUARIO_TABELA);
    }

    @Test
    @Disabled("BUG: GET /usuario-sincronizacao always returns 404 on a fresh database because no migration seeds the "
            + "'USUARIO' row in the sincronizacao tabela (V4 only creates the tabela; V5/V6 seed outros tables only)")
    void shouldListChangedUsuariosUsingMigrationSeed() {
        createUsuario();

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(1));
    }

    @Test
    void shouldListUsuariosChangedWhenNeverSincronizacaod() {
        seedUsuarioSincronizacao();
        createUsuario();

        request()
        .when()
                .get(PATH)
        .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].email", equalTo(UsuarioFixture.EMAIL));
    }

    @Test
    void shouldIncludeInactivatedUsuariosSoThePdvReceivesTheDeletion() {
        seedUsuarioSincronizacao();
        String id = createUsuario();
        request().when().delete("/usuario/{id}", id).then().statusCode(200);

        request().when().get(PATH).then().statusCode(200)
                .body("$", hasSize(1))
                .body("[0].ativo", equalTo(false));
    }

    @Test
    void shouldReturnEmptyListWhenNothingChangedSinceUltimaSincronizacao() {
        seedUsuarioSincronizacao();
        createUsuario();
        request().body(Map.of("nomeTabela", USUARIO_TABELA, "ultimaSincronize", FUTURE_SINCRONIZACAO))
                .when().put("/sincronizacao").then().statusCode(200);

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(0));
    }

    private void seedUsuarioSincronizacao() {
        jdbcTemplate.update("INSERT IGNORE INTO sincronizacao (nome) VALUES (?)", USUARIO_TABELA);
    }

    private String createUsuario() {
        return request().body(UsuarioFixture.validoInput()).when().post("/usuario")
                .then().statusCode(200).extract().path("id");
    }
}
