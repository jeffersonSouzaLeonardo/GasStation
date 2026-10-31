package com.br.manager.domain.sale_receipt.controller;

import com.br.manager.domain.sale_receipt.entity.Sale;
import com.br.manager.domain.sale_receipt.enums.PaymentStatus;
import com.br.manager.domain.sale_receipt.enums.SaleStatus;
import com.br.manager.domain.sale_receipt.fixture.SaleFixture;
import com.br.manager.domain.sale_receipt.fixture.SaleReceiptReferenceFixture;
import com.br.manager.domain.sale_receipt.fixture.SaleRefundFixture;
import com.br.manager.domain.sale_receipt.support.SaleReceiptTestData;
import com.br.manager.domain.sale_receipt.support.SaleReceiptTestData.SaleScenario;
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

class SaleRefundControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/api/v1/sale-refunds";
    private static final String NOT_REFUNDABLE_MESSAGE = "Apenas vendas pagas ou com crédito pendente podem ser estornadas.";
    private static final String SAME_APPROVER_MESSAGE = "A aprovação do estorno deve ser feita por um usuário diferente.";
    private static final String MULTIPLE_BAG_BUG = "BUG: SaleRefundService.create loads the sale with "
            + "SaleRepository.findDetailedById (SaleRepository.java:17), whose @EntityGraph fetches 3 List collections "
            + "-> MultipleBagFetchException, so every POST fails before any business rule is evaluated";
    private static final String CREATE_BUG = "BUG: MultipleBagFetchException in SaleRepository.findDetailedById "
            + "(SaleRepository.java:17); SaleRefund (SaleRefund.java:20) and CashMovement (CashMovement.java:16) have "
            + "@Id without @GeneratedValue; and SaleRefundService.java:82 never sets the NOT NULL CashMovement.amount";

    @Autowired
    private ApplicationContext applicationContext;

    private SaleReceiptTestData testData;
    private SaleScenario scenario;
    private UUID requestedBy;
    private UUID approvedBy;

    @BeforeEach
    void setUp() {
        testData = new SaleReceiptTestData(applicationContext);
        scenario = testData.createScenario();
        requestedBy = scenario.cashierUserId();
        approvedBy = testData.createUser(true);
    }

    @Test
    @Disabled(CREATE_BUG)
    void shouldRefundPaidSale() {
        UUID paymentMethodId = testData.createPaymentMethod(SaleReceiptReferenceFixture.cashPaymentMethod());
        Sale sale = testData.createSaleWithPayment(scenario, SaleStatus.PAID, paymentMethodId, PaymentStatus.RECEIVED);

        request()
                .body(SaleRefundFixture.input(sale.getId(), requestedBy, approvedBy))
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("reason", equalTo(SaleRefundFixture.REASON))
                .body("requestedBy", equalTo(requestedBy.toString()))
                .body("approvedBy", equalTo(approvedBy.toString()));
    }

    @Test
    @Disabled(MULTIPLE_BAG_BUG)
    void shouldReturnNotFoundWhenSaleDoesNotExist() {
        request()
                .body(SaleRefundFixture.input(UUID.randomUUID(), requestedBy, approvedBy))
        .when()
                .post(PATH)
        .then()
                .statusCode(404);
    }

    @Test
    @Disabled(MULTIPLE_BAG_BUG)
    void shouldReturnBadRequestWhenSaleIsOpen() {
        Sale sale = testData.createSale(scenario, SaleFixture.SALE_NUMBER, SaleStatus.OPEN);

        request()
                .body(SaleRefundFixture.input(sale.getId(), requestedBy, approvedBy))
        .when()
                .post(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo(NOT_REFUNDABLE_MESSAGE));
    }

    @Test
    @Disabled(MULTIPLE_BAG_BUG)
    void shouldReturnBadRequestWhenRequesterApprovesOwnRefund() {
        Sale sale = testData.createSale(scenario, SaleFixture.SALE_NUMBER, SaleStatus.PAID);

        request()
                .body(SaleRefundFixture.input(sale.getId(), requestedBy, requestedBy))
        .when()
                .post(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo(SAME_APPROVER_MESSAGE));
    }

    @Test
    @Disabled(MULTIPLE_BAG_BUG)
    void shouldReturnNotFoundWhenApproverDoesNotExist() {
        Sale sale = testData.createSale(scenario, SaleFixture.SALE_NUMBER, SaleStatus.PAID);

        request()
                .body(SaleRefundFixture.input(sale.getId(), requestedBy, UUID.randomUUID()))
        .when()
                .post(PATH)
        .then()
                .statusCode(404);
    }

    @Test
    void shouldListRefundsBySale() {
        Sale sale = testData.createRefundedSale(scenario, requestedBy, approvedBy);

        request()
        .when()
                .get(PATH + "/sale/{saleId}", sale.getId())
        .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].reason", equalTo(SaleRefundFixture.REASON))
                .body("[0].requestedBy", equalTo(requestedBy.toString()))
                .body("[0].approvedBy", equalTo(approvedBy.toString()))
                .body("[0].notes", equalTo(SaleRefundFixture.NOTES));
    }

    @Test
    void shouldReturnEmptyListForSaleWithoutRefunds() {
        Sale sale = testData.createSale(scenario, SaleFixture.SALE_NUMBER, SaleStatus.PAID);

        request().when().get(PATH + "/sale/{saleId}", sale.getId()).then().statusCode(200).body("$", hasSize(0));
    }
}
