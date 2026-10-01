package com.br.manager.domain.shift_cachs.dto;

import java.math.BigDecimal;
import java.util.List;

public class ShiftSummaryResponseDTO {

    private ShiftResponseDTO shift;
    private BigDecimal grossSales;
    private BigDecimal calibrationAndTestAdjustment;
    private BigDecimal netSales;
    private BigDecimal openingCash;
    private BigDecimal closingCash;
    private BigDecimal totalDifferenceAmount;
    private List<CashMovementResponseDTO> cashMovements;
    private List<PaymentClosingResponseDTO> paymentClosings;

    public ShiftResponseDTO getShift() {
        return shift;
    }

    public void setShift(ShiftResponseDTO shift) {
        this.shift = shift;
    }

    public BigDecimal getGrossSales() {
        return grossSales;
    }

    public void setGrossSales(BigDecimal grossSales) {
        this.grossSales = grossSales;
    }

    public BigDecimal getCalibrationAndTestAdjustment() {
        return calibrationAndTestAdjustment;
    }

    public void setCalibrationAndTestAdjustment(BigDecimal calibrationAndTestAdjustment) {
        this.calibrationAndTestAdjustment = calibrationAndTestAdjustment;
    }

    public BigDecimal getNetSales() {
        return netSales;
    }

    public void setNetSales(BigDecimal netSales) {
        this.netSales = netSales;
    }

    public BigDecimal getOpeningCash() {
        return openingCash;
    }

    public void setOpeningCash(BigDecimal openingCash) {
        this.openingCash = openingCash;
    }

    public BigDecimal getClosingCash() {
        return closingCash;
    }

    public void setClosingCash(BigDecimal closingCash) {
        this.closingCash = closingCash;
    }

    public BigDecimal getTotalDifferenceAmount() {
        return totalDifferenceAmount;
    }

    public void setTotalDifferenceAmount(BigDecimal totalDifferenceAmount) {
        this.totalDifferenceAmount = totalDifferenceAmount;
    }

    public List<CashMovementResponseDTO> getCashMovements() {
        return cashMovements;
    }

    public void setCashMovements(List<CashMovementResponseDTO> cashMovements) {
        this.cashMovements = cashMovements;
    }

    public List<PaymentClosingResponseDTO> getPaymentClosings() {
        return paymentClosings;
    }

    public void setPaymentClosings(List<PaymentClosingResponseDTO> paymentClosings) {
        this.paymentClosings = paymentClosings;
    }
}
