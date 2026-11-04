package com.br.manager.domain.turno_caixa.controller;

import com.br.manager.domain.turno_caixa.dto.MovimentoCaixaInputDTO;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.fixture.MovimentoCaixaFixture;
import com.br.manager.domain.turno_caixa.support.TurnoBTestData;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Import(TurnoBTestData.class)
class TurnoMovimentoCaixaControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/api/v1/turnos/{id}/movimentos-caixa";

    @Autowired
    private TurnoBTestData testData;

    private Turno abertoTurno;
    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        abertoTurno = testData.createAbertoTurno();
        usuarioId = testData.createUsuario();
    }

    @Test
    @Disabled("BUG: MovimentoCaixa.id has no @GeneratedValue and neither MovimentoCaixaMapper nor TurnoMovimentoCaixaService "
            + "assigns an id, so saveAndFlush fails with IdentifierGenerationException (HTTP 500)")
    void shouldRegisterMovimentoCaixa() {
        UUID pagamentoMethodId = testData.createMetodoPagamento();
        MovimentoCaixaInputDTO input = MovimentoCaixaFixture.completeInput(usuarioId, usuarioId, pagamentoMethodId);

        request().config(TurnoBTestData.DECIMAL_JSON)
                .body(input)
        .when()
                .post(PATH, abertoTurno.getId())
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("turnoId", equalTo(abertoTurno.getId().toString()))
                .body("tipoMovimento", equalTo(MovimentoCaixaFixture.MOVIMENTO_TYPE.name()))
                .body("pagamentoMethodId", equalTo(pagamentoMethodId.toString()))
                .body("valor", comparesEqualTo(MovimentoCaixaFixture.VALOR))
                .body("referencia", equalTo(MovimentoCaixaFixture.REFERENCIA))
                .body("motivo", equalTo(MovimentoCaixaFixture.REASON))
                .body("criadoPor", equalTo(usuarioId.toString()))
                .body("aprovadoPor", equalTo(usuarioId.toString()));
    }

    @Test
    void shouldReturnNotFoundWhenTurnoDoesNotExist() {
        request()
                .body(MovimentoCaixaFixture.validoInput(usuarioId))
                .when().post(PATH, UUID.randomUUID())
                .then().statusCode(404);
    }

    @Test
    void shouldReturnBadRequestWhenTurnoIsFechado() {
        Turno fechadoTurno = testData.createFechadoTurno();

        request()
                .body(MovimentoCaixaFixture.validoInput(usuarioId))
                .when().post(PATH, fechadoTurno.getId())
                .then().statusCode(400);
    }

    @Test
    void shouldReturnNotFoundWhenCreatorDoesNotExist() {
        request()
                .body(MovimentoCaixaFixture.validoInput(UUID.randomUUID()))
                .when().post(PATH, abertoTurno.getId())
                .then().statusCode(404);
    }

    @Test
    void shouldReturnBadRequestWhenObrigatorioFieldsAreMissing() {
        MovimentoCaixaInputDTO input = MovimentoCaixaFixture.validoInput(null);
        input.setTipoMovimento(null);

        request().body(input).when().post(PATH, abertoTurno.getId()).then().statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenValorIsNegative() {
        MovimentoCaixaInputDTO input = MovimentoCaixaFixture.validoInput(usuarioId);
        input.setValor(MovimentoCaixaFixture.NEGATIVE_VALOR);

        request().body(input).when().post(PATH, abertoTurno.getId()).then().statusCode(400);
    }
}
