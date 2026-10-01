package com.br.manager.domain.sale_receipt.dto;

import com.br.manager.domain.sale_receipt.enums.FiscalStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class SaleInputDTO {
    private UUID id;
    private UUID stationId;
    private UUID shiftId;
    private String saleNumber;
    private Instant soldAt;
    private UUID customerId;
    private UUID vehicleId;
    private UUID attendantUserId;
    private UUID cashierUserId;
    private BigDecimal discountAmount;
    private FiscalStatus fiscalStatus;
    private String notes;
    private List<SaleItemInputDTO> items;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getStationId() { return stationId; }
    public void setStationId(UUID stationId) { this.stationId = stationId; }
    public UUID getShiftId() { return shiftId; }
    public void setShiftId(UUID shiftId) { this.shiftId = shiftId; }
    public String getSaleNumber() { return saleNumber; }
    public void setSaleNumber(String saleNumber) { this.saleNumber = saleNumber; }
    public Instant getSoldAt() { return soldAt; }
    public void setSoldAt(Instant soldAt) { this.soldAt = soldAt; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public UUID getVehicleId() { return vehicleId; }
    public void setVehicleId(UUID vehicleId) { this.vehicleId = vehicleId; }
    public UUID getAttendantUserId() { return attendantUserId; }
    public void setAttendantUserId(UUID attendantUserId) { this.attendantUserId = attendantUserId; }
    public UUID getCashierUserId() { return cashierUserId; }
    public void setCashierUserId(UUID cashierUserId) { this.cashierUserId = cashierUserId; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public FiscalStatus getFiscalStatus() { return fiscalStatus; }
    public void setFiscalStatus(FiscalStatus fiscalStatus) { this.fiscalStatus = fiscalStatus; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<SaleItemInputDTO> getItems() { return items; }
    public void setItems(List<SaleItemInputDTO> items) { this.items = items; }
}
