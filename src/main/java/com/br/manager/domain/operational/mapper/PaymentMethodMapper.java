package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.PaymentMethodInputDTO;
import com.br.manager.domain.operational.dto.PaymentMethodResponseDTO;
import com.br.manager.domain.operational.entity.PaymentMethod;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentMethodMapper {
    public PaymentMethod paymentMethodInputDTOToPaymentMethod(PaymentMethodInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        PaymentMethod entity = new PaymentMethod();
        BeanUtils.copyProperties(inputDTO, entity);
        return entity;
    }

    public PaymentMethodResponseDTO paymentMethodToPaymentMethodResponseDTO(PaymentMethod entity) {
        if (entity == null) {
            return null;
        }
        PaymentMethodResponseDTO dto = new PaymentMethodResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public List<PaymentMethodResponseDTO> listPaymentMethodToListPaymentMethodResponseDTO(List<PaymentMethod> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(this::paymentMethodToPaymentMethodResponseDTO).toList();
    }

    public void updatePaymentMethodFromDto(PaymentMethodInputDTO dto, PaymentMethod entity) {
        if (dto == null || entity == null) {
            return;
        }
        BeanUtils.copyProperties(dto, entity);
    }
}
