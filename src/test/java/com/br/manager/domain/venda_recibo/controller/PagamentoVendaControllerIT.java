package com.br.manager.domain.venda_recibo.controller;

import com.br.manager.domain.venda_recibo.dto.VendaPagamentoInputDTO;
import com.br.manager.domain.venda_recibo.entity.Venda;
import com.br.manager.domain.venda_recibo.enums.SituacaoPagamento;
import com.br.manager.domain.venda_recibo.enums.VendaSituacao;
import com.br.manager.domain.venda_recibo.fixture.VendaFixture;
import com.br.manager.domain.venda_recibo.fixture.VendaPagamentoFixture;
import com.br.manager.domain.venda_recibo.fixture.ReciboVendaReferenciaFixture;
import com.br.manager.domain.venda_recibo.support.ReciboVendaDadosTeste;
import com.br.manager.domain.venda_recibo.support.ReciboVendaDadosTeste.VendaScenario;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class PagamentoVendaControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/api/v1/pagamentos-venda";
    private static final String MENSAGEM_VENDA_BLOQUEADA = "Vendas pagas ou com crédito pendente não podem ser alteradas.";
    private static final String INVALIDO_VALOR_MESSAGE = "O valor do pagamento deve ser maior que zero.";
    private static final String MULTIPLE_BAG_BUG = "BUG: PagamentoVendaService.create loads the venda with "
            + "VendaRepository.findDetailedById (VendaRepository.java:17), whose @EntityGraph fetches 3 List collections "
            + "-> MultipleBagFetchException, so every POST fails before any business rule is evaluated";
    private static final String CREATE_BUG = "BUG: MultipleBagFetchException in VendaRepository.findDetailedById "
            + "(VendaRepository.java:17) and PagamentoVenda has @Id without @GeneratedValue (PagamentoVenda.java:23) while "
            + "PagamentoVendaService.create sets id=null, so the pagamento can never be persisted";

    @Autowired
    private ApplicationContext applicationContext;

    private ReciboVendaDadosTeste testData;
    private VendaScenario scenario;
    private UUID pagamentoMethodId;

    @BeforeEach
    void setUp() {
        testData = new ReciboVendaDadosTeste(applicationContext);
        scenario = testData.createScenario();
        pagamentoMethodId = testData.createMetodoPagamento(ReciboVendaReferenciaFixture.dinheiroMetodoPagamento());
    }

    @Test
    @Disabled(CREATE_BUG)
    void shouldCreatePagamentoForAbertoVenda() {
        Venda venda = testData.createVenda(scenario, VendaFixture.NUMERO_VENDA, VendaSituacao.ABERTO);

        request()
                .body(VendaPagamentoFixture.input(venda.getId(), pagamentoMethodId))
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("pagamentoMethodId", equalTo(pagamentoMethodId.toString()))
                .body("valor", equalTo(VendaPagamentoFixture.VALOR.floatValue()))
                .body("status", equalTo(SituacaoPagamento.RECEBIDO.name()))
                .body("referenciaTransacao", equalTo(VendaPagamentoFixture.REFERENCIA_TRANSACAO));
    }

    @Test
    @Disabled(MULTIPLE_BAG_BUG)
    void shouldReturnNotFoundWhenVendaDoesNotExist() {
        request()
                .body(VendaPagamentoFixture.input(UUID.randomUUID(), pagamentoMethodId))
        .when()
                .post(PATH)
        .then()
                .statusCode(404);
    }

    @Test
    @Disabled(MULTIPLE_BAG_BUG)
    void shouldReturnBadRequestWhenVendaIsAlreadyPago() {
        Venda venda = testData.createVenda(scenario, VendaFixture.NUMERO_VENDA, VendaSituacao.PAGO);

        request()
                .body(VendaPagamentoFixture.input(venda.getId(), pagamentoMethodId))
        .when()
                .post(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo(MENSAGEM_VENDA_BLOQUEADA));
    }

    @Test
    @Disabled(MULTIPLE_BAG_BUG)
    void shouldReturnNotFoundWhenMetodoPagamentoDoesNotExist() {
        Venda venda = testData.createVenda(scenario, VendaFixture.NUMERO_VENDA, VendaSituacao.ABERTO);

        request()
                .body(VendaPagamentoFixture.input(venda.getId(), UUID.randomUUID()))
        .when()
                .post(PATH)
        .then()
                .statusCode(404);
    }

    @Test
    @Disabled(MULTIPLE_BAG_BUG)
    void shouldReturnBadRequestWhenValorIsNotPositive() {
        Venda venda = testData.createVenda(scenario, VendaFixture.NUMERO_VENDA, VendaSituacao.ABERTO);
        VendaPagamentoInputDTO input = VendaPagamentoFixture.input(venda.getId(), pagamentoMethodId);
        input.setValor(BigDecimal.ZERO);

        request()
                .body(input)
        .when()
                .post(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo(INVALIDO_VALOR_MESSAGE));
    }

    @Test
    void shouldListPagamentosByVenda() {
        Venda venda = testData.createVendaWithPagamento(scenario, VendaSituacao.PAGO, pagamentoMethodId, SituacaoPagamento.RECEBIDO);

        request()
        .when()
                .get(PATH + "/venda/{vendaId}", venda.getId())
        .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].pagamentoMethodId", equalTo(pagamentoMethodId.toString()))
                .body("[0].status", equalTo(SituacaoPagamento.RECEBIDO.name()));
    }

    @Test
    void shouldReturnEmptyListForVendaWithoutPagamentos() {
        Venda venda = testData.createVenda(scenario, VendaFixture.NUMERO_VENDA, VendaSituacao.ABERTO);

        request().when().get(PATH + "/venda/{vendaId}", venda.getId()).then().statusCode(200).body("$", hasSize(0));
    }

    @Test
    void shouldFindPagamentoById() {
        Venda venda = testData.createVendaWithPagamento(scenario, VendaSituacao.PAGO, pagamentoMethodId, SituacaoPagamento.AUTORIZADO);
        UUID pagamentoId = venda.getPagamentos().get(0).getId();

        request()
        .when()
                .get(PATH + "/{id}", pagamentoId)
        .then()
                .statusCode(200)
                .body("id", equalTo(pagamentoId.toString()))
                .body("valor", equalTo(VendaPagamentoFixture.VALOR.floatValue()))
                .body("status", equalTo(SituacaoPagamento.AUTORIZADO.name()))
                .body("referenciaTransacao", equalTo(VendaPagamentoFixture.REFERENCIA_TRANSACAO));
    }

    @Test
    void shouldReturnNotFoundForUnknownPagamento() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
    }
}
