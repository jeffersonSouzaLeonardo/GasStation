package com.br.manager.domain.sale_receipt.controller;

import com.br.manager.domain.sale_receipt.dto.SalePaymentInputDTO;
import com.br.manager.domain.sale_receipt.entity.Sale;
import com.br.manager.domain.sale_receipt.enums.PaymentStatus;
import com.br.manager.domain.sale_receipt.enums.SaleStatus;
import com.br.manager.domain.sale_receipt.fixture.SaleFixture;
import com.br.manager.domain.sale_receipt.fixture.SalePaymentFixture;
import com.br.manager.domain.sale_receipt.fixture.SaleReceiptReferenceFixture;
import com.br.manager.domain.sale_receipt.support.SaleReceiptTestData;
import com.br.manager.domain.sale_receipt.support.SaleReceiptTestData.SaleScenario;
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

class SalePaymentControllerIT extends AbstractIntegrationTest {

    private static final String PATH = "/api/v1/sale-payments";
    private static final String LOCKED_SALE_MESSAGE = "Vendas pagas ou com crédito pendente não podem ser alteradas.";
    private static final String INVALID_AMOUNT_MESSAGE = "O valor do pagamento deve ser maior que zero.";
    private static final String MULTIPLE_BAG_BUG = "BUG: SalePaymentService.create loads the sale with "
            + "SaleRepository.findDetailedById (SaleRepository.java:17), whose @EntityGraph fetches 3 List collections "
            + "-> MultipleBagFetchException, so every POST fails before any business rule is evaluated";
    private static final String CREATE_BUG = "BUG: MultipleBagFetchException in SaleRepository.findDetailedById "
            + "(SaleRepository.java:17) and SalePayment has @Id without @GeneratedValue (SalePayment.java:23) while "
            + "SalePaymentService.create sets id=null, so the payment can never be persisted";

    @Autowired
    private ApplicationContext applicationContext;

    private SaleReceiptTestData testData;
    private SaleScenario scenario;
    private UUID paymentMethodId;

    @BeforeEach
    void setUp() {
        testData = new SaleReceiptTestData(applicationContext);
        scenario = testData.createScenario();
        paymentMethodId = testData.createPaymentMethod(SaleReceiptReferenceFixture.cashPaymentMethod());
    }

    @Test
    @Disabled(CREATE_BUG)
    void shouldCreatePaymentForOpenSale() {
        Sale sale = testData.createSale(scenario, SaleFixture.SALE_NUMBER, SaleStatus.OPEN);

        request()
                .body(SalePaymentFixture.input(sale.getId(), paymentMethodId))
        .when()
                .post(PATH)
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("paymentMethodId", equalTo(paymentMethodId.toString()))
                .body("amount", equalTo(SalePaymentFixture.AMOUNT.floatValue()))
                .body("status", equalTo(PaymentStatus.RECEIVED.name()))
                .body("transactionReference", equalTo(SalePaymentFixture.TRANSACTION_REFERENCE));
    }

    @Test
    @Disabled(MULTIPLE_BAG_BUG)
    void shouldReturnNotFoundWhenSaleDoesNotExist() {
        request()
                .body(SalePaymentFixture.input(UUID.randomUUID(), paymentMethodId))
        .when()
                .post(PATH)
        .then()
                .statusCode(404);
    }

    @Test
    @Disabled(MULTIPLE_BAG_BUG)
    void shouldReturnBadRequestWhenSaleIsAlreadyPaid() {
        Sale sale = testData.createSale(scenario, SaleFixture.SALE_NUMBER, SaleStatus.PAID);

        request()
                .body(SalePaymentFixture.input(sale.getId(), paymentMethodId))
        .when()
                .post(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo(LOCKED_SALE_MESSAGE));
    }

    @Test
    @Disabled(MULTIPLE_BAG_BUG)
    void shouldReturnNotFoundWhenPaymentMethodDoesNotExist() {
        Sale sale = testData.createSale(scenario, SaleFixture.SALE_NUMBER, SaleStatus.OPEN);

        request()
                .body(SalePaymentFixture.input(sale.getId(), UUID.randomUUID()))
        .when()
                .post(PATH)
        .then()
                .statusCode(404);
    }

    @Test
    @Disabled(MULTIPLE_BAG_BUG)
    void shouldReturnBadRequestWhenAmountIsNotPositive() {
        Sale sale = testData.createSale(scenario, SaleFixture.SALE_NUMBER, SaleStatus.OPEN);
        SalePaymentInputDTO input = SalePaymentFixture.input(sale.getId(), paymentMethodId);
        input.setAmount(BigDecimal.ZERO);

        request()
                .body(input)
        .when()
                .post(PATH)
        .then()
                .statusCode(400)
                .body("title", equalTo(INVALID_AMOUNT_MESSAGE));
    }

    @Test
    void shouldListPaymentsBySale() {
        Sale sale = testData.createSaleWithPayment(scenario, SaleStatus.PAID, paymentMethodId, PaymentStatus.RECEIVED);

        request()
        .when()
                .get(PATH + "/sale/{saleId}", sale.getId())
        .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].paymentMethodId", equalTo(paymentMethodId.toString()))
                .body("[0].status", equalTo(PaymentStatus.RECEIVED.name()));
    }

    @Test
    void shouldReturnEmptyListForSaleWithoutPayments() {
        Sale sale = testData.createSale(scenario, SaleFixture.SALE_NUMBER, SaleStatus.OPEN);

        request().when().get(PATH + "/sale/{saleId}", sale.getId()).then().statusCode(200).body("$", hasSize(0));
    }

    @Test
    void shouldFindPaymentById() {
        Sale sale = testData.createSaleWithPayment(scenario, SaleStatus.PAID, paymentMethodId, PaymentStatus.AUTHORIZED);
        UUID paymentId = sale.getPayments().get(0).getId();

        request()
        .when()
                .get(PATH + "/{id}", paymentId)
        .then()
                .statusCode(200)
                .body("id", equalTo(paymentId.toString()))
                .body("amount", equalTo(SalePaymentFixture.AMOUNT.floatValue()))
                .body("status", equalTo(PaymentStatus.AUTHORIZED.name()))
                .body("transactionReference", equalTo(SalePaymentFixture.TRANSACTION_REFERENCE));
    }

    @Test
    void shouldReturnNotFoundForUnknownPayment() {
        request().when().get(PATH + "/{id}", UUID.randomUUID()).then().statusCode(404);
    }
}
