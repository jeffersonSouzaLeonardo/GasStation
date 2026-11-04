package com.br.manager.domain.comum.controller;

import com.br.manager.domain.comum.entity.NomeTabelaSincronizacaoEnum;
import com.br.manager.domain.comum.repository.RepositorioSincronizacao;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class ControladorSincronizacaoIT extends AbstractIntegrationTest {

    private static final String PATH = "/sincronizacao";
    private static final String TABLE = "PRODUTO";
    private static final String ULTIMA_SINCRONIZE = "2026-07-15T10:30:00";

    @Autowired
    private RepositorioSincronizacao sincronizacaoRepository;

    @Test
    void shouldUpdateUltimaSincronizacaoWithInformedDate() {
        request()
                .body(Map.of("nomeTabela", TABLE, "ultimaSincronize", ULTIMA_SINCRONIZE))
        .when()
                .put(PATH)
        .then()
                .statusCode(200)
                .body("nomeTabela", equalTo(TABLE))
                .body("ultimaSincronizacao", equalTo(ULTIMA_SINCRONIZE));

        assertThat(persistedUltimaSincronizacao(NomeTabelaSincronizacaoEnum.PRODUTO)).isEqualTo(LocalDateTime.parse(ULTIMA_SINCRONIZE));
    }

    @Test
    void shouldAcceptLowerCaseTabelaNameAndUseCurrentDateWhenNotInformed() {
        request()
                .body(Map.of("nomeTabela", "sessao_caixa"))
        .when()
                .put(PATH)
        .then()
                .statusCode(200)
                .body("nomeTabela", equalTo("SESSAO_CAIXA"))
                .body("ultimaSincronizacao", notNullValue());

        assertThat(persistedUltimaSincronizacao(NomeTabelaSincronizacaoEnum.SESSAO_CAIXA)).isNotNull();
    }

    @Test
    void shouldReturnNotFoundWhenTabelaNameIsInvalido() {
        request().body(Map.of("nomeTabela", "INEXISTENTE")).when().put(PATH).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenTabelaNameIsVazio() {
        request().body(Map.of("nomeTabela", " ")).when().put(PATH).then().statusCode(404);
    }

    private LocalDateTime persistedUltimaSincronizacao(NomeTabelaSincronizacaoEnum tabela) {
        return sincronizacaoRepository.findByNomeTabela(tabela).orElseThrow().getUltimaSincronizacao();
    }
}
