package com.br.manager.domain.shift_cachs.dto;

import com.br.manager.domain.shift_cachs.enums.NozzleReadingTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class NozzleReadingResponseDTO {

    private UUID id;
    private UUID shiftId;
    private UUID nozzleId;
    private NozzleReadingTypeEnum readingType;
    private BigDecimal meterReading;
    private LocalDateTime readAt;
    private UUID recordedBy;
    private String notes;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getShiftId() {
        return shiftId;
    }

    public void setShiftId(UUID shiftId) {
        this.shiftId = shiftId;
    }

    public UUID getNozzleId() {
        return nozzleId;
    }

    public void setNozzleId(UUID nozzleId) {
        this.nozzleId = nozzleId;
    }

    public NozzleReadingTypeEnum getReadingType() {
        return readingType;
    }

    public void setReadingType(NozzleReadingTypeEnum readingType) {
        this.readingType = readingType;
    }

    public BigDecimal getMeterReading() {
        return meterReading;
    }

    public void setMeterReading(BigDecimal meterReading) {
        this.meterReading = meterReading;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public UUID getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(UUID recordedBy) {
        this.recordedBy = recordedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
