package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.comum.entity.NomeTabelaSincronizacaoEnum;
import com.br.manager.domain.operacional.fixture.FornecedorFixture;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

class FornecedorControladorSincronizacaoIT extends AbstractIntegrationTest {

    private static final String PATH = "/fornecedor-sincronizacao";
    private static final String FORNECEDORES_PATH = "/api/fornecedores";
    private static final String SINCRONIZACAO_PATH = "/sincronizacao";

    @Test
    void shouldReturnFornecedorsCriadoWhenNeverSincronizacaod() {
        String id = createFornecedor();

        request().when().get(PATH)
                .then().statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id", equalTo(id))
                .body("[0].legalName", equalTo(FornecedorFixture.LEGAL_NAME));
    }

    @Test
    void shouldReturnEmptyListWhenNothingChangedSinceUltimaSincronizacao() {
        createFornecedor();
        markSincronizacaodAt(LocalDateTime.now().plusHours(1));

        request().when().get(PATH).then().statusCode(200).body("$", hasSize(0));
    }

    @Test
    void shouldReturnFornecedorExcluidoAfterUltimaSincronizacaoAsInativo() {
        markSincronizacaodAt(LocalDateTime.now().minusHours(1));
        String id = createFornecedor();
        request().when().delete(FORNECEDORES_PATH + "/{id}", id).then().statusCode(204);

        request().when().get(PATH)
                .then().statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id", equalTo(id))
                .body("[0].ativo", equalTo(false));
    }

    private String createFornecedor() {
        return request().body(FornecedorFixture.validoInput()).when().post(FORNECEDORES_PATH)
                .then().statusCode(201).extract().path("id");
    }

    private void markSincronizacaodAt(LocalDateTime ultimaSincronizacao) {
        request()
                .body(Map.of(
                        "nomeTabela", NomeTabelaSincronizacaoEnum.FORNECEDOR.name(),
                        "ultimaSincronize", ultimaSincronizacao.withNano(0).toString()))
        .when()
                .put(SINCRONIZACAO_PATH)
        .then()
                .statusCode(200);
    }
}
