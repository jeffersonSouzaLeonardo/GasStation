package com.br.manager.domain.shift_cachs.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.shift_cachs.dto.NozzleReadingInputDTO;
import com.br.manager.domain.shift_cachs.dto.NozzleReadingResponseDTO;
import com.br.manager.domain.shift_cachs.entity.NozzleReading;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.NozzleReadingTypeEnum;
import com.br.manager.domain.shift_cachs.mapper.NozzleReadingMapper;
import com.br.manager.domain.shift_cachs.repository.NozzleReadingRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ShiftNozzleReadingService {

    @Autowired
    private ShiftValidationSupport validationSupport;

    @Autowired
    private NozzleReadingRepository nozzleReadingRepository;

    @Autowired
    private NozzleReadingMapper nozzleReadingMapper;

    @Transactional
    public NozzleReadingResponseDTO registerNozzleReading(UUID shiftId, NozzleReadingInputDTO request) {
        if (request == null) {
            throw new BusinessException("O payload da leitura da bomba é obrigatório.");
        }

        Shift shift = validationSupport.getRequiredShift(shiftId);
        validationSupport.ensureShiftIsOpen(shift);
        validationSupport.validateNozzleBelongsToShiftStation(shift.getStationId(), request.getNozzleId());

        if (request.getReadingType() == NozzleReadingTypeEnum.OPENING &&
                nozzleReadingRepository.existsByShiftIdAndNozzleIdAndReadingType(shiftId, request.getNozzleId(), NozzleReadingTypeEnum.OPENING)) {
            throw new BusinessException(String.format("Já existe uma leitura de abertura para a bomba %s neste turno.", request.getNozzleId()));
        }

        if (request.getReadingType() == NozzleReadingTypeEnum.CLOSING &&
                nozzleReadingRepository.existsByShiftIdAndNozzleIdAndReadingType(shiftId, request.getNozzleId(), NozzleReadingTypeEnum.CLOSING)) {
            throw new BusinessException(String.format("Já existe uma leitura de fechamento para a bomba %s neste turno.", request.getNozzleId()));
        }

        NozzleReading reading = nozzleReadingMapper.toEntity(request, shift);
        NozzleReading savedReading = nozzleReadingRepository.saveAndFlush(reading);
        return nozzleReadingMapper.toResponse(savedReading);
    }
}
