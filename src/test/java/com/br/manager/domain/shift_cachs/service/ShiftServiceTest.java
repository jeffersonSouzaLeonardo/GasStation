package com.br.manager.domain.shift_cachs.service;

import com.br.manager.domain.shift_cachs.dto.CashMovementInputDTO;
import com.br.manager.domain.shift_cachs.dto.CashMovementResponseDTO;
import com.br.manager.domain.shift_cachs.dto.NozzleReadingInputDTO;
import com.br.manager.domain.shift_cachs.dto.NozzleReadingResponseDTO;
import com.br.manager.domain.shift_cachs.dto.PaymentClosingInputDTO;
import com.br.manager.domain.shift_cachs.dto.PaymentClosingResponseDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftAllocationInputDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftAllocationResponseDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftCloseRequestDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftOpenRequestDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftResponseDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftSummaryResponseDTO;
import com.br.manager.domain.shift_cachs.fixture.ShiftAllocationFixture;
import com.br.manager.domain.shift_cachs.fixture.ShiftFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShiftServiceTest {

    private static final UUID SHIFT_ID = UUID.randomUUID();

    @Mock
    private ShiftOpeningService shiftOpeningService;

    @Mock
    private ShiftAllocationService shiftAllocationService;

    @Mock
    private ShiftNozzleReadingService shiftNozzleReadingService;

    @Mock
    private ShiftCashMovementService shiftCashMovementService;

    @Mock
    private ShiftPaymentClosingService shiftPaymentClosingService;

    @Mock
    private ShiftClosingService shiftClosingService;

    @Mock
    private ShiftSummaryService shiftSummaryService;

    @InjectMocks
    private ShiftService shiftService;

    @Test
    void shouldDelegateOpenShiftToOpeningService() {
        ShiftOpenRequestDTO request = ShiftFixture.openRequest();
        ShiftResponseDTO expected = new ShiftResponseDTO();
        when(shiftOpeningService.openShift(request)).thenReturn(expected);

        assertThat(shiftService.openShift(request)).isSameAs(expected);
    }

    @Test
    void shouldDelegateAllocationToAllocationService() {
        ShiftAllocationInputDTO request = ShiftAllocationFixture.validInput();
        ShiftAllocationResponseDTO expected = new ShiftAllocationResponseDTO();
        when(shiftAllocationService.addOrUpdateAllocation(SHIFT_ID, request)).thenReturn(expected);

        assertThat(shiftService.addOrUpdateAllocation(SHIFT_ID, request)).isSameAs(expected);
    }

    @Test
    void shouldDelegateNozzleReadingToNozzleReadingService() {
        NozzleReadingInputDTO request = new NozzleReadingInputDTO();
        NozzleReadingResponseDTO expected = new NozzleReadingResponseDTO();
        when(shiftNozzleReadingService.registerNozzleReading(SHIFT_ID, request)).thenReturn(expected);

        assertThat(shiftService.registerNozzleReading(SHIFT_ID, request)).isSameAs(expected);
    }

    @Test
    void shouldDelegateCashMovementToCashMovementService() {
        CashMovementInputDTO request = new CashMovementInputDTO();
        CashMovementResponseDTO expected = new CashMovementResponseDTO();
        when(shiftCashMovementService.registerCashMovement(SHIFT_ID, request)).thenReturn(expected);

        assertThat(shiftService.registerCashMovement(SHIFT_ID, request)).isSameAs(expected);
    }

    @Test
    void shouldDelegatePaymentClosingToPaymentClosingService() {
        PaymentClosingInputDTO request = new PaymentClosingInputDTO();
        PaymentClosingResponseDTO expected = new PaymentClosingResponseDTO();
        when(shiftPaymentClosingService.registerPaymentClosing(SHIFT_ID, request)).thenReturn(expected);

        assertThat(shiftService.registerPaymentClosing(SHIFT_ID, request)).isSameAs(expected);
    }

    @Test
    void shouldDelegateCloseShiftToClosingService() {
        ShiftCloseRequestDTO request = ShiftFixture.closeRequest(UUID.randomUUID());
        ShiftResponseDTO expected = new ShiftResponseDTO();
        when(shiftClosingService.closeShift(SHIFT_ID, request)).thenReturn(expected);

        assertThat(shiftService.closeShift(SHIFT_ID, request)).isSameAs(expected);
    }

    @Test
    void shouldDelegateSummaryToSummaryService() {
        ShiftSummaryResponseDTO expected = new ShiftSummaryResponseDTO();
        when(shiftSummaryService.getShiftSummary(SHIFT_ID)).thenReturn(expected);

        assertThat(shiftService.getShiftSummary(SHIFT_ID)).isSameAs(expected);
    }
}
