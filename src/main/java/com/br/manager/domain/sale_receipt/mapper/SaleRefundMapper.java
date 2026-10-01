package com.br.manager.domain.sale_receipt.mapper;

import com.br.manager.domain.sale_receipt.dto.SaleRefundInputDTO;
import com.br.manager.domain.sale_receipt.dto.SaleRefundResponseDTO;
import com.br.manager.domain.sale_receipt.entity.SaleRefund;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SaleRefundMapper {

    public SaleRefund toEntity(SaleRefundInputDTO inputDTO) {
        SaleRefund entity = new SaleRefund();
        entity.setId(inputDTO.getId());
        entity.setReason(inputDTO.getReason());
        entity.setRequestedBy(inputDTO.getRequestedBy());
        entity.setApprovedBy(inputDTO.getApprovedBy());
        entity.setRefundedAt(inputDTO.getRefundedAt());
        entity.setNotes(inputDTO.getNotes());
        return entity;
    }

    public SaleRefundResponseDTO toResponse(SaleRefund refund) {
        return new SaleRefundResponseDTO(
                refund.getId(), refund.getReason(), refund.getRequestedBy(), refund.getApprovedBy(),
                refund.getRefundedAt(), refund.getNotes()
        );
    }

    public List<SaleRefundResponseDTO> toResponseList(List<SaleRefund> refunds) {
        return refunds.stream().map(this::toResponse).toList();
    }
}
