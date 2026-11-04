package com.br.manager.domain.turno_caixa.controller;

import com.br.manager.domain.turno_caixa.dto.SessaoCaixaSincronizacaoDTO;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.SessaoCaixaSituacaoEnum;
import com.br.manager.domain.turno_caixa.enums.CargoCaixaEnum;
import com.br.manager.domain.turno_caixa.fixture.SessaoCaixaFixture;
import com.br.manager.domain.turno_caixa.support.TurnoBTestData;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;

@Import(TurnoBTestData.class)
class SessaoCaixaControladorSincronizacaoIT extends AbstractIntegrationTest {

    private static final String PATH = "/sessao-caixa-sincronizacao";
    private static final String SESSAO_PATH = "/api/v1/sessoes-caixa/{id}";
    private static final String SESSOES_BY_TURNO_PATH = "/api/v1/sessoes-caixa";
    private static final String OBSERVACOES_ATUALIZADAS = "Sessao reenviada pelo PDV";

    @Autowired
    private TurnoBTestData testData;

    private Turno turno;
    private SessaoCaixaSincronizacaoDTO sincronizacao;

    @BeforeEach
    void setUp() {
        turno = testData.createAbertoTurno();
        sincronizacao = SessaoCaixaFixture.fechadoSincronizacaoInput(UUID.randomUUID(), turno.getId(), testData.createUsuario());
    }

    private void sincronizacao(List<SessaoCaixaSincronizacaoDTO> sessoes) {
        request().body(sessoes).when().post(PATH).then().statusCode(200);
    }

    @Test
    void shouldPersistSessoesRecebidoFromPdv() {
        sincronizacao(List.of(sincronizacao));

        request().config(TurnoBTestData.DECIMAL_JSON)
        .when()
                .get(SESSAO_PATH, sincronizacao.getId())
        .then()
                .statusCode(200)
                .body("id", equalTo(sincronizacao.getId().toString()))
                .body("turnoId", equalTo(turno.getId().toString()))
                .body("caixeiroUsuarioId", equalTo(sincronizacao.getCaixeiroUsuarioId().toString()))
                .body("cargo", equalTo(CargoCaixaEnum.FRENTISTA.name()))
                .body("status", equalTo(SessaoCaixaSituacaoEnum.FECHADO.name()))
                .body("openedAt", equalTo(SessaoCaixaFixture.OPENED_AT.toString() + ":00"))
                .body("fechadoAt", equalTo(SessaoCaixaFixture.FECHADO_AT.toString() + ":00"))
                .body("fundoAbertura", comparesEqualTo(SessaoCaixaFixture.FUNDO_ABERTURA))
                .body("totalDinheiro", comparesEqualTo(SessaoCaixaFixture.TOTAL_DINHEIRO))
                .body("totalCartao", comparesEqualTo(SessaoCaixaFixture.TOTAL_CARTAO))
                .body("totalPix", comparesEqualTo(SessaoCaixaFixture.TOTAL_PIX))
                .body("totalSangria", comparesEqualTo(SessaoCaixaFixture.TOTAL_SANGRIA))
                .body("observacoes", equalTo(SessaoCaixaFixture.SINCRONIZACAO_OBSERVACOES));
    }

    @Test
    void shouldUpdateExistingSessaoWhenBatchIsResent() {
        sincronizacao(List.of(sincronizacao));
        sincronizacao.setObservacoes(OBSERVACOES_ATUALIZADAS);
        sincronizacao.setTotalPix(null);

        sincronizacao(List.of(sincronizacao));

        request().queryParam("turnoId", turno.getId())
        .when()
                .get(SESSOES_BY_TURNO_PATH)
        .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].observacoes", equalTo(OBSERVACOES_ATUALIZADAS))
                .body("[0].totalPix", nullValue());
    }

    @Test
    void shouldAcceptEmptyBatch() {
        sincronizacao(List.of());
    }

    @Test
    void shouldReturnBadRequestWhenTurnoOfSessaoDoesNotExist() {
        sincronizacao.setTurnoId(UUID.randomUUID());

        request().body(List.of(sincronizacao)).when().post(PATH).then().statusCode(400);

        request().when().get(SESSAO_PATH, sincronizacao.getId()).then().statusCode(404);
    }

    @Test
    void shouldRollbackWholeBatchWhenOneSessaoIsInvalido() {
        SessaoCaixaSincronizacaoDTO orphan = SessaoCaixaFixture.fechadoSincronizacaoInput(UUID.randomUUID(), UUID.randomUUID(), testData.createUsuario());

        request().body(List.of(sincronizacao, orphan)).when().post(PATH).then().statusCode(400);

        request().when().get(SESSAO_PATH, sincronizacao.getId()).then().statusCode(404);
    }

    @Test
    void shouldReturnBadRequestWhenObrigatorioFieldIsMissing() {
        sincronizacao.setId(null);

        request().body(List.of(sincronizacao)).when().post(PATH).then().statusCode(400);
    }
}
