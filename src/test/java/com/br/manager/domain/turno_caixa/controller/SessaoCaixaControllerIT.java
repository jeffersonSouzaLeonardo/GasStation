package com.br.manager.domain.turno_caixa.controller;

import com.br.manager.domain.turno_caixa.dto.SessaoCaixaFechamentoInputDTO;
import com.br.manager.domain.turno_caixa.dto.SessaoCaixaInputDTO;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.fixture.SessaoCaixaFixture;
import com.br.manager.domain.turno_caixa.support.TurnoBTestData;
import com.br.manager.support.integration.AbstractIntegrationTest;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@Import(TurnoBTestData.class)
class SessaoCaixaControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/api/v1/sessoes-caixa";
    private static final String ABERTO_PATH = PATH + "/abrir";
    private static final String CLOSE_PATH = PATH + "/{id}/fechar";
    private static final String ID_PATH = PATH + "/{id}";

    @Autowired
    private TurnoBTestData testData;

    private Turno abertoTurno;
    private UUID caixeiroUsuarioId;

    @BeforeEach
    void setUp() {
        abertoTurno = testData.createAbertoTurno();
        caixeiroUsuarioId = testData.createUsuario();
    }

    private RequestSpecification decimalRequest() {
        return request().config(TurnoBTestData.DECIMAL_JSON);
    }

    private String abertoSessao(UUID turnoId, UUID usuarioId) {
        return request()
                .body(SessaoCaixaFixture.validoInput(turnoId, usuarioId))
                .when().post(ABERTO_PATH)
                .then().statusCode(201)
                .extract().path("id");
    }

    @Nested
    class Aberto {

        @Test
        void shouldAbertoSessaoCaixa() {
            decimalRequest()
                    .body(SessaoCaixaFixture.validoInput(abertoTurno.getId(), caixeiroUsuarioId))
            .when()
                    .post(ABERTO_PATH)
            .then()
                    .statusCode(201)
                    .body("id", notNullValue())
                    .body("turnoId", equalTo(abertoTurno.getId().toString()))
                    .body("caixeiroUsuarioId", equalTo(caixeiroUsuarioId.toString()))
                    .body("cargo", equalTo(SessaoCaixaFixture.CARGO.name()))
                    .body("status", equalTo("ABERTO"))
                    .body("fundoAbertura", comparesEqualTo(SessaoCaixaFixture.FUNDO_ABERTURA))
                    .body("openedAt", notNullValue())
                    .body("fechadoAt", nullValue())
                    .body("observacoes", equalTo(SessaoCaixaFixture.OBSERVACOES));
        }

        @Test
        void shouldReturnBadRequestWhenObrigatorioFieldsAreMissing() {
            SessaoCaixaInputDTO input = SessaoCaixaFixture.validoInput(null, caixeiroUsuarioId);
            input.setFundoAbertura(null);

            request().body(input).when().post(ABERTO_PATH).then().statusCode(400);
        }

        @Test
        void shouldReturnBadRequestWhenAberturaDinheiroIsNegative() {
            SessaoCaixaInputDTO input = SessaoCaixaFixture.validoInput(abertoTurno.getId(), caixeiroUsuarioId);
            input.setFundoAbertura(new BigDecimal("-1.00"));

            request().body(input).when().post(ABERTO_PATH).then().statusCode(400);
        }

        @Test
        void shouldReturnBadRequestWhenTurnoIsFechado() {
            Turno fechadoTurno = testData.createFechadoTurno();

            request()
                    .body(SessaoCaixaFixture.validoInput(fechadoTurno.getId(), caixeiroUsuarioId))
                    .when().post(ABERTO_PATH)
                    .then().statusCode(400);
        }

        @Test
        void shouldReturnBadRequestWhenTurnoDoesNotExist() {
            request()
                    .body(SessaoCaixaFixture.validoInput(UUID.randomUUID(), caixeiroUsuarioId))
                    .when().post(ABERTO_PATH)
                    .then().statusCode(400);
        }

        @Test
        void shouldReturnNotFoundWhenCaixeiroUsuarioDoesNotExist() {
            request()
                    .body(SessaoCaixaFixture.validoInput(abertoTurno.getId(), UUID.randomUUID()))
                    .when().post(ABERTO_PATH)
                    .then().statusCode(404);
        }

        @Test
        void shouldReturnNotFoundWhenCaixeiroUsuarioIsInativo() {
            request()
                    .body(SessaoCaixaFixture.validoInput(abertoTurno.getId(), testData.createInativoUsuario()))
                    .when().post(ABERTO_PATH)
                    .then().statusCode(404);
        }

        @Test
        void shouldReturnBadRequestWhenCaixeiroAlreadyHasAbertoSessao() {
            abertoSessao(abertoTurno.getId(), caixeiroUsuarioId);

            request()
                    .body(SessaoCaixaFixture.validoInput(abertoTurno.getId(), caixeiroUsuarioId))
                    .when().post(ABERTO_PATH)
                    .then().statusCode(400);
        }
    }

    @Nested
    class Close {

        @Test
        void shouldCloseSessaoWithTotals() {
            String id = abertoSessao(abertoTurno.getId(), caixeiroUsuarioId);

            decimalRequest()
                    .body(SessaoCaixaFixture.closeInput())
            .when()
                    .post(CLOSE_PATH, id)
            .then()
                    .statusCode(200)
                    .body("id", equalTo(id))
                    .body("status", equalTo("FECHADO"))
                    .body("fechadoAt", notNullValue())
                    .body("totalDinheiro", comparesEqualTo(SessaoCaixaFixture.TOTAL_DINHEIRO))
                    .body("totalCartao", comparesEqualTo(SessaoCaixaFixture.TOTAL_CARTAO))
                    .body("totalPix", comparesEqualTo(SessaoCaixaFixture.TOTAL_PIX))
                    .body("totalSangria", comparesEqualTo(SessaoCaixaFixture.TOTAL_SANGRIA))
                    .body("observacoes", equalTo(SessaoCaixaFixture.FECHAMENTO_OBSERVACOES));
        }

        @Test
        void shouldCloseSessaoWithoutBody() {
            String id = abertoSessao(abertoTurno.getId(), caixeiroUsuarioId);

            request()
            .when()
                    .post(CLOSE_PATH, id)
            .then()
                    .statusCode(200)
                    .body("status", equalTo("FECHADO"))
                    .body("totalDinheiro", nullValue())
                    .body("observacoes", equalTo(SessaoCaixaFixture.OBSERVACOES));
        }

        @Test
        void shouldAllowCaixeiroToAbertoNewSessaoAfterFechamento() {
            String id = abertoSessao(abertoTurno.getId(), caixeiroUsuarioId);
            request().when().post(CLOSE_PATH, id).then().statusCode(200);

            request()
                    .body(SessaoCaixaFixture.validoInput(abertoTurno.getId(), caixeiroUsuarioId))
                    .when().post(ABERTO_PATH)
                    .then().statusCode(201);
        }

        @Test
        void shouldReturnNotFoundWhenSessaoIsAlreadyFechado() {
            String id = abertoSessao(abertoTurno.getId(), caixeiroUsuarioId);
            request().when().post(CLOSE_PATH, id).then().statusCode(200);

            request().when().post(CLOSE_PATH, id).then().statusCode(404);
        }

        @Test
        void shouldReturnNotFoundWhenSessaoDoesNotExist() {
            request()
                    .body(SessaoCaixaFixture.closeInput())
                    .when().post(CLOSE_PATH, UUID.randomUUID())
                    .then().statusCode(404);
        }

        @Test
        void shouldReturnBadRequestWhenTotalIsNegative() {
            String id = abertoSessao(abertoTurno.getId(), caixeiroUsuarioId);
            SessaoCaixaFechamentoInputDTO input = SessaoCaixaFixture.closeInput();
            input.setTotalDinheiro(new BigDecimal("-0.01"));

            request().body(input).when().post(CLOSE_PATH, id).then().statusCode(400);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldFindSessaoById() {
            String id = abertoSessao(abertoTurno.getId(), caixeiroUsuarioId);

            request()
            .when()
                    .get(ID_PATH, id)
            .then()
                    .statusCode(200)
                    .body("id", equalTo(id))
                    .body("turnoId", equalTo(abertoTurno.getId().toString()))
                    .body("status", equalTo("ABERTO"))
                    .body("criadoEm", notNullValue());
        }

        @Test
        void shouldReturnNotFoundForUnknownSessao() {
            request().when().get(ID_PATH, UUID.randomUUID()).then().statusCode(404);
        }

        @Test
        void shouldListSessoesOfTurno() {
            String firstId = abertoSessao(abertoTurno.getId(), caixeiroUsuarioId);
            String secondId = abertoSessao(abertoTurno.getId(), testData.createUsuario());
            abertoSessao(testData.createAbertoTurno().getId(), testData.createUsuario());

            request()
                    .queryParam("turnoId", abertoTurno.getId())
            .when()
                    .get(PATH)
            .then()
                    .statusCode(200)
                    .body("$", hasSize(2))
                    .body("id", containsInAnyOrder(firstId, secondId));
        }

        @Test
        void shouldReturnEmptyListForTurnoWithoutSessoes() {
            request()
                    .queryParam("turnoId", abertoTurno.getId())
                    .when().get(PATH)
                    .then().statusCode(200).body("$", hasSize(0));
        }

        @Test
        void shouldReturnBadRequestWhenTurnoIdParameterIsMissing() {
            request().when().get(PATH).then().statusCode(400);
        }
    }
}
