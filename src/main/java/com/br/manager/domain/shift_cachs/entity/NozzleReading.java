package com.br.manager.domain.shift_cachs.entity;

import com.br.manager.domain.shift_cachs.enums.NozzleReadingTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "nozzle_readings")
public class NozzleReading {

    @Id
@Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "O turno é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @NotNull(message = "O ID da bomba é obrigatório.")
    @Column(name = "nozzle_id", nullable = false)
    private UUID nozzleId;

    @NotNull(message = "O tipo de leitura é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "reading_type", nullable = false, length = 20)
    private NozzleReadingTypeEnum readingType;

    @NotNull(message = "A leitura do medidor é obrigatória.")
    @DecimalMin(value = "0.000", message = "A leitura do medidor não pode ser negativa.")
    @Column(name = "meter_reading", nullable = false, precision = 18, scale = 3)
    private BigDecimal meterReading;

    @NotNull(message = "A data da leitura é obrigatória.")
    @Column(name = "read_at", nullable = false)
    private LocalDateTime readAt;

    @NotNull(message = "O usuário registrador é obrigatório.")
    @Column(name = "recorded_by", nullable = false)
    private UUID recordedBy;

    @Column(name = "notes", length = 500)
    private String notes;
public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
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
