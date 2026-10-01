package com.br.manager.domain.shift_cachs.mapper;

import com.br.manager.domain.shift_cachs.dto.NozzleReadingInputDTO;
import com.br.manager.domain.shift_cachs.dto.NozzleReadingResponseDTO;
import com.br.manager.domain.shift_cachs.entity.NozzleReading;
import com.br.manager.domain.shift_cachs.entity.Shift;
import org.springframework.stereotype.Component;

@Component
public class NozzleReadingMapper {

    public NozzleReading toEntity(NozzleReadingInputDTO dto, Shift shift) {
        if (dto == null) {
            return null;
        }

        NozzleReading reading = new NozzleReading();
        reading.setShift(shift);
        reading.setNozzleId(dto.getNozzleId());
        reading.setReadingType(dto.getReadingType());
        reading.setMeterReading(dto.getMeterReading());
        reading.setReadAt(dto.getReadAt());
        reading.setRecordedBy(dto.getRecordedBy());
        reading.setNotes(dto.getNotes());
        return reading;
    }

    public NozzleReadingResponseDTO toResponse(NozzleReading reading) {
        if (reading == null) {
            return null;
        }

        NozzleReadingResponseDTO dto = new NozzleReadingResponseDTO();
        dto.setId(reading.getId());
        dto.setShiftId(reading.getShift() != null ? reading.getShift().getId() : null);
        dto.setNozzleId(reading.getNozzleId());
        dto.setReadingType(reading.getReadingType());
        dto.setMeterReading(reading.getMeterReading());
        dto.setReadAt(reading.getReadAt());
        dto.setRecordedBy(reading.getRecordedBy());
        dto.setNotes(reading.getNotes());
        return dto;
    }
}
