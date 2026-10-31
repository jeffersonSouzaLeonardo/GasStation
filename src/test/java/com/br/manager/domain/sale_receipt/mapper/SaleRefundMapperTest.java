package com.br.manager.domain.sale_receipt.mapper;

import com.br.manager.domain.sale_receipt.dto.SaleRefundInputDTO;
import com.br.manager.domain.sale_receipt.dto.SaleRefundResponseDTO;
import com.br.manager.domain.sale_receipt.entity.SaleRefund;
import com.br.manager.domain.sale_receipt.fixture.SaleRefundFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SaleRefundMapperTest {

    private final SaleRefundMapper mapper = new SaleRefundMapper();

    @Test
    void shouldMapInputToEntityWithoutSale() {
        SaleRefundInputDTO input = SaleRefundFixture.input(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        input.setId(UUID.randomUUID());

        SaleRefund refund = mapper.toEntity(input);

        assertThat(refund)
                .usingRecursiveComparison()
                .ignoringFields("sale")
                .isEqualTo(input);
        assertThat(refund.getSale()).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        SaleRefund refund = SaleRefundFixture.persistedEntity();

        SaleRefundResponseDTO response = mapper.toResponse(refund);

        assertThat(response).usingRecursiveComparison().isEqualTo(refund);
    }

    @Test
    void shouldMapEntityListToResponseListKeepingOrder() {
        List<SaleRefund> refunds = List.of(SaleRefundFixture.persistedEntity(), SaleRefundFixture.persistedEntity());

        List<SaleRefundResponseDTO> responses = mapper.toResponseList(refunds);

        assertThat(responses)
                .extracting(SaleRefundResponseDTO::id)
                .containsExactly(refunds.get(0).getId(), refunds.get(1).getId());
    }

    @Test
    void shouldMapEmptyListToEmptyResponseList() {
        assertThat(mapper.toResponseList(List.of())).isEmpty();
    }
}
