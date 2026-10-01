package com.br.manager.domain.sale_receipt.mapper;

import com.br.manager.domain.sale_receipt.dto.SaleItemResponseDTO;
import com.br.manager.domain.sale_receipt.dto.SalePaymentResponseDTO;
import com.br.manager.domain.sale_receipt.dto.SaleRefundResponseDTO;
import com.br.manager.domain.sale_receipt.dto.SaleResponseDTO;
import com.br.manager.domain.sale_receipt.entity.Sale;
import com.br.manager.domain.sale_receipt.entity.SaleItem;
import com.br.manager.domain.sale_receipt.entity.SalePayment;
import com.br.manager.domain.sale_receipt.entity.SaleRefund;
import org.springframework.stereotype.Component;

@Component
public class SaleMapper {

    public SaleResponseDTO toResponse(Sale sale) {
        return new SaleResponseDTO(
                sale.getId(),
                sale.getStationId(),
                sale.getShiftId(),
                sale.getSaleNumber(),
                sale.getStatus(),
                sale.getSoldAt(),
                sale.getCustomerId(),
                sale.getVehicleId(),
                sale.getAttendantUserId(),
                sale.getCashierUserId(),
                sale.getSubtotal(),
                sale.getDiscountAmount(),
                sale.getFiscalStatus(),
                sale.getNotes(),
                sale.getItems().stream().map(this::toItemResponse).toList(),
                sale.getPayments().stream().map(this::toPaymentResponse).toList(),
                sale.getRefunds().stream().map(this::toRefundResponse).toList()
        );
    }

    public SaleItemResponseDTO toItemResponse(SaleItem item) {
        return new SaleItemResponseDTO(
                item.getId(),
                item.getLineNumber(),
                item.getProductId(),
                item.getNozzleId(),
                item.getTankId(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getDiscountAmount(),
                item.getMeterStart(),
                item.getMeterEnd()
        );
    }

    public SalePaymentResponseDTO toPaymentResponse(SalePayment payment) {
        return new SalePaymentResponseDTO(
                payment.getId(),
                payment.getPaymentMethodId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getTransactionReference(),
                payment.getAuthorizedAt(),
                payment.getReceivedAt()
        );
    }

    public SaleRefundResponseDTO toRefundResponse(SaleRefund refund) {
        return new SaleRefundResponseDTO(
                refund.getId(),
                refund.getReason(),
                refund.getRequestedBy(),
                refund.getApprovedBy(),
                refund.getRefundedAt(),
                refund.getNotes()
        );
    }

}
