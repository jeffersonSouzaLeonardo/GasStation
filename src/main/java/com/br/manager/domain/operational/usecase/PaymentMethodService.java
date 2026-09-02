package com.br.manager.domain.operational.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.dto.PaymentMethodInputDTO;
import com.br.manager.domain.operational.dto.PaymentMethodResponseDTO;
import com.br.manager.domain.operational.entity.PaymentMethod;
import com.br.manager.domain.operational.mapper.PaymentMethodMapper;
import com.br.manager.domain.operational.repository.PaymentMethodRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentMethodService {
    @Autowired private PaymentMethodRepository paymentMethodRepository;
    @Autowired private PaymentMethodMapper paymentMethodMapper;

    public PaymentMethodResponseDTO create(PaymentMethodInputDTO inputDTO) {
        try {
            PaymentMethod entity = paymentMethodMapper.paymentMethodInputDTOToPaymentMethod(inputDTO);
            if (entity.getId() == null) { entity.setId(UUID.randomUUID()); }
            return paymentMethodMapper.paymentMethodToPaymentMethodResponseDTO(paymentMethodRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException e) {
            throw new BusinessException(e.getConstraintViolations().stream().map(v -> v.getMessage()).toList().toString());
        } catch (Exception e) {
            throw new BusinessException("Error while saving payment method", e);
        }
    }

    public PaymentMethodResponseDTO update(PaymentMethodInputDTO inputDTO) {
        try {
            PaymentMethod entity = paymentMethodRepository.findByIdAndActiveTrue(inputDTO.getId());
            if (entity == null) { throw new NotFoundBusinessException(String.format("Payment method with ID %s not found", inputDTO.getId())); }
            paymentMethodMapper.updatePaymentMethodFromDto(inputDTO, entity);
            return paymentMethodMapper.paymentMethodToPaymentMethodResponseDTO(paymentMethodRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException e) {
            throw new BusinessException(e.getConstraintViolations().stream().map(v -> v.getMessage()).toList().toString());
        } catch (NotFoundBusinessException e) { throw e; } catch (Exception e) { throw new BusinessException("Error while updating payment method", e); }
    }

    public List<PaymentMethodResponseDTO> findAll() { return paymentMethodMapper.listPaymentMethodToListPaymentMethodResponseDTO(paymentMethodRepository.findAllByActiveTrue()); }

    public List<PaymentMethodResponseDTO> findByDescription(String description) {
        if (!StringUtils.hasText(description)) return findAll();
        return paymentMethodMapper.listPaymentMethodToListPaymentMethodResponseDTO(paymentMethodRepository.findByNameContainingIgnoreCaseAndActiveTrue(description));
    }

    public PaymentMethodResponseDTO find(UUID id) {
        PaymentMethod entity = paymentMethodRepository.findByIdAndActiveTrue(id);
        if (entity == null) throw new NotFoundBusinessException(String.format("Payment method with ID %s not found", id));
        return paymentMethodMapper.paymentMethodToPaymentMethodResponseDTO(entity);
    }

    public void delete(UUID id) {
        PaymentMethod entity = paymentMethodRepository.findById(id).orElseThrow(() -> new NotFoundBusinessException(String.format("Payment method with ID %s not found", id)));
        entity.setActive(false);
        paymentMethodRepository.saveAndFlush(entity);
    }
}
