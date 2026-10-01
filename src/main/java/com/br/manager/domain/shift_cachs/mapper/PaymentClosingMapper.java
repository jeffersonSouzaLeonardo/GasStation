package com.br.manager.domain.shift_cachs.mapper;

import com.br.manager.domain.shift_cachs.dto.PaymentClosingInputDTO;
import com.br.manager.domain.shift_cachs.dto.PaymentClosingResponseDTO;
import com.br.manager.domain.shift_cachs.entity.PaymentClosing;
import com.br.manager.domain.shift_cachs.entity.Shift;
import org.springframework.stereotype.Component;

@Component
public class PaymentClosingMapper {

    public PaymentClosing toEntity(PaymentClosingInputDTO dto, Shift shift) {
        if (dto == null) {
            return null;
        }

        PaymentClosing closing = new PaymentClosing();
        closing.setShift(shift);
        closing.setPaymentMethodId(dto.getPaymentMethodId());
        closing.setExpectedAmount(dto.getExpectedAmount());
        closing.setDeclaredAmount(dto.getDeclaredAmount());
        closing.setDifferenceAmount(dto.getDeclaredAmount().subtract(dto.getExpectedAmount()));
        closing.setEvidenceReference(dto.getEvidenceReference());
        return closing;
    }

    public PaymentClosingResponseDTO toResponse(PaymentClosing closing) {
        if (closing == null) {
            return null;
        }

        PaymentClosingResponseDTO dto = new PaymentClosingResponseDTO();
        dto.setId(closing.getId());
        dto.setShiftId(closing.getShift() != null ? closing.getShift().getId() : null);
        dto.setPaymentMethodId(closing.getPaymentMethodId());
        dto.setExpectedAmount(closing.getExpectedAmount());
        dto.setDeclaredAmount(closing.getDeclaredAmount());
        dto.setDifferenceAmount(closing.getDifferenceAmount());
        dto.setEvidenceReference(closing.getEvidenceReference());
        return dto;
    }
}
