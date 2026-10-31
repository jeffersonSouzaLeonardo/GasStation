package com.br.manager.domain.sale_receipt.mapper;

import com.br.manager.domain.sale_receipt.dto.SalePaymentInputDTO;
import com.br.manager.domain.sale_receipt.dto.SalePaymentResponseDTO;
import com.br.manager.domain.sale_receipt.entity.SalePayment;
import com.br.manager.domain.sale_receipt.enums.PaymentStatus;
import com.br.manager.domain.sale_receipt.fixture.SalePaymentFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SalePaymentMapperTest {

    private final SalePaymentMapper mapper = new SalePaymentMapper();

    @Test
    void shouldMapInputToEntityWithoutSale() {
        SalePaymentInputDTO input = SalePaymentFixture.input(UUID.randomUUID(), UUID.randomUUID());
        input.setId(UUID.randomUUID());

        SalePayment payment = mapper.toEntity(input);

        assertThat(payment)
                .usingRecursiveComparison()
                .ignoringFields("sale")
                .isEqualTo(input);
        assertThat(payment.getSale()).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        SalePayment payment = SalePaymentFixture.persistedEntity(PaymentStatus.RECEIVED);

        SalePaymentResponseDTO response = mapper.toResponse(payment);

        assertThat(response).usingRecursiveComparison().isEqualTo(payment);
    }

    @Test
    void shouldMapEntityListToResponseListKeepingOrder() {
        List<SalePayment> payments = List.of(
                SalePaymentFixture.persistedEntity(PaymentStatus.PENDING),
                SalePaymentFixture.persistedEntity(PaymentStatus.RECEIVED));

        List<SalePaymentResponseDTO> responses = mapper.toResponseList(payments);

        assertThat(responses)
                .extracting(SalePaymentResponseDTO::status)
                .containsExactly(PaymentStatus.PENDING, PaymentStatus.RECEIVED);
    }

    @Test
    void shouldMapEmptyListToEmptyResponseList() {
        assertThat(mapper.toResponseList(List.of())).isEmpty();
    }
}
