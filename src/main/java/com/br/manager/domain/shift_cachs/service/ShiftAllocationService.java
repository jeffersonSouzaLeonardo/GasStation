package com.br.manager.domain.shift_cachs.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.shift_cachs.dto.ShiftAllocationInputDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftAllocationResponseDTO;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.entity.ShiftAllocation;
import com.br.manager.domain.shift_cachs.mapper.ShiftAllocationMapper;
import com.br.manager.domain.shift_cachs.repository.ShiftAllocationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ShiftAllocationService {

    @Autowired
    private ShiftValidationSupport validationSupport;

    @Autowired
    private ShiftAllocationRepository allocationRepository;

    @Autowired
    private ShiftAllocationMapper allocationMapper;

    @Transactional
    public ShiftAllocationResponseDTO addOrUpdateAllocation(UUID shiftId, ShiftAllocationInputDTO request) {
        if (request == null) {
            throw new BusinessException("O payload da alocação é obrigatório.");
        }

        Shift shift = validationSupport.getRequiredShift(shiftId);
        validationSupport.ensureShiftIsOpen(shift);

        User allocationUser = validationSupport.getRequiredUser(request.getUserId(), "allocationUserId");

        ShiftAllocation existing = allocationRepository.findByShiftIdAndUserId(shiftId, request.getUserId()).orElse(null);
        if (existing != null) {
            existing.setRole(request.getRole());
            return allocationMapper.toResponse(allocationRepository.saveAndFlush(existing));
        }

        return allocationMapper.toResponse(allocationRepository.saveAndFlush(allocationMapper.toEntity(request, shift)));
    }
}
