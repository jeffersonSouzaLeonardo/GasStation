package com.br.manager.domain.shift_cachs.service;

import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.shift_cachs.dto.ShiftSummaryResponseDTO;
import com.br.manager.domain.shift_cachs.entity.CashMovement;
import com.br.manager.domain.shift_cachs.entity.NozzleReading;
import com.br.manager.domain.shift_cachs.entity.PaymentClosing;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.CashMovementTypeEnum;
import com.br.manager.domain.shift_cachs.enums.NozzleReadingTypeEnum;
import com.br.manager.domain.shift_cachs.fixture.ShiftFixture;
import com.br.manager.domain.shift_cachs.mapper.CashMovementMapper;
import com.br.manager.domain.shift_cachs.mapper.PaymentClosingMapper;
import com.br.manager.domain.shift_cachs.mapper.ShiftMapper;
import com.br.manager.domain.shift_cachs.repository.CashMovementRepository;
import com.br.manager.domain.shift_cachs.repository.NozzleReadingRepository;
import com.br.manager.domain.shift_cachs.repository.PaymentClosingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShiftSummaryServiceTest {

    private static final BigDecimal FIRST_SALE = new BigDecimal("100.00");
    private static final BigDecimal SECOND_SALE = new BigDecimal("50.50");
    private static final BigDecimal WITHDRAWAL = new BigDecimal("30.00");
    private static final BigDecimal TEST_READING = new BigDecimal("2.00");
    private static final BigDecimal CALIBRATION_READING = new BigDecimal("3.00");
    private static final BigDecimal OPENING_READING = new BigDecimal("999.00");
    private static final BigDecimal SHORTAGE = new BigDecimal("-5.00");
    private static final BigDecimal SURPLUS = new BigDecimal("1.25");

    @Mock
    private ShiftValidationSupport validationSupport;

    @Mock
    private CashMovementRepository cashMovementRepository;

    @Mock
    private PaymentClosingRepository paymentClosingRepository;

    @Mock
    private NozzleReadingRepository nozzleReadingRepository;

    @Spy
    private ShiftMapper shiftMapper = new ShiftMapper();

    @Spy
    private CashMovementMapper cashMovementMapper = new CashMovementMapper();

    @Spy
    private PaymentClosingMapper paymentClosingMapper = new PaymentClosingMapper();

    @InjectMocks
    private ShiftSummaryService shiftSummaryService;

    @Test
    void shouldReturnZeroTotalsWhenShiftHasNoActivity() {
        Shift shift = ShiftFixture.openShift();
        when(validationSupport.getRequiredShift(shift.getId())).thenReturn(shift);

        ShiftSummaryResponseDTO summary = shiftSummaryService.getShiftSummary(shift.getId());

        assertThat(summary.getShift().getId()).isEqualTo(shift.getId());
        assertThat(summary.getGrossSales()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.getCalibrationAndTestAdjustment()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.getNetSales()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.getOpeningCash()).isEqualByComparingTo(ShiftFixture.OPENING_CASH);
        assertThat(summary.getClosingCash()).isEqualByComparingTo(ShiftFixture.OPENING_CASH);
        assertThat(summary.getTotalDifferenceAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.getCashMovements()).isEmpty();
        assertThat(summary.getPaymentClosings()).isEmpty();
    }

    @Test
    void shouldSumOnlySaleMovementsAsGrossSales() {
        Shift shift = givenShiftWithMovements();

        ShiftSummaryResponseDTO summary = shiftSummaryService.getShiftSummary(shift.getId());

        assertThat(summary.getGrossSales()).isEqualByComparingTo(FIRST_SALE.add(SECOND_SALE));
        assertThat(summary.getCashMovements()).hasSize(3);
    }

    @Test
    void shouldAddGrossSalesToOpeningCashAsClosingCash() {
        Shift shift = givenShiftWithMovements();

        ShiftSummaryResponseDTO summary = shiftSummaryService.getShiftSummary(shift.getId());

        assertThat(summary.getClosingCash())
                .isEqualByComparingTo(ShiftFixture.OPENING_CASH.add(FIRST_SALE).add(SECOND_SALE));
    }

    @Test
    void shouldSubtractTestAndCalibrationReadingsFromGrossSales() {
        Shift shift = givenShiftWithMovements();
        when(nozzleReadingRepository.findByShiftId(shift.getId())).thenReturn(List.of(
                reading(NozzleReadingTypeEnum.TEST, TEST_READING),
                reading(NozzleReadingTypeEnum.CALIBRATION, CALIBRATION_READING),
                reading(NozzleReadingTypeEnum.OPENING, OPENING_READING)));

        ShiftSummaryResponseDTO summary = shiftSummaryService.getShiftSummary(shift.getId());

        BigDecimal adjustment = TEST_READING.add(CALIBRATION_READING);
        assertThat(summary.getCalibrationAndTestAdjustment()).isEqualByComparingTo(adjustment);
        assertThat(summary.getNetSales()).isEqualByComparingTo(FIRST_SALE.add(SECOND_SALE).subtract(adjustment));
    }

    @Test
    void shouldSumPaymentClosingDifferences() {
        Shift shift = ShiftFixture.openShift();
        when(validationSupport.getRequiredShift(shift.getId())).thenReturn(shift);
        when(paymentClosingRepository.findByShiftIdOrderByPaymentMethodId(shift.getId()))
                .thenReturn(List.of(closing(shift, SHORTAGE), closing(shift, SURPLUS)));

        ShiftSummaryResponseDTO summary = shiftSummaryService.getShiftSummary(shift.getId());

        assertThat(summary.getTotalDifferenceAmount()).isEqualByComparingTo(SHORTAGE.add(SURPLUS));
        assertThat(summary.getPaymentClosings()).hasSize(2);
        assertThat(summary.getPaymentClosings().get(0).getShiftId()).isEqualTo(shift.getId());
    }

    @Test
    void shouldPropagateNotFoundWhenShiftIsUnknown() {
        UUID shiftId = UUID.randomUUID();
        when(validationSupport.getRequiredShift(shiftId)).thenThrow(new NotFoundBusinessException("turno"));

        assertThatThrownBy(() -> shiftSummaryService.getShiftSummary(shiftId))
                .isInstanceOf(NotFoundBusinessException.class);
        verifyNoInteractions(cashMovementRepository, paymentClosingRepository, nozzleReadingRepository);
    }

    private Shift givenShiftWithMovements() {
        Shift shift = ShiftFixture.openShift();
        when(validationSupport.getRequiredShift(shift.getId())).thenReturn(shift);
        when(cashMovementRepository.findByShiftIdOrderByOccurredAtAsc(shift.getId())).thenReturn(List.of(
                movement(shift, CashMovementTypeEnum.SALE, FIRST_SALE),
                movement(shift, CashMovementTypeEnum.WITHDRAWAL, WITHDRAWAL),
                movement(shift, CashMovementTypeEnum.SALE, SECOND_SALE)));
        return shift;
    }

    private static CashMovement movement(Shift shift, CashMovementTypeEnum type, BigDecimal amount) {
        CashMovement movement = new CashMovement();
        movement.setId(UUID.randomUUID());
        movement.setShift(shift);
        movement.setMovementType(type);
        movement.setAmount(amount);
        return movement;
    }

    private static NozzleReading reading(NozzleReadingTypeEnum type, BigDecimal meter) {
        NozzleReading reading = new NozzleReading();
        reading.setId(UUID.randomUUID());
        reading.setReadingType(type);
        reading.setMeterReading(meter);
        return reading;
    }

    private static PaymentClosing closing(Shift shift, BigDecimal difference) {
        PaymentClosing closing = new PaymentClosing();
        closing.setId(UUID.randomUUID());
        closing.setShift(shift);
        closing.setPaymentMethodId(UUID.randomUUID());
        closing.setDifferenceAmount(difference);
        return closing;
    }
}
