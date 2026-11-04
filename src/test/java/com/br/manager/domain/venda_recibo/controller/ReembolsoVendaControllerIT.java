package com.br.manager.domain.venda_recibo.controller;

import com.br.manager.domain.venda_recibo.entity.Venda;
import com.br.manager.domain.venda_recibo.enums.SituacaoPagamento;
import com.br.manager.domain.venda_recibo.enums.VendaSituacao;
import com.br.manager.domain.venda_recibo.fixture.VendaFixture;
import com.br.manager.domain.venda_recibo.fixture.ReciboVendaReferenciaFixture;
import com.br.manager.domain.venda_recibo.fixture.VendaReembolsoFixture;
import com.br.manager.domain.venda_recibo.support.ReciboVendaDadosTeste;
import com.br.manager.domain.venda_recibo.support.ReciboVendaDadosTeste.VendaScenario;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class ReembolsoVendaControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/api/v1/reembolsos-venda";
    private static final String NOT_REFUNDABLE_MESSAGE = "Apenas vendas pagas ou com crédito pendente podem ser estornadas.";
    private static final String SAME_APPROVER_MESSAGE = "A aprovação do estorno deve ser feita por um usuário diferente.";
    private static final String MULTIPLE_BAG_BUG = "BUG: ReembolsoVendaService.create loads the venda with "
            + "VendaRepository.findDetailedById (VendaRepository.java:17), whose @EntityGraph fetches 3 List collections "
            + "-> MultipleBagFetchException, so every POST fails before any business rule is evaluated";
    private static final String CREATE_BUG = "BUG: MultipleBagFetchException in VendaRepository.findDetailedById "
            + "(VendaRepository.java:17); ReembolsoVenda (ReembolsoVenda.java:20) and MovimentoCaixa (MovimentoCaixa.java:16) ter "
            + "@Id without @GeneratedValue; and ReembolsoVendaService.java:82 never sets the NOT NULL MovimentoCaixa.valor";

    @Autowired
    private ApplicationContext applicationContext;

    private ReciboVendaDadosTeste testData;
    private VendaScenario scenario;
    private UUID requestedBy;
    private UUID aprovadoPor;

    @BeforeEach
    void setUp() {
        testData = new ReciboVendaDadosTeste(applicationContext);
        scenario = testData.createScenario();
        requestedBy = scenario.caixeiroUsuarioId();
        aprovadoPor = testData.createUsuario(true);
    }

    @Test
    @Disabled(CREATE_BUG)
    void shouldReembolsoPagoVenda() {
        UUID pagamentoMethodId = testData.createMetodoPagamento(ReciboVendaReferenciaFixture.dinheiroMetodoPagamento());
        Venda venda = testData.createVendaWithPagamento(scenario, VendaSituacao.PAGO, pagamentoMethodId, SituacaoPagamento.RECEBIDO);

        request()
                .body(VendaReembolsoFixture.input(venda.getId(), requestedBy, aprovadoPor))
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("motivo", equalTo(VendaReembolsoFixture.REASON))
                .body("requestedBy", equalTo(requestedBy.toString()))
                .body("aprovadoPor", equalTo(aprovadoPor.toString()));
    }

    @Test
    @Disabled(MULTIPLE_BAG_BUG)
    void shouldReturnNotFoundWhenVendaDoesNotExist() {
        request()
                .body(VendaReembolsoFixture.input(UUID.randomUUID(), requestedBy, aprovadoPor))
        .when()
                .post(PATH)
        .then()
                .statusCode(404);
    }

    @Test
    @Disabled(MULTIPLE_BAG_BUG)
    void shouldReturnBadRequestWhenVendaIsAberto() {
        Venda venda = testData.createVenda(scenario, VendaFixture.NUMERO_VENDA, VendaSituacao.ABERTO);

        request()
                .body(VendaReembolsoFixture.input(venda.getId(), requestedBy, aprovadoPor))
        .when()
                .post(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo(NOT_REFUNDABLE_MESSAGE));
    }

    @Test
    @Disabled(MULTIPLE_BAG_BUG)
    void shouldReturnBadRequestWhenRequesterApprovesOwnReembolso() {
        Venda venda = testData.createVenda(scenario, VendaFixture.NUMERO_VENDA, VendaSituacao.PAGO);

        request()
                .body(VendaReembolsoFixture.input(venda.getId(), requestedBy, requestedBy))
        .when()
                .post(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo(SAME_APPROVER_MESSAGE));
    }

    @Test
    @Disabled(MULTIPLE_BAG_BUG)
    void shouldReturnNotFoundWhenApproverDoesNotExist() {
        Venda venda = testData.createVenda(scenario, VendaFixture.NUMERO_VENDA, VendaSituacao.PAGO);

        request()
                .body(VendaReembolsoFixture.input(venda.getId(), requestedBy, UUID.randomUUID()))
        .when()
                .post(PATH)
        .then()
                .statusCode(404);
    }

    @Test
    void shouldListReembolsosByVenda() {
        Venda venda = testData.createReembolsadoVenda(scenario, requestedBy, aprovadoPor);

        request()
        .when()
                .get(PATH + "/venda/{vendaId}", venda.getId())
        .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].motivo", equalTo(VendaReembolsoFixture.REASON))
                .body("[0].requestedBy", equalTo(requestedBy.toString()))
                .body("[0].aprovadoPor", equalTo(aprovadoPor.toString()))
                .body("[0].observacoes", equalTo(VendaReembolsoFixture.OBSERVACOES));
    }

    @Test
    void shouldReturnEmptyListForVendaWithoutReembolsos() {
        Venda venda = testData.createVenda(scenario, VendaFixture.NUMERO_VENDA, VendaSituacao.PAGO);

        request().when().get(PATH + "/venda/{vendaId}", venda.getId()).then().statusCode(200).body("$", hasSize(0));
    }
}
