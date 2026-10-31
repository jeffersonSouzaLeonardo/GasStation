package com.br.manager.domain.shift_cachs.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.shift_cachs.dto.PaymentClosingInputDTO;
import com.br.manager.domain.shift_cachs.dto.PaymentClosingResponseDTO;
import com.br.manager.domain.shift_cachs.entity.PaymentClosing;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import com.br.manager.domain.shift_cachs.fixture.PaymentClosingFixture;
import com.br.manager.domain.shift_cachs.mapper.PaymentClosingMapper;
import com.br.manager.domain.shift_cachs.repository.PaymentClosingRepository;
import com.br.manager.domain.shift_cachs.support.ShiftBTestData;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShiftPaymentClosingServiceTest {

    private static final BigDecimal UPDATED_DECLARED_AMOUNT = new BigDecimal("510.00");
    private static final BigDecimal UPDATED_DIFFERENCE = new BigDecimal("10.00");

    @Mock
    private ShiftValidationSupport validationSupport;

    @Mock
    private PaymentClosingRepository paymentClosingRepository;

    @Spy
    private PaymentClosingMapper paymentClosingMapper = new PaymentClosingMapper();

    @InjectMocks
    private ShiftPaymentClosingService service;

    private final Shift shift = ShiftBTestData.shiftEntity(ShiftStatusEnum.OPEN);
    private final UUID paymentMethodId = UUID.randomUUID();
    private final PaymentClosingInputDTO input = PaymentClosingFixture.validInput(paymentMethodId);

    private void givenOpenShift() {
        when(validationSupport.getRequiredShift(shift.getId())).thenReturn(shift);
    }

    private PaymentClosing captureSavedClosing() {
        ArgumentCaptor<PaymentClosing> captor = ArgumentCaptor.forClass(PaymentClosing.class);
        verify(paymentClosingRepository).saveAndFlush(captor.capture());
        return captor.getValue();
    }

    @Nested
    class Register {

        @Test
        void shouldCreateClosingWithComputedDifference() {
            givenOpenShift();
            when(paymentClosingRepository.findByShiftIdOrderByPaymentMethodId(shift.getId())).thenReturn(List.of());
            when(paymentClosingRepository.saveAndFlush(any(PaymentClosing.class))).thenAnswer(invocation -> invocation.getArgument(0));

            PaymentClosingResponseDTO response = service.registerPaymentClosing(shift.getId(), input);

            PaymentClosing saved = captureSavedClosing();
            assertThat(saved.getShift()).isSameAs(shift);
            assertThat(saved.getPaymentMethodId()).isEqualTo(paymentMethodId);
            assertThat(saved.getDifferenceAmount()).isEqualByComparingTo(PaymentClosingFixture.DIFFERENCE_AMOUNT);
            assertThat(response.getShiftId()).isEqualTo(shift.getId());
            assertThat(response.getExpectedAmount()).isEqualTo(PaymentClosingFixture.EXPECTED_AMOUNT);
            assertThat(response.getDeclaredAmount()).isEqualTo(PaymentClosingFixture.DECLARED_AMOUNT);
            assertThat(response.getEvidenceReference()).isEqualTo(PaymentClosingFixture.EVIDENCE_REFERENCE);
        }

        @Test
        void shouldUpdateExistingClosingOfSamePaymentMethod() {
            givenOpenShift();
            PaymentClosing existing = PaymentClosingFixture.persistedEntity(shift, paymentMethodId);
            PaymentClosing otherMethod = PaymentClosingFixture.persistedEntity(shift, UUID.randomUUID());
            when(paymentClosingRepository.findByShiftIdOrderByPaymentMethodId(shift.getId())).thenReturn(List.of(otherMethod, existing));
            when(paymentClosingRepository.saveAndFlush(existing)).thenReturn(existing);
            input.setDeclaredAmount(UPDATED_DECLARED_AMOUNT);

            PaymentClosingResponseDTO response = service.registerPaymentClosing(shift.getId(), input);

            PaymentClosing saved = captureSavedClosing();
            assertThat(saved).isSameAs(existing);
            assertThat(saved.getDeclaredAmount()).isEqualTo(UPDATED_DECLARED_AMOUNT);
            assertThat(saved.getDifferenceAmount()).isEqualByComparingTo(UPDATED_DIFFERENCE);
            assertThat(response.getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldValidateShiftIsOpen() {
            givenOpenShift();
            when(paymentClosingRepository.findByShiftIdOrderByPaymentMethodId(shift.getId())).thenReturn(List.of());
            when(paymentClosingRepository.saveAndFlush(any(PaymentClosing.class))).thenAnswer(invocation -> invocation.getArgument(0));

            service.registerPaymentClosing(shift.getId(), input);

            verify(validationSupport).ensureShiftIsOpen(shift);
        }
    }

    @Nested
    class Validation {

        @Test
        void shouldThrowBusinessExceptionWhenPayloadIsNull() {
            assertThatThrownBy(() -> service.registerPaymentClosing(shift.getId(), null))
                    .isInstanceOf(BusinessException.class);
            verifyNoInteractions(validationSupport, paymentClosingRepository);
        }

        @Test
        void shouldPropagateNotFoundWhenShiftDoesNotExist() {
            when(validationSupport.getRequiredShift(shift.getId())).thenThrow(new NotFoundBusinessException("turno"));

            assertThatThrownBy(() -> service.registerPaymentClosing(shift.getId(), input))
                    .isInstanceOf(NotFoundBusinessException.class);
            verifyNoInteractions(paymentClosingRepository);
        }

        @Test
        void shouldPropagateBusinessExceptionWhenShiftIsClosed() {
            givenOpenShift();
            doThrow(new BusinessException("fechado")).when(validationSupport).ensureShiftIsOpen(shift);

            assertThatThrownBy(() -> service.registerPaymentClosing(shift.getId(), input))
                    .isInstanceOf(BusinessException.class);
            verifyNoInteractions(paymentClosingRepository);
        }

        @Test
        void shouldThrowBusinessExceptionWhenExpectedAmountIsNull() {
            givenOpenShift();
            input.setExpectedAmount(null);

            assertThatThrownBy(() -> service.registerPaymentClosing(shift.getId(), input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("valor esperado");
            verifyNoInteractions(paymentClosingRepository);
        }

        @Test
        void shouldThrowBusinessExceptionWhenExpectedAmountIsNegative() {
            givenOpenShift();
            input.setExpectedAmount(PaymentClosingFixture.NEGATIVE_AMOUNT);

            assertThatThrownBy(() -> service.registerPaymentClosing(shift.getId(), input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("valor esperado");
            verifyNoInteractions(paymentClosingRepository);
        }

        @Test
        void shouldThrowBusinessExceptionWhenDeclaredAmountIsNull() {
            givenOpenShift();
            input.setDeclaredAmount(null);

            assertThatThrownBy(() -> service.registerPaymentClosing(shift.getId(), input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("valor declarado");
            verifyNoInteractions(paymentClosingRepository);
        }

        @Test
        void shouldThrowBusinessExceptionWhenDeclaredAmountIsNegative() {
            givenOpenShift();
            input.setDeclaredAmount(PaymentClosingFixture.NEGATIVE_AMOUNT);

            assertThatThrownBy(() -> service.registerPaymentClosing(shift.getId(), input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("valor declarado");
            verifyNoInteractions(paymentClosingRepository);
        }
    }
}
