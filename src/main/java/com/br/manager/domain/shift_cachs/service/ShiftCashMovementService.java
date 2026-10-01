package com.br.manager.domain.shift_cachs.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.shift_cachs.dto.CashMovementInputDTO;
import com.br.manager.domain.shift_cachs.dto.CashMovementResponseDTO;
import com.br.manager.domain.shift_cachs.entity.CashMovement;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.mapper.CashMovementMapper;
import com.br.manager.domain.shift_cachs.repository.CashMovementRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ShiftCashMovementService {

    @Autowired
    private ShiftValidationSupport validationSupport;

    @Autowired
    private CashMovementRepository cashMovementRepository;

    @Autowired
    private CashMovementMapper cashMovementMapper;

    @Transactional
    public CashMovementResponseDTO registerCashMovement(UUID shiftId, CashMovementInputDTO request) {
        if (request == null) {
            throw new BusinessException("O payload da movimentação de caixa é obrigatório.");
        }

        Shift shift = validationSupport.getRequiredShift(shiftId);
        validationSupport.ensureShiftIsOpen(shift);

        if (request.getMovementType() == null) {
            throw new BusinessException("O tipo de movimentação é obrigatório.");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("O valor deve ser maior ou igual a zero.");
        }

        User createdBy = validationSupport.getRequiredUser(request.getCreatedBy(), "createdBy");

        CashMovement movement = cashMovementMapper.toEntity(request, shift);
        CashMovement savedMovement = cashMovementRepository.saveAndFlush(movement);
        return cashMovementMapper.toResponse(savedMovement);
    }
}
