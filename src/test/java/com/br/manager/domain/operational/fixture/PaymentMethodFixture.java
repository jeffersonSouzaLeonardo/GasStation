package com.br.manager.domain.operational.fixture;

import com.br.manager.domain.operational.dto.PaymentMethodInputDTO;
import com.br.manager.domain.operational.entity.PaymentMethod;
import com.br.manager.domain.operational.enums.PaymentMethodKindEnum;

import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Forma de Pagamento.
 */
public final class PaymentMethodFixture {

    public static final String NAME = "Cartao de Credito Visa";
    public static final String OTHER_NAME = "Dinheiro";
    public static final PaymentMethodKindEnum KIND = PaymentMethodKindEnum.CREDIT_CARD;
    public static final int SETTLEMENT_DAYS = 30;

    private PaymentMethodFixture() {
    }

    public static PaymentMethodInputDTO validInput() {
        PaymentMethodInputDTO input = new PaymentMethodInputDTO();
        input.setName(NAME);
        input.setKind(KIND);
        input.setRequiresReference(true);
        input.setSettlementDays(SETTLEMENT_DAYS);
        input.setActive(true);
        return input;
    }

    public static PaymentMethodInputDTO otherInput() {
        PaymentMethodInputDTO input = new PaymentMethodInputDTO();
        input.setName(OTHER_NAME);
        input.setKind(PaymentMethodKindEnum.MONEY);
        return input;
    }

    public static PaymentMethod persistedEntity() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(UUID.randomUUID());
        paymentMethod.setName(NAME);
        paymentMethod.setKind(KIND);
        paymentMethod.setRequiresReference(true);
        paymentMethod.setSettlementDays(SETTLEMENT_DAYS);
        paymentMethod.setActive(true);
        return paymentMethod;
    }
}
