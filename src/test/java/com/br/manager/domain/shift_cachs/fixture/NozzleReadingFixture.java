package com.br.manager.domain.shift_cachs.fixture;

import com.br.manager.domain.shift_cachs.dto.NozzleReadingInputDTO;
import com.br.manager.domain.shift_cachs.entity.NozzleReading;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.NozzleReadingTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Leitura de Encerrante (bico) do turno.
 */
public final class NozzleReadingFixture {

    public static final BigDecimal METER_READING = new BigDecimal("12345.678");
    public static final LocalDateTime READ_AT = LocalDateTime.of(2026, 10, 31, 14, 0);
    public static final String NOTES = "Leitura conferida pelo supervisor";

    private NozzleReadingFixture() {
    }

    public static NozzleReadingInputDTO validInput(UUID nozzleId, UUID recordedBy, NozzleReadingTypeEnum readingType) {
        NozzleReadingInputDTO input = new NozzleReadingInputDTO();
        input.setNozzleId(nozzleId);
        input.setReadingType(readingType);
        input.setMeterReading(METER_READING);
        input.setReadAt(READ_AT);
        input.setRecordedBy(recordedBy);
        input.setNotes(NOTES);
        return input;
    }

    public static NozzleReading persistedEntity(Shift shift, UUID nozzleId, UUID recordedBy, NozzleReadingTypeEnum readingType) {
        NozzleReading reading = new NozzleReading();
        reading.setId(UUID.randomUUID());
        reading.setShift(shift);
        reading.setNozzleId(nozzleId);
        reading.setReadingType(readingType);
        reading.setMeterReading(METER_READING);
        reading.setReadAt(READ_AT);
        reading.setRecordedBy(recordedBy);
        reading.setNotes(NOTES);
        return reading;
    }
}
