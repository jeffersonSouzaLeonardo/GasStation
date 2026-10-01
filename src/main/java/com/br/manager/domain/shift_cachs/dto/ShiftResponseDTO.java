package com.br.manager.domain.shift_cachs.dto;

import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ShiftResponseDTO {

    private UUID id;
    private UUID stationId;
    private UUID cashierUserId;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private BigDecimal openingCash;
    private ShiftStatusEnum status;
    private UUID openedBy;
    private UUID closedBy;
    private String notes;
    private List<ShiftAllocationResponseDTO> allocations;
    private List<NozzleReadingResponseDTO> nozzleReadings;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public BigDecimal getOpeningCash() {
        return openingCash;
    }

    public void setOpeningCash(BigDecimal openingCash) {
        this.openingCash = openingCash;
    }

    public ShiftStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ShiftStatusEnum status) {
        this.status = status;
    }

    public UUID getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(UUID openedBy) {
        this.openedBy = openedBy;
    }

    public UUID getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(UUID closedBy) {
        this.closedBy = closedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<ShiftAllocationResponseDTO> getAllocations() {
        return allocations;
    }

    public void setAllocations(List<ShiftAllocationResponseDTO> allocations) {
        this.allocations = allocations;
    }

    public List<NozzleReadingResponseDTO> getNozzleReadings() {
        return nozzleReadings;
    }

    public void setNozzleReadings(List<NozzleReadingResponseDTO> nozzleReadings) {
        this.nozzleReadings = nozzleReadings;
    }
}
