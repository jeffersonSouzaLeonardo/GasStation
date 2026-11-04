package com.br.manager.domain.turno_caixa.controller;

import com.br.manager.domain.turno_caixa.dto.TurnoSincronizacaoDTO;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import com.br.manager.domain.turno_caixa.fixture.TurnoFixture;
import com.br.manager.domain.turno_caixa.repository.TurnoRepository;
import com.br.manager.domain.turno_caixa.support.TurnoATestData;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TurnoATestData.class)
class TurnoControladorSincronizacaoIT extends AbstractIntegrationTest {

    private static final String PATH = "/turno-sincronizacao";

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
    void shouldInsertRecebidoTurno() {
        UUID turnoId = UUID.randomUUID();

        request().body(List.of(TurnoFixture.abertoSincronizacao(turnoId, estacaoId, usuarioId))).when().post(PATH)
                .then().statusCode(200);

        Turno saved = turnoRepository.findById(turnoId).orElseThrow();
        assertThat(saved.getEstacaoId()).isEqualTo(estacaoId);
        assertThat(saved.getCaixeiroUsuarioId()).isEqualTo(usuarioId);
        assertThat(saved.getNome()).isEqualTo(TurnoFixture.NAME);
        assertThat(saved.getStatus()).isEqualTo(TurnoSituacaoEnum.ABERTO);
        assertThat(saved.getFundoAbertura()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldUpdateTurnoWhenItIsResent() {
        UUID turnoId = UUID.randomUUID();
        sincronizacao(TurnoFixture.abertoSincronizacao(turnoId, estacaoId, usuarioId));

        request().body(List.of(TurnoFixture.fechadoSincronizacao(turnoId, estacaoId, usuarioId))).when().post(PATH)
                .then().statusCode(200);

        Turno saved = turnoRepository.findById(turnoId).orElseThrow();
        assertThat(turnoRepository.count()).isEqualTo(1);
        assertThat(saved.getStatus()).isEqualTo(TurnoSituacaoEnum.FECHADO);
        assertThat(saved.getFechadoAt()).isEqualTo(TurnoFixture.FECHADO_AT);
        assertThat(saved.getFechadoBy()).isEqualTo(usuarioId);
    }

    @Test
    void shouldAcceptEmptyList() {
        request().body(List.of()).when().post(PATH).then().statusCode(200);

        assertThat(turnoRepository.count()).isZero();
    }

    @Test
    void shouldReturnBadRequestWhenObrigatorioFieldIsMissing() {
        TurnoSincronizacaoDTO invalido = TurnoFixture.abertoSincronizacao(UUID.randomUUID(), null, usuarioId);

        request().body(List.of(invalido)).when().post(PATH).then().statusCode(400);

        assertThat(turnoRepository.count()).isZero();
    }

    private void sincronizacao(TurnoSincronizacaoDTO dto) {
        request().body(List.of(dto)).when().post(PATH).then().statusCode(200);
    }
}
