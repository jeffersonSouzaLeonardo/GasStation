package com.br.manager.domain.shift_cachs.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.shift_cachs.dto.CashSessionCloseInputDTO;
import com.br.manager.domain.shift_cachs.dto.CashSessionInputDTO;
import com.br.manager.domain.shift_cachs.dto.CashSessionResponseDTO;
import com.br.manager.domain.shift_cachs.dto.CashSessionSyncDTO;
import com.br.manager.domain.shift_cachs.entity.CashSession;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.CashSessionStatusEnum;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import com.br.manager.domain.shift_cachs.mapper.CashSessionMapper;
import com.br.manager.domain.shift_cachs.repository.CashSessionRepository;
import com.br.manager.domain.shift_cachs.repository.ShiftRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CashSessionService {

    @Autowired
    private CashSessionRepository cashSessionRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private CashSessionMapper cashSessionMapper;

    @Autowired
    private ShiftValidationSupport validationSupport;

    public CashSessionResponseDTO open(CashSessionInputDTO request) {
        if (request == null) {
            throw new BusinessException("O payload de abertura do caixa é obrigatório.");
        }

        Shift shift = shiftRepository.findByIdAndStatus(request.getShiftId(), ShiftStatusEnum.OPEN)
                .orElseThrow(() -> new BusinessException(String.format(
                        "Não existe turno ABERTO com o id %s.", request.getShiftId())));

        validationSupport.getRequiredUser(request.getCashierUserId(), "cashierUserId");

        if (cashSessionRepository.existsByCashierUserIdAndStatus(
                request.getCashierUserId(), CashSessionStatusEnum.OPEN)) {
            throw new BusinessException(String.format(
                    "O usuário %s já possui uma sessão de caixa ABERTA.", request.getCashierUserId()));
        }

        CashSession session = cashSessionMapper.toEntity(request, shift);
        session.setId(UUID.randomUUID());
        session.setStatus(CashSessionStatusEnum.OPEN);
        session.setOpenedAt(LocalDateTime.now());

        return cashSessionMapper.toResponse(cashSessionRepository.saveAndFlush(session));
    }

    public CashSessionResponseDTO close(UUID id, CashSessionCloseInputDTO request) {
        CashSession session = cashSessionRepository.findByIdAndStatus(id, CashSessionStatusEnum.OPEN)
                .orElseThrow(() -> new NotFoundBusinessException(String.format(
                        "Sessão de caixa ABERTA não encontrada com o id %s.", id)));

        if (request != null) {
            session.setTotalCash(request.getTotalCash());
            session.setTotalCard(request.getTotalCard());
            session.setTotalPix(request.getTotalPix());
            session.setTotalWithdrawal(request.getTotalWithdrawal());
            if (request.getNotes() != null) {
                session.setNotes(request.getNotes());
            }
        }

        session.setStatus(CashSessionStatusEnum.CLOSED);
        session.setClosedAt(LocalDateTime.now());

        return cashSessionMapper.toResponse(cashSessionRepository.saveAndFlush(session));
    }

    public CashSessionResponseDTO findById(UUID id) {
        CashSession session = cashSessionRepository.findById(id)
                .orElseThrow(() -> new NotFoundBusinessException(String.format(
                        "Sessão de caixa não encontrada com o id %s.", id)));
        return cashSessionMapper.toResponse(session);
    }

    public List<CashSessionResponseDTO> findByShift(UUID shiftId) {
        return cashSessionMapper.toResponseList(
                cashSessionRepository.findByShift_IdOrderByOpenedAt(shiftId));
    }

    /**
     * Persiste as sessões de caixa recebidas do PDV. A operação é idempotente:
     * reenvios do mesmo lote apenas atualizam os registros já existentes.
     */
    public void saveAll(List<CashSessionSyncDTO> sessions) {
        if (sessions == null || sessions.isEmpty()) {
            return;
        }
        try {
            for (CashSessionSyncDTO dto : sessions) {
                if (dto == null) {
                    continue;
                }
                CashSession existing = cashSessionRepository.findById(dto.getId()).orElse(null);
                if (existing != null) {
                    cashSessionMapper.updateFromSync(dto, existing);
                    cashSessionRepository.saveAndFlush(existing);
                } else {
                    Shift shift = shiftRepository.findById(dto.getShiftId())
                            .orElseThrow(() -> new BusinessException(String.format(
                                    "Turno %s da sessão de caixa %s não encontrado. Sincronize os turnos antes das sessões.",
                                    dto.getShiftId(), dto.getId())));
                    cashSessionRepository.saveAndFlush(cashSessionMapper.toEntity(dto, shift));
                }
            }
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        }
    }
}
