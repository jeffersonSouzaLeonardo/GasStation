package com.br.manager.domain.sale_receipt.mapper;

import com.br.manager.domain.sale_receipt.dto.SalePaymentInputDTO;
import com.br.manager.domain.sale_receipt.dto.SalePaymentResponseDTO;
import com.br.manager.domain.sale_receipt.entity.SalePayment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SalePaymentMapper {

    public SalePayment toEntity(SalePaymentInputDTO inputDTO) {
        SalePayment entity = new SalePayment();
        entity.setId(inputDTO.getId());
        entity.setPaymentMethodId(inputDTO.getPaymentMethodId());
        entity.setAmount(inputDTO.getAmount());
        entity.setStatus(inputDTO.getStatus());
        entity.setTransactionReference(inputDTO.getTransactionReference());
        entity.setAuthorizedAt(inputDTO.getAuthorizedAt());
        entity.setReceivedAt(inputDTO.getReceivedAt());
        return entity;
    }

    public SalePaymentResponseDTO toResponse(SalePayment payment) {
        return new SalePaymentResponseDTO(
                payment.getId(), payment.getPaymentMethodId(), payment.getAmount(), payment.getStatus(),
                payment.getTransactionReference(), payment.getAuthorizedAt(), payment.getReceivedAt()
        );
    }

    public List<SalePaymentResponseDTO> toResponseList(List<SalePayment> payments) {
        return payments.stream().map(this::toResponse).toList();
    }
}
