package com.br.manager.domain.shift_cachs.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class ShiftOpenRequestDTO {

    @NotNull(message = "Station ID is required.")
    private UUID stationId;

    @NotNull(message = "Cashier user ID is required.")
    private UUID cashierUserId;

    @NotNull(message = "Opening cash is required.")
    @DecimalMin(value = "0.00", message = "Opening cash cannot be negative.")
    private BigDecimal openingCash;

    @NotNull(message = "Opened by is required.")
    private UUID openedBy;

    @Size(max = 500, message = "Notes cannot exceed 500 characters.")
    private String notes;

    private List<ShiftAllocationInputDTO> allocations;

    public UUID getStationId() {
        return stationId;
    }

    public void setStationId(UUID stationId) {
        this.stationId = stationId;
    }

    public UUID getCashierUserId() {
        return cashierUserId;
    }

    public void setCashierUserId(UUID cashierUserId) {
        this.cashierUserId = cashierUserId;
    }

    public BigDecimal getOpeningCash() {
        return openingCash;
    }

    public void setOpeningCash(BigDecimal openingCash) {
        this.openingCash = openingCash;
    }

    public UUID getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(UUID openedBy) {
        this.openedBy = openedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<ShiftAllocationInputDTO> getAllocations() {
        return allocations;
    }

    public void setAllocations(List<ShiftAllocationInputDTO> allocations) {
        this.allocations = allocations;
    }
}
