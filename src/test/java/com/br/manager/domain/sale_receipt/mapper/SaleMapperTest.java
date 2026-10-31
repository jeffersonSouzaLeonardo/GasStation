package com.br.manager.domain.sale_receipt.mapper;

import com.br.manager.domain.sale_receipt.dto.SaleItemResponseDTO;
import com.br.manager.domain.sale_receipt.dto.SalePaymentResponseDTO;
import com.br.manager.domain.sale_receipt.dto.SaleRefundResponseDTO;
import com.br.manager.domain.sale_receipt.dto.SaleResponseDTO;
import com.br.manager.domain.sale_receipt.entity.Sale;
import com.br.manager.domain.sale_receipt.entity.SaleItem;
import com.br.manager.domain.sale_receipt.entity.SalePayment;
import com.br.manager.domain.sale_receipt.entity.SaleRefund;
import com.br.manager.domain.sale_receipt.enums.PaymentStatus;
import com.br.manager.domain.sale_receipt.enums.SaleStatus;
import com.br.manager.domain.sale_receipt.fixture.SaleFixture;
import com.br.manager.domain.sale_receipt.fixture.SaleItemFixture;
import com.br.manager.domain.sale_receipt.fixture.SalePaymentFixture;
import com.br.manager.domain.sale_receipt.fixture.SaleRefundFixture;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SaleMapperTest {

    private final SaleMapper mapper = new SaleMapper();

    @Test
    void shouldMapSaleHeaderToResponse() {
        Sale sale = SaleFixture.persistedEntity(SaleStatus.OPEN);
        sale.setCustomerId(UUID.randomUUID());
        sale.setVehicleId(UUID.randomUUID());
        sale.setAttendantUserId(UUID.randomUUID());

        SaleResponseDTO response = mapper.toResponse(sale);

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("items", "payments", "refunds")
                .isEqualTo(sale);
    }

    @Test
    void shouldMapNestedCollectionsOfSale() {
        Sale sale = SaleFixture.persistedEntity(SaleStatus.PAID);
        SaleItem item = SaleItemFixture.persistedEntity(UUID.randomUUID());
        SalePayment payment = SalePaymentFixture.persistedEntity(PaymentStatus.RECEIVED);
        SaleRefund refund = SaleRefundFixture.persistedEntity();
        sale.addItem(item);
        sale.addPayment(payment);
        sale.addRefund(refund);

        SaleResponseDTO response = mapper.toResponse(sale);

        assertThat(response.items()).extracting(SaleItemResponseDTO::id).containsExactly(item.getId());
        assertThat(response.payments()).extracting(SalePaymentResponseDTO::id).containsExactly(payment.getId());
        assertThat(response.refunds()).extracting(SaleRefundResponseDTO::id).containsExactly(refund.getId());
    }

    @Test
    void shouldMapSaleWithoutChildrenToEmptyLists() {
        SaleResponseDTO response = mapper.toResponse(SaleFixture.persistedEntity(SaleStatus.OPEN));

        assertThat(response.items()).isEmpty();
        assertThat(response.payments()).isEmpty();
        assertThat(response.refunds()).isEmpty();
    }

    @Test
    void shouldMapItemToResponse() {
        SaleItem item = SaleItemFixture.persistedFuelEntity();

        SaleItemResponseDTO response = mapper.toItemResponse(item);

        assertThat(response).usingRecursiveComparison().isEqualTo(item);
    }

    @Test
    void shouldMapPaymentToResponse() {
        SalePayment payment = SalePaymentFixture.persistedEntity(PaymentStatus.AUTHORIZED);

        SalePaymentResponseDTO response = mapper.toPaymentResponse(payment);

        assertThat(response).usingRecursiveComparison().isEqualTo(payment);
    }

    @Test
    void shouldMapRefundToResponse() {
        SaleRefund refund = SaleRefundFixture.persistedEntity();

        SaleRefundResponseDTO response = mapper.toRefundResponse(refund);

        assertThat(response).usingRecursiveComparison().isEqualTo(refund);
    }
}
