package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.operacional.dto.AbastecimentoRequestDTO;
import com.br.manager.domain.operacional.entity.Abastecimento;
import com.br.manager.domain.operacional.fixture.AbastecimentoFixture;
import com.br.manager.domain.operacional.repository.AbastecimentoRepository;
import com.br.manager.domain.operacional.support.OperacionalBDadosTeste;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(OperacionalBDadosTeste.class)
class AbastecimentoControladorSincronizacaoIT extends AbstractIntegrationTest {

    private static final String PATH = "/abastecimento-sincronizacao";

    @Autowired
    private OperacionalBDadosTeste testData;

    @Autowired
    private AbastecimentoRepository abastecimentoRepository;

    private UUID tanqueId;
    private UUID bombaId;

    @BeforeEach
    void createPrerequisites() {
        tanqueId = testData.createTanque();
        bombaId = testData.createBomba();
    }

    @Test
    void shouldPersistRecebidoAbastecimentosKeepingPdvIds() {
        AbastecimentoRequestDTO first = AbastecimentoFixture.validoRequest(tanqueId, bombaId);
        AbastecimentoRequestDTO second = AbastecimentoFixture.validoRequest(tanqueId, bombaId);

        request().body(List.of(asJson(first), asJson(second))).when().post(PATH).then().statusCode(200);

        assertThat(abastecimentoRepository.findAll())
                .extracting(Abastecimento::getId)
                .containsExactlyInAnyOrder(first.getId(), second.getId());
    }

    @Test
    void shouldStoreAbastecimentoValues() {
        AbastecimentoRequestDTO abastecimento = AbastecimentoFixture.validoRequest(tanqueId, bombaId);

        request().body(List.of(asJson(abastecimento))).when().post(PATH).then().statusCode(200);

        Abastecimento stored = abastecimentoRepository.findById(abastecimento.getId()).orElseThrow();
        assertThat(stored.getTanqueId()).isEqualTo(tanqueId);
        assertThat(stored.getBombaId()).isEqualTo(bombaId);
        assertThat(stored.getDataHora()).isEqualTo(AbastecimentoFixture.DATA_TIME);
        assertThat(stored.getVolume()).isEqualByComparingTo(AbastecimentoFixture.VOLUME);
        assertThat(stored.getValorTotal()).isEqualByComparingTo(AbastecimentoFixture.TOTAL_VALOR);
    }

    @Test
    void shouldBeIdempotentWhenBatchIsResent() {
        AbastecimentoRequestDTO abastecimento = AbastecimentoFixture.validoRequest(tanqueId, bombaId);
        request().body(List.of(asJson(abastecimento))).when().post(PATH).then().statusCode(200);
        abastecimento.setValorTotal(AbastecimentoFixture.ATUALIZADO_TOTAL_VALOR);

        request().body(List.of(asJson(abastecimento))).when().post(PATH).then().statusCode(200);

        assertThat(abastecimentoRepository.count()).isEqualTo(1);
        assertThat(abastecimentoRepository.findById(abastecimento.getId()).orElseThrow().getValorTotal())
                .isEqualByComparingTo(AbastecimentoFixture.ATUALIZADO_TOTAL_VALOR);
    }

    @Test
    void shouldAcceptEmptyBatch() {
        request().body(List.of()).when().post(PATH).then().statusCode(200);

        assertThat(abastecimentoRepository.count()).isZero();
    }

    @Test
    void shouldReturnBadRequestWhenMandatoryFieldIsMissing() {
        AbastecimentoRequestDTO abastecimento = AbastecimentoFixture.validoRequest(tanqueId, bombaId);
        abastecimento.setVolume(null);

        request().body(List.of(asJson(abastecimento))).when().post(PATH).then().statusCode(400);

        assertThat(abastecimentoRepository.count()).isZero();
    }

    @Test
    void shouldReturnBadRequestWhenTanqueDoesNotExist() {
        AbastecimentoRequestDTO abastecimento = AbastecimentoFixture.validoRequest(UUID.randomUUID(), bombaId);

        request().body(List.of(asJson(abastecimento))).when().post(PATH).then().statusCode(400);

        assertThat(abastecimentoRepository.count()).isZero();
    }

    private Map<String, Object> asJson(AbastecimentoRequestDTO abastecimento) {
        Map<String, Object> json = new HashMap<>();
        json.put("id", abastecimento.getId());
        json.put("dataHora", abastecimento.getDataHora().toString());
        json.put("tanqueId", abastecimento.getTanqueId());
        json.put("bombaId", abastecimento.getBombaId());
        json.put("volume", abastecimento.getVolume());
        json.put("valorTotal", abastecimento.getValorTotal());
        return json;
    }
}
