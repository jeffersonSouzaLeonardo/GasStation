package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.PaymentMethodInputDTO;
import com.br.manager.domain.operational.dto.PaymentMethodResponseDTO;
import com.br.manager.domain.operational.entity.PaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {
    PaymentMethod paymentMethodInputDTOToPaymentMethod(PaymentMethodInputDTO inputDTO);

    PaymentMethodResponseDTO paymentMethodToPaymentMethodResponseDTO(PaymentMethod entity);

    List<PaymentMethodResponseDTO> listPaymentMethodToListPaymentMethodResponseDTO(List<PaymentMethod> entities);

    void updatePaymentMethodFromDto(PaymentMethodInputDTO dto, @MappingTarget PaymentMethod entity);
}
