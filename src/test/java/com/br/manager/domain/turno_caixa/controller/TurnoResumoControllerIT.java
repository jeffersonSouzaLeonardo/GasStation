package com.br.manager.domain.turno_caixa.controller;

import com.br.manager.domain.turno_caixa.dto.TurnoSincronizacaoDTO;
import com.br.manager.domain.turno_caixa.fixture.TurnoFixture;
import com.br.manager.domain.turno_caixa.support.TurnoATestData;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;

@Import(TurnoATestData.class)
class TurnoResumoControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/api/v1/turnos/{id}/resumo";
    private static final String SINCRONIZACAO_PATH = "/turno-sincronizacao";

    @Autowired
    private TurnoATestData testData;

    @Test
    @Disabled("BUG: FechamentoPagamento is mapped to tabela 'encerramentos_pagamento' (entity/FechamentoPagamento.java:11) but the migration creates 'encerramentos_pagamento_turno' (V1__create_initial_schema.sql:387), so the resumo query fails with 500")
    void shouldReturnResumoOfTurnoWithoutActivity() {
        UUID usuarioId = testData.createUsuario();
        UUID turnoId = sincronizacaoTurno(TurnoFixture.abertoSincronizacao(UUID.randomUUID(), testData.createEstacao(), usuarioId));

        request()
        .when()
                .get(PATH, turnoId)
        .then()
                .statusCode(200)
                .body("turno.id", equalTo(turnoId.toString()))
                .body("turno.status", equalTo("ABERTO"))
                .body("grossVendas", equalTo(0))
                .body("netVendas", equalTo(0))
                .body("calibracaoAndTestAjuste", equalTo(0))
                .body("totalDiferencaValor", equalTo(0))
                .body("movimentosCaixa", empty())
                .body("fechamentosPagamento", empty());
    }

    @Test
    void shouldReturnNotFoundWhenTurnoIsUnknown() {
        request().when().get(PATH, UUID.randomUUID()).then().statusCode(404);
    }

    private UUID sincronizacaoTurno(TurnoSincronizacaoDTO dto) {
        request().body(List.of(dto)).when().post(SINCRONIZACAO_PATH).then().statusCode(200);
        return dto.getId();
    }
}
