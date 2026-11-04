package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.comum.entity.NomeTabelaSincronizacaoEnum;
import com.br.manager.domain.operacional.dto.BicoInputDTO;
import com.br.manager.domain.operacional.fixture.BicoFixture;
import com.br.manager.domain.operacional.support.OperacionalAIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

class BicoControladorSincronizacaoIT extends OperacionalAIntegrationTest {

    private static final String PATH = "/bico-sincronizacao";
    private static final String TABLE = "bicos";
    private static final NomeTabelaSincronizacaoEnum SINCRONIZACAO_TABELA = NomeTabelaSincronizacaoEnum.BICO;

    private UUID combustivelId;
    private UUID tanqueId;
    private UUID bombaId;

    @BeforeEach
    void createPrerequisites() {
        combustivelId = createCombustivelProduto();
        tanqueId = createTanque(combustivelId);
        bombaId = createBomba();
    }

    @Test
    void shouldReturnAllRecordsWhenTabelaWasNeverSincronizacaod() {
        String id = create(BICOS, BicoFixture.validoInput(bombaId, tanqueId, combustivelId));

        request().when().get(PATH)
                .then().statusCode(200).body("$", hasSize(1)).body("[0].id", equalTo(id));
    }

    @Test
    void shouldReturnOnlyRecordsChangedSinceUltimaSincronizacao() {
        LocalDateTime now = LocalDateTime.now();
        String changed = create(BICOS, BicoFixture.validoInput(bombaId, tanqueId, combustivelId));
        String unchanged = create(BICOS, outrosBico());
        testData.backdate(TABLE, unchanged, now.minusDays(2));
        testData.setUltimaSincronizacao(SINCRONIZACAO_TABELA, now.minusDays(1));

        request().when().get(PATH)
                .then().statusCode(200).body("id", contains(changed));
    }

    @Test
    void shouldReturnRecordsExcluidoSinceUltimaSincronizacao() {
        LocalDateTime now = LocalDateTime.now();
        String excluido = create(BICOS, BicoFixture.validoInput(bombaId, tanqueId, combustivelId));
        testData.backdate(TABLE, excluido, now.minusDays(2));
        testData.setUltimaSincronizacao(SINCRONIZACAO_TABELA, now.minusDays(1));
        delete(BICOS, excluido);

        request().when().get(PATH)
                .then().statusCode(200).body("id", contains(excluido)).body("[0].ativo", equalTo(false));
    }

    @Test
    void shouldReturnEmptyWhenNothingChangedAfterUltimaSincronizacao() {
        create(BICOS, BicoFixture.validoInput(bombaId, tanqueId, combustivelId));
        testData.setUltimaSincronizacao(SINCRONIZACAO_TABELA, LocalDateTime.now().plusDays(1));

        request().when().get(PATH)
                .then().statusCode(200).body("$", hasSize(0));
    }

    private BicoInputDTO outrosBico() {
        BicoInputDTO input = BicoFixture.validoInput(bombaId, tanqueId, combustivelId);
        input.setCodigo(BicoFixture.OUTROS_CODE);
        return input;
    }
}
