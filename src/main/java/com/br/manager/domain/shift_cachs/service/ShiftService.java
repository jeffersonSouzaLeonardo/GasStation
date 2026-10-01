package com.br.manager.domain.shift_cachs.service;

import com.br.manager.domain.shift_cachs.dto.*;
import com.br.manager.domain.shift_cachs.dto.ShiftAllocationResponseDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftCloseRequestDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftOpenRequestDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ShiftService {

    @Autowired
    private ShiftOpeningService shiftOpeningService;

    @Autowired
    private ShiftAllocationService shiftAllocationService;

    @Autowired
    private ShiftNozzleReadingService shiftNozzleReadingService;

    @Autowired
    private ShiftCashMovementService shiftCashMovementService;

    @Autowired
    private ShiftPaymentClosingService shiftPaymentClosingService;

    @Autowired
    private ShiftClosingService shiftClosingService;

    @Autowired
    private ShiftSummaryService shiftSummaryService;

    public ShiftResponseDTO openShift(ShiftOpenRequestDTO request) {
        return shiftOpeningService.openShift(request);
    }

    public ShiftAllocationResponseDTO addOrUpdateAllocation(UUID shiftId, ShiftAllocationInputDTO request) {
        return shiftAllocationService.addOrUpdateAllocation(shiftId, request);
    }

    public NozzleReadingResponseDTO registerNozzleReading(UUID shiftId, NozzleReadingInputDTO request) {
        return shiftNozzleReadingService.registerNozzleReading(shiftId, request);
    }

    public CashMovementResponseDTO registerCashMovement(UUID shiftId, CashMovementInputDTO request) {
        return shiftCashMovementService.registerCashMovement(shiftId, request);
    }

    public PaymentClosingResponseDTO registerPaymentClosing(UUID shiftId, PaymentClosingInputDTO request) {
        return shiftPaymentClosingService.registerPaymentClosing(shiftId, request);
    }

    public ShiftResponseDTO closeShift(UUID shiftId, ShiftCloseRequestDTO request) {
        return shiftClosingService.closeShift(shiftId, request);
    }

    public ShiftSummaryResponseDTO getShiftSummary(UUID shiftId) {
        return shiftSummaryService.getShiftSummary(shiftId);
    }
}
