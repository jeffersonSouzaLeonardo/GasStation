package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.PaymentMethodInputDTO;
import com.br.manager.domain.operational.dto.PaymentMethodResponseDTO;
import com.br.manager.domain.operational.entity.PaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class PaymentMethodMapper {
    public abstract PaymentMethod paymentMethodInputDTOToPaymentMethod(PaymentMethodInputDTO inputDTO);

    public abstract PaymentMethodResponseDTO paymentMethodToPaymentMethodResponseDTO(PaymentMethod entity);

    public abstract List<PaymentMethodResponseDTO> listPaymentMethodToListPaymentMethodResponseDTO(List<PaymentMethod> entities);

    public abstract void updatePaymentMethodFromDto(PaymentMethodInputDTO dto, @MappingTarget PaymentMethod entity);
}
