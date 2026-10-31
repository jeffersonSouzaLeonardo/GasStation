package com.br.manager.domain.shift_cachs.mapper;

import com.br.manager.domain.shift_cachs.dto.PaymentClosingInputDTO;
import com.br.manager.domain.shift_cachs.dto.PaymentClosingResponseDTO;
import com.br.manager.domain.shift_cachs.entity.PaymentClosing;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import com.br.manager.domain.shift_cachs.fixture.PaymentClosingFixture;
import com.br.manager.domain.shift_cachs.support.ShiftBTestData;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentClosingMapperTest {

    private final PaymentClosingMapper mapper = new PaymentClosingMapper();
    private final Shift shift = ShiftBTestData.shiftEntity(ShiftStatusEnum.OPEN);

    @Test
    void shouldMapInputToEntityLinkingShift() {
        PaymentClosingInputDTO input = PaymentClosingFixture.validInput(UUID.randomUUID());

        PaymentClosing closing = mapper.toEntity(input, shift);

        assertThat(closing.getId()).isNull();
        assertThat(closing.getShift()).isSameAs(shift);
        assertThat(closing.getPaymentMethodId()).isEqualTo(input.getPaymentMethodId());
        assertThat(closing.getExpectedAmount()).isEqualTo(PaymentClosingFixture.EXPECTED_AMOUNT);
        assertThat(closing.getDeclaredAmount()).isEqualTo(PaymentClosingFixture.DECLARED_AMOUNT);
        assertThat(closing.getEvidenceReference()).isEqualTo(PaymentClosingFixture.EVIDENCE_REFERENCE);
    }

    @Test
    void shouldComputeDifferenceAsDeclaredMinusExpected() {
        PaymentClosing closing = mapper.toEntity(PaymentClosingFixture.validInput(UUID.randomUUID()), shift);

        assertThat(closing.getDifferenceAmount()).isEqualByComparingTo(PaymentClosingFixture.DIFFERENCE_AMOUNT);
    }

    @Test
    void shouldReturnNullEntityWhenInputIsNull() {
        assertThat(mapper.toEntity(null, shift)).isNull();
    }

    @Test
    void shouldMapEntityToResponseWithShiftId() {
        PaymentClosing closing = PaymentClosingFixture.persistedEntity(shift, UUID.randomUUID());

        PaymentClosingResponseDTO response = mapper.toResponse(closing);

        assertThat(response.getShiftId()).isEqualTo(shift.getId());
        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("shiftId")
                .isEqualTo(closing);
    }

    @Test
    void shouldMapNullShiftIdWhenClosingHasNoShift() {
        PaymentClosing closing = PaymentClosingFixture.persistedEntity(null, UUID.randomUUID());

        assertThat(mapper.toResponse(closing).getShiftId()).isNull();
    }

    @Test
    void shouldReturnNullResponseWhenEntityIsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }
}
