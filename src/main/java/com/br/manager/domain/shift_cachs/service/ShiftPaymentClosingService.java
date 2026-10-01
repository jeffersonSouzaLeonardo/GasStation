package com.br.manager.domain.shift_cachs.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.shift_cachs.dto.PaymentClosingInputDTO;
import com.br.manager.domain.shift_cachs.dto.PaymentClosingResponseDTO;
import com.br.manager.domain.shift_cachs.entity.PaymentClosing;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.mapper.PaymentClosingMapper;
import com.br.manager.domain.shift_cachs.repository.PaymentClosingRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ShiftPaymentClosingService {

    @Autowired
    private ShiftValidationSupport validationSupport;

    @Autowired
    private PaymentClosingRepository paymentClosingRepository;

    @Autowired
    private PaymentClosingMapper paymentClosingMapper;

    @Transactional
    public PaymentClosingResponseDTO registerPaymentClosing(UUID shiftId, PaymentClosingInputDTO request) {
        if (request == null) {
            throw new BusinessException("O payload do fechamento de pagamento é obrigatório.");
        }

        Shift shift = validationSupport.getRequiredShift(shiftId);
        validationSupport.ensureShiftIsOpen(shift);

        if (request.getExpectedAmount() == null || request.getExpectedAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("O valor esperado é obrigatório e deve ser maior ou igual a zero.");
        }
        if (request.getDeclaredAmount() == null || request.getDeclaredAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("O valor declarado é obrigatório e deve ser maior ou igual a zero.");
        }

        List<PaymentClosing> existingClosings = paymentClosingRepository.findByShiftIdOrderByPaymentMethodId(shiftId);
        PaymentClosing paymentClosing = existingClosings.stream()
                .filter(item -> item.getPaymentMethodId().equals(request.getPaymentMethodId()))
                .findFirst()
                .orElse(null);

        if (paymentClosing == null) {
            paymentClosing = new PaymentClosing();
            paymentClosing.setShift(shift);
            paymentClosing.setPaymentMethodId(request.getPaymentMethodId());
        }

        paymentClosing.setExpectedAmount(request.getExpectedAmount());
        paymentClosing.setDeclaredAmount(request.getDeclaredAmount());
        paymentClosing.setDifferenceAmount(request.getDeclaredAmount().subtract(request.getExpectedAmount()));
        paymentClosing.setEvidenceReference(request.getEvidenceReference());

        PaymentClosing savedClosing = paymentClosingRepository.saveAndFlush(paymentClosing);
        return paymentClosingMapper.toResponse(savedClosing);
    }
}
