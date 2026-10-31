package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.PaymentMethodInputDTO;
import com.br.manager.domain.operational.dto.PaymentMethodResponseDTO;
import com.br.manager.domain.operational.entity.PaymentMethod;
import com.br.manager.domain.operational.enums.PaymentMethodKindEnum;
import com.br.manager.domain.operational.fixture.PaymentMethodFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentMethodMapperTest {

    private final PaymentMethodMapper mapper = new PaymentMethodMapper();

    @Test
    void shouldMapInputToEntity() {
        PaymentMethodInputDTO input = PaymentMethodFixture.validInput();
        input.setId(UUID.randomUUID());

        PaymentMethod paymentMethod = mapper.paymentMethodInputDTOToPaymentMethod(input);

        assertThat(paymentMethod)
                .usingRecursiveComparison()
                .isEqualTo(input);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.paymentMethodInputDTOToPaymentMethod(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        PaymentMethod paymentMethod = PaymentMethodFixture.persistedEntity();

        PaymentMethodResponseDTO response = mapper.paymentMethodToPaymentMethodResponseDTO(paymentMethod);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(paymentMethod);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.paymentMethodToPaymentMethodResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<PaymentMethod> paymentMethods = List.of(PaymentMethodFixture.persistedEntity(), PaymentMethodFixture.persistedEntity());

        List<PaymentMethodResponseDTO> responses = mapper.listPaymentMethodToListPaymentMethodResponseDTO(paymentMethods);

        assertThat(responses)
                .extracting(PaymentMethodResponseDTO::getId)
                .containsExactly(paymentMethods.get(0).getId(), paymentMethods.get(1).getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listPaymentMethodToListPaymentMethodResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityFromInput() {
        PaymentMethod paymentMethod = PaymentMethodFixture.persistedEntity();
        PaymentMethodInputDTO input = PaymentMethodFixture.otherInput();
        input.setId(paymentMethod.getId());

        mapper.updatePaymentMethodFromDto(input, paymentMethod);

        assertThat(paymentMethod.getName()).isEqualTo(PaymentMethodFixture.OTHER_NAME);
        assertThat(paymentMethod.getKind()).isEqualTo(PaymentMethodKindEnum.MONEY);
        assertThat(paymentMethod.getRequiresReference()).isFalse();
        assertThat(paymentMethod.getSettlementDays()).isZero();
    }

    @Test
    void shouldIgnoreUpdateWhenEntityIsNull() {
        PaymentMethodInputDTO input = PaymentMethodFixture.validInput();

        mapper.updatePaymentMethodFromDto(input, null);

        assertThat(input.getName()).isEqualTo(PaymentMethodFixture.NAME);
    }
}
