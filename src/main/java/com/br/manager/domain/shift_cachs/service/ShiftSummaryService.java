package com.br.manager.domain.shift_cachs.service;

import com.br.manager.domain.shift_cachs.dto.ShiftSummaryResponseDTO;
import com.br.manager.domain.shift_cachs.entity.CashMovement;
import com.br.manager.domain.shift_cachs.entity.NozzleReading;
import com.br.manager.domain.shift_cachs.entity.PaymentClosing;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.CashMovementTypeEnum;
import com.br.manager.domain.shift_cachs.enums.NozzleReadingTypeEnum;
import com.br.manager.domain.shift_cachs.mapper.CashMovementMapper;
import com.br.manager.domain.shift_cachs.mapper.NozzleReadingMapper;
import com.br.manager.domain.shift_cachs.mapper.PaymentClosingMapper;
import com.br.manager.domain.shift_cachs.mapper.ShiftMapper;
import com.br.manager.domain.shift_cachs.repository.CashMovementRepository;
import com.br.manager.domain.shift_cachs.repository.NozzleReadingRepository;
import com.br.manager.domain.shift_cachs.repository.PaymentClosingRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ShiftSummaryService {

    @Autowired
    private ShiftValidationSupport validationSupport;

    @Autowired
    private CashMovementRepository cashMovementRepository;

    @Autowired
    private PaymentClosingRepository paymentClosingRepository;

    @Autowired
    private NozzleReadingRepository nozzleReadingRepository;

    @Autowired
    private ShiftMapper shiftMapper;

    @Autowired
    private CashMovementMapper cashMovementMapper;

    @Autowired
    private PaymentClosingMapper paymentClosingMapper;

    @Transactional
    public ShiftSummaryResponseDTO getShiftSummary(UUID shiftId) {
        Shift shift = validationSupport.getRequiredShift(shiftId);

        List<CashMovement> movements = cashMovementRepository.findByShiftIdOrderByOccurredAtAsc(shiftId);
        List<PaymentClosing> closings = paymentClosingRepository.findByShiftIdOrderByPaymentMethodId(shiftId);
        List<NozzleReading> readings = nozzleReadingRepository.findByShiftId(shiftId);

        BigDecimal grossSales = movements.stream()
                .filter(item -> item.getMovementType() == CashMovementTypeEnum.SALE)
                .map(CashMovement::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal calibrationAndTestAdjustment = readings.stream()
                .filter(item -> item.getReadingType() == NozzleReadingTypeEnum.TEST || item.getReadingType() == NozzleReadingTypeEnum.CALIBRATION)
                .map(NozzleReading::getMeterReading)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netSales = grossSales.subtract(calibrationAndTestAdjustment);
        BigDecimal totalDifferenceAmount = closings.stream()
                .map(PaymentClosing::getDifferenceAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ShiftSummaryResponseDTO summary = new ShiftSummaryResponseDTO();
        summary.setShift(shiftMapper.toResponse(shift));
        summary.setGrossSales(grossSales);
        summary.setCalibrationAndTestAdjustment(calibrationAndTestAdjustment);
        summary.setNetSales(netSales);
        summary.setOpeningCash(shift.getOpeningCash());
        summary.setClosingCash(shift.getOpeningCash().add(grossSales));
        summary.setTotalDifferenceAmount(totalDifferenceAmount);
        summary.setCashMovements(movements.stream().map(cashMovementMapper::toResponse).toList());
        summary.setPaymentClosings(closings.stream().map(paymentClosingMapper::toResponse).toList());
        return summary;
    }
}
