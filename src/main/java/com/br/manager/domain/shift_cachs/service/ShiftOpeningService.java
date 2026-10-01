package com.br.manager.domain.shift_cachs.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.operational.entity.Nozzle;
import com.br.manager.domain.operational.repository.NozzleRepository;
import com.br.manager.domain.organization.entity.Station;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.shift_cachs.dto.ShiftAllocationInputDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftOpenRequestDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftResponseDTO;
import com.br.manager.domain.shift_cachs.entity.NozzleReading;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.entity.ShiftAllocation;
import com.br.manager.domain.shift_cachs.enums.NozzleReadingTypeEnum;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import com.br.manager.domain.shift_cachs.mapper.ShiftAllocationMapper;
import com.br.manager.domain.shift_cachs.mapper.ShiftMapper;
import com.br.manager.domain.shift_cachs.repository.NozzleReadingRepository;
import com.br.manager.domain.shift_cachs.repository.ShiftAllocationRepository;
import com.br.manager.domain.shift_cachs.repository.ShiftRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ShiftOpeningService {

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private ShiftAllocationRepository allocationRepository;

    @Autowired
    private NozzleReadingRepository nozzleReadingRepository;

    @Autowired
    private ShiftValidationSupport validationSupport;

    @Autowired
    private ShiftMapper shiftMapper;

    @Autowired
    private ShiftAllocationMapper shiftAllocationMapper;

    @Autowired
    private NozzleRepository nozzleRepository;

    @Transactional
    public ShiftResponseDTO openShift(ShiftOpenRequestDTO request) {
        if (request == null) {
            throw new BusinessException("O payload de abertura do turno é obrigatório.");
        }

        Station station = validationSupport.getRequiredStation(request.getStationId());
        User cashier = validationSupport.getRequiredUser(request.getCashierUserId(), "cashierUserId");
        User openedByUser = validationSupport.getRequiredUser(request.getOpenedBy(), "openedBy");

        if (shiftRepository.existsByStationIdAndCashierUserIdAndStatus(
                request.getStationId(), request.getCashierUserId(), ShiftStatusEnum.OPEN)) {
            throw new BusinessException(String.format(
                    "Já existe um turno ABERTO para o posto %s e o caixa %s.",
                    request.getStationId(), request.getCashierUserId()));
        }

        Shift shift = shiftMapper.toEntity(request);
        shift.setStatus(ShiftStatusEnum.OPEN);
        shift.setOpenedAt(LocalDateTime.now());

        Shift savedShift = shiftRepository.saveAndFlush(shift);

        if (request.getAllocations() != null) {
            for (ShiftAllocationInputDTO allocationDto : request.getAllocations()) {
                if (allocationDto == null) {
                    continue;
                }
                validationSupport.getRequiredUser(allocationDto.getUserId(), "allocationUserId");
                ShiftAllocation allocation = shiftAllocationMapper.toEntity(allocationDto, savedShift);
                allocationRepository.saveAndFlush(allocation);
            }
        }

        registerOpeningNozzleReadings(savedShift, request.getOpenedBy());
        return shiftMapper.toResponse(savedShift);
    }

    private void registerOpeningNozzleReadings(Shift shift, UUID recordedBy) {
        for (Nozzle nozzle : validationSupport.findNozzlesByStationId(shift.getStationId())) {
            NozzleReading opening = new NozzleReading();
            opening.setShift(shift);
            opening.setNozzleId(nozzle.getId());
            opening.setReadingType(NozzleReadingTypeEnum.OPENING);
            opening.setMeterReading(nozzle.getMeterNumber() == null ? BigDecimal.ZERO : nozzle.getMeterNumber());
            opening.setReadAt(LocalDateTime.now());
            opening.setRecordedBy(recordedBy);
            opening.setNotes("Leitura de abertura do turno");
            nozzleReadingRepository.saveAndFlush(opening);
        }
    }
}
