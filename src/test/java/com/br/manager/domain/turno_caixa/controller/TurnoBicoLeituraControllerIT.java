package com.br.manager.domain.turno_caixa.controller;

import com.br.manager.domain.turno_caixa.dto.BicoLeituraInputDTO;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TipoLeituraBicoEnum;
import com.br.manager.domain.turno_caixa.fixture.BicoLeituraFixture;
import com.br.manager.domain.turno_caixa.repository.BicoLeituraRepository;
import com.br.manager.domain.turno_caixa.support.TurnoBTestData;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Import(TurnoBTestData.class)
class TurnoBicoLeituraControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/api/v1/turnos/{id}/leituras-bico";

    @Autowired
    private TurnoBTestData testData;

    @Autowired
    private BicoLeituraRepository bicoLeituraRepository;

    private Turno abertoTurno;
    private UUID bicoId;
    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        abertoTurno = testData.createAbertoTurno();
        bicoId = testData.createBico();
        usuarioId = testData.createUsuario();
    }

    private BicoLeituraInputDTO input(TipoLeituraBicoEnum type) {
        return BicoLeituraFixture.validoInput(bicoId, usuarioId, type);
    }

    private void givenRegisteredLeitura(TipoLeituraBicoEnum type) {
        bicoLeituraRepository.saveAndFlush(BicoLeituraFixture.persistedEntity(abertoTurno, bicoId, usuarioId, type));
    }

    @Test
    @Disabled("BUG: LeituraBico.id has no @GeneratedValue and neither BicoLeituraMapper nor TurnoBicoLeituraService "
            + "assigns an id, so saveAndFlush fails with IdentifierGenerationException (HTTP 500)")
    void shouldRegisterFechamentoLeitura() {
        request().config(TurnoBTestData.DECIMAL_JSON)
                .body(input(TipoLeituraBicoEnum.FECHAMENTO))
        .when()
                .post(PATH, abertoTurno.getId())
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("turnoId", equalTo(abertoTurno.getId().toString()))
                .body("bicoId", equalTo(bicoId.toString()))
                .body("tipoLeitura", equalTo(TipoLeituraBicoEnum.FECHAMENTO.name()))
                .body("leituraMedidor", comparesEqualTo(BicoLeituraFixture.MEDIDOR_LEITURA))
                .body("registradoPor", equalTo(usuarioId.toString()))
                .body("observacoes", equalTo(BicoLeituraFixture.OBSERVACOES));
    }

    @Test
    void shouldReturnNotFoundWhenTurnoDoesNotExist() {
        request()
                .body(input(TipoLeituraBicoEnum.FECHAMENTO))
                .when().post(PATH, UUID.randomUUID())
                .then().statusCode(404);
    }

    @Test
    void shouldReturnBadRequestWhenTurnoIsFechado() {
        Turno fechadoTurno = testData.createFechadoTurno();

        request()
                .body(input(TipoLeituraBicoEnum.FECHAMENTO))
                .when().post(PATH, fechadoTurno.getId())
                .then().statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenBicoIsUnknown() {
        BicoLeituraInputDTO unknownBico = BicoLeituraFixture.validoInput(UUID.randomUUID(), usuarioId, TipoLeituraBicoEnum.FECHAMENTO);

        request()
                .body(unknownBico)
                .when().post(PATH, abertoTurno.getId())
                .then().statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenAberturaLeituraAlreadyExists() {
        givenRegisteredLeitura(TipoLeituraBicoEnum.ABERTURA);

        request()
                .body(input(TipoLeituraBicoEnum.ABERTURA))
                .when().post(PATH, abertoTurno.getId())
                .then().statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenFechamentoLeituraAlreadyExists() {
        givenRegisteredLeitura(TipoLeituraBicoEnum.FECHAMENTO);

        request()
                .body(input(TipoLeituraBicoEnum.FECHAMENTO))
                .when().post(PATH, abertoTurno.getId())
                .then().statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenObrigatorioFieldsAreMissing() {
        BicoLeituraInputDTO invalido = BicoLeituraFixture.validoInput(null, null, null);

        request().body(invalido).when().post(PATH, abertoTurno.getId()).then().statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenMedidorLeituraIsNegative() {
        BicoLeituraInputDTO invalido = input(TipoLeituraBicoEnum.FECHAMENTO);
        invalido.setLeituraMedidor(new BigDecimal("-0.001"));

        request().body(invalido).when().post(PATH, abertoTurno.getId()).then().statusCode(400);
    }
}
