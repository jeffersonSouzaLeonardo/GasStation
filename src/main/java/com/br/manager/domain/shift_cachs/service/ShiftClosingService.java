package com.br.manager.domain.shift_cachs.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.shift_cachs.dto.ShiftCloseRequestDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftResponseDTO;
import com.br.manager.domain.shift_cachs.entity.NozzleReading;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.NozzleReadingTypeEnum;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import com.br.manager.domain.shift_cachs.mapper.ShiftMapper;
import com.br.manager.domain.shift_cachs.repository.NozzleReadingRepository;
import com.br.manager.domain.shift_cachs.repository.ShiftRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ShiftClosingService {

    @Autowired
    private ShiftValidationSupport validationSupport;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private NozzleReadingRepository nozzleReadingRepository;

    @Autowired
    private ShiftMapper shiftMapper;

    @Transactional
    public ShiftResponseDTO closeShift(UUID shiftId, ShiftCloseRequestDTO request) {
        if (request == null || request.getClosedBy() == null) {
            throw new BusinessException("O usuário de fechamento é obrigatório.");
        }

        Shift shift = validationSupport.getRequiredShift(shiftId);
        if (shift.getStatus() == ShiftStatusEnum.CLOSED) {
            throw new BusinessException(String.format("O turno %s já está fechado.", shiftId));
        }

        for (var nozzle : validationSupport.findNozzlesByStationId(shift.getStationId())) {
            boolean hasClosingReading = nozzleReadingRepository.existsByShiftIdAndNozzleIdAndReadingType(
                    shiftId, nozzle.getId(), NozzleReadingTypeEnum.CLOSING);
            if (!hasClosingReading) {
                throw new BusinessException(String.format(
                        "A leitura de fechamento é obrigatória para a bomba %s antes de encerrar o turno.", nozzle.getId()));
            }
        }

        User closingUser = validationSupport.getRequiredUser(request.getClosedBy(), "closedBy");

        shift.setStatus(ShiftStatusEnum.CLOSED);
        shift.setClosedAt(LocalDateTime.now());
        shift.setClosedBy(request.getClosedBy());

        Shift savedShift = shiftRepository.saveAndFlush(shift);
        return shiftMapper.toResponse(savedShift);
    }
}
