package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.comum.entity.NomeTabelaSincronizacaoEnum;
import com.br.manager.domain.operacional.fixture.TanqueFixture;
import com.br.manager.domain.operacional.support.OperacionalAIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

class TanqueControladorSincronizacaoIT extends OperacionalAIntegrationTest {

    private static final String PATH = "/tanque-sincronizacao";
    private static final String TABLE = "tanques";
    private static final NomeTabelaSincronizacaoEnum SINCRONIZACAO_TABELA = NomeTabelaSincronizacaoEnum.TANQUE;

    private UUID combustivelId;

    @BeforeEach
    void createCombustivel() {
        combustivelId = createCombustivelProduto();
    }

    @Test
    void shouldReturnAllRecordsWhenTabelaWasNeverSincronizacaod() {
        String id = createTanque(TanqueFixture.validoInput(combustivelId));

        request().when().get(PATH)
                .then().statusCode(200).body("$", hasSize(1)).body("[0].id", equalTo(id));
    }

    @Test
    void shouldReturnOnlyRecordsChangedSinceUltimaSincronizacao() {
        LocalDateTime now = LocalDateTime.now();
        String changed = createTanque(TanqueFixture.validoInput(combustivelId));
        String unchanged = createTanque(TanqueFixture.validoInput(combustivelId, TanqueFixture.OUTROS_CODE));
        testData.backdate(TABLE, unchanged, now.minusDays(2));
        testData.setUltimaSincronizacao(SINCRONIZACAO_TABELA, now.minusDays(1));

        request().when().get(PATH)
                .then().statusCode(200).body("id", contains(changed));
    }

    @Test
    void shouldReturnRecordsExcluidoSinceUltimaSincronizacao() {
        LocalDateTime now = LocalDateTime.now();
        String excluido = createTanque(TanqueFixture.validoInput(combustivelId));
        testData.backdate(TABLE, excluido, now.minusDays(2));
        testData.setUltimaSincronizacao(SINCRONIZACAO_TABELA, now.minusDays(1));
        delete(TANQUES, excluido);

        request().when().get(PATH)
                .then().statusCode(200).body("id", contains(excluido)).body("[0].ativo", equalTo(false));
    }

    @Test
    void shouldReturnEmptyWhenNothingChangedAfterUltimaSincronizacao() {
        createTanque(TanqueFixture.validoInput(combustivelId));
        testData.setUltimaSincronizacao(SINCRONIZACAO_TABELA, LocalDateTime.now().plusDays(1));

        request().when().get(PATH)
                .then().statusCode(200).body("$", hasSize(0));
    }
}
