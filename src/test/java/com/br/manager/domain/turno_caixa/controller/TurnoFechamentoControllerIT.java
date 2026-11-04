package com.br.manager.domain.turno_caixa.controller;

import com.br.manager.domain.turno_caixa.dto.TurnoFechamentoRequestDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoSincronizacaoDTO;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import com.br.manager.domain.turno_caixa.fixture.TurnoFixture;
import com.br.manager.domain.turno_caixa.repository.TurnoRepository;
import com.br.manager.domain.turno_caixa.support.TurnoATestData;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Import(TurnoATestData.class)
class TurnoFechamentoControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/api/v1/turnos/{id}/fechar";
    private static final String SINCRONIZACAO_PATH = "/turno-sincronizacao";

    @Autowired
    private TurnoATestData testData;

    @Autowired
    private TurnoRepository turnoRepository;

    private UUID estacaoId;
    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        estacaoId = testData.createEstacao();
        usuarioId = testData.createUsuario();
    }

    @Test
    void shouldCloseAbertoTurno() {
        UUID turnoId = sincronizacaoTurno(TurnoFixture.abertoSincronizacao(UUID.randomUUID(), estacaoId, usuarioId));

        request()
                .body(TurnoFixture.closeRequest(usuarioId))
        .when()
                .post(PATH, turnoId)
        .then()
                .statusCode(200)
                .body("id", equalTo(turnoId.toString()))
                .body("status", equalTo("FECHADO"))
                .body("fechadoBy", equalTo(usuarioId.toString()))
                .body("fechadoAt", notNullValue());

        assertThat(turnoRepository.findById(turnoId).orElseThrow().getStatus()).isEqualTo(TurnoSituacaoEnum.FECHADO);
    }

    @Test
    void shouldReturnBadRequestWhenTurnoIsAlreadyFechado() {
        UUID turnoId = sincronizacaoTurno(TurnoFixture.fechadoSincronizacao(UUID.randomUUID(), estacaoId, usuarioId));

        request().body(TurnoFixture.closeRequest(usuarioId)).when().post(PATH, turnoId).then().statusCode(400);
    }

    @Test
    void shouldReturnBadRequestWhenFechadoByIsMissing() {
        UUID turnoId = sincronizacaoTurno(TurnoFixture.abertoSincronizacao(UUID.randomUUID(), estacaoId, usuarioId));

        request().body(new TurnoFechamentoRequestDTO()).when().post(PATH, turnoId).then().statusCode(400);
    }

    @Test
    void shouldReturnNotFoundWhenTurnoIsUnknown() {
        request().body(TurnoFixture.closeRequest(usuarioId)).when().post(PATH, UUID.randomUUID()).then().statusCode(404);
    }

    @Test
    void shouldReturnNotFoundWhenFechamentoUsuarioIsUnknown() {
        UUID turnoId = sincronizacaoTurno(TurnoFixture.abertoSincronizacao(UUID.randomUUID(), estacaoId, usuarioId));

        request().body(TurnoFixture.closeRequest(UUID.randomUUID())).when().post(PATH, turnoId).then().statusCode(404);

        assertThat(turnoRepository.findById(turnoId).orElseThrow().getStatus()).isEqualTo(TurnoSituacaoEnum.ABERTO);
    }

    private UUID sincronizacaoTurno(TurnoSincronizacaoDTO dto) {
        request().body(List.of(dto)).when().post(SINCRONIZACAO_PATH).then().statusCode(200);
        return dto.getId();
    }
}
