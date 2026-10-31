package com.br.manager.domain.sale_receipt.dto;

import com.br.manager.domain.sale_receipt.enums.FiscalStatus;
import com.br.manager.domain.sale_receipt.enums.PaymentStatus;
import com.br.manager.domain.sale_receipt.fixture.SaleFixture;
import com.br.manager.domain.sale_receipt.fixture.SaleItemFixture;
import com.br.manager.domain.sale_receipt.fixture.SalePaymentFixture;
import com.br.manager.domain.sale_receipt.fixture.SaleRefundFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SaleReceiptRequestDtoValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeFactory() {
        factory.close();
    }

    private static <T> Set<String> messages(T dto) {
        return validator.validate(dto).stream().map(ConstraintViolation::getMessage)
                .collect(java.util.stream.Collectors.toSet());
    }

    private static CreateSaleItemDTO validItem() {
        return new CreateSaleItemDTO(SaleItemFixture.LINE_NUMBER, UUID.randomUUID(), null, null,
                SaleItemFixture.QUANTITY, SaleItemFixture.UNIT_PRICE, SaleItemFixture.DISCOUNT, null, null);
    }

    private static CreateSaleRequestDTO validSale(List<CreateSaleItemDTO> items) {
        return new CreateSaleRequestDTO(UUID.randomUUID(), UUID.randomUUID(), SaleFixture.SALE_NUMBER, SaleFixture.SOLD_AT,
                null, null, null, UUID.randomUUID(), SaleFixture.DISCOUNT, FiscalStatus.PENDING_EMISSION,
                SaleFixture.NOTES, items);
    }

    @Test
    void shouldAcceptValidCreateSaleRequest() {
        assertThat(validator.validate(validSale(List.of(validItem())))).isEmpty();
    }

    @Test
    void shouldReportAllMissingRequiredFieldsOfCreateSaleRequest() {
        CreateSaleRequestDTO dto = new CreateSaleRequestDTO(null, null, " ", null, null, null, null, null,
                null, null, null, List.of());

        assertThat(messages(dto)).containsExactlyInAnyOrder(
                "Station ID is required.",
                "Shift ID is required.",
                "Sale number is required.",
                "Sold at is required.",
                "Cashier user ID is required.",
                "Sale must contain at least one item.");
    }

    @Test
    void shouldCascadeValidationToSaleItems() {
        CreateSaleItemDTO invalidItem = new CreateSaleItemDTO(null, null, null, null, null, null, null, null, null);

        assertThat(messages(validSale(List.of(invalidItem)))).containsExactlyInAnyOrder(
                "Line number is required.",
                "Product ID is required.",
                "Quantity is required.",
                "Unit price is required.");
    }

    @Test
    void shouldAcceptValidPaymentRequest() {
        AddSalePaymentRequestDTO dto = new AddSalePaymentRequestDTO(UUID.randomUUID(), SalePaymentFixture.AMOUNT,
                PaymentStatus.RECEIVED, SalePaymentFixture.TRANSACTION_REFERENCE, null, null);

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void shouldReportMissingFieldsOfPaymentRequest() {
        AddSalePaymentRequestDTO dto = new AddSalePaymentRequestDTO(null, null, null, null, null, null);

        assertThat(messages(dto)).containsExactlyInAnyOrder(
                "Payment method ID is required.",
                "Amount is required.",
                "Status is required.");
    }

    @Test
    void shouldAcceptValidRefundRequest() {
        RefundSaleRequestDTO dto = new RefundSaleRequestDTO(SaleRefundFixture.REASON, UUID.randomUUID(), UUID.randomUUID(),
                null, SaleRefundFixture.NOTES);

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void shouldReportMissingFieldsOfRefundRequest() {
        RefundSaleRequestDTO dto = new RefundSaleRequestDTO("", null, null, null, null);

        assertThat(messages(dto)).containsExactlyInAnyOrder(
                "Reason is required.",
                "Requested by is required.",
                "Approved by is required.");
    }
}
