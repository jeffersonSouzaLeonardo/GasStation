package com.br.manager.domain.shift_cachs.dto;

import com.br.manager.domain.shift_cachs.enums.NozzleReadingTypeEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class NozzleReadingInputDTO {

    @NotNull(message = "Nozzle ID is required.")
    private UUID nozzleId;

    @NotNull(message = "Reading type is required.")
    private NozzleReadingTypeEnum readingType;

    @NotNull(message = "Meter reading is required.")
    @DecimalMin(value = "0.000", message = "Meter reading cannot be negative.")
    private BigDecimal meterReading;

    @NotNull(message = "Read date is required.")
    private LocalDateTime readAt;

    @NotNull(message = "Recorded by is required.")
    private UUID recordedBy;

    private String notes;

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
