package com.br.manager.domain.shift_cachs.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.shift_cachs.dto.ShiftSyncDTO;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.mapper.ShiftMapper;
import com.br.manager.domain.shift_cachs.repository.ShiftRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ShiftSyncService {

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private ShiftMapper shiftMapper;

    /**
     * Persiste os turnos recebidos do PDV. A operação é idempotente:
     * reenvios do mesmo lote apenas atualizam os registros já existentes.
     */
    public void saveAll(List<ShiftSyncDTO> shifts) {
        if (shifts == null || shifts.isEmpty()) {
            return;
        }
        try {
            for (ShiftSyncDTO dto : shifts) {
                if (dto == null) {
                    continue;
                }
                Shift existing = shiftRepository.findById(dto.getId()).orElse(null);
                if (existing != null) {
                    shiftMapper.updateFromSync(dto, existing);
                    shiftRepository.saveAndFlush(existing);
                } else {
                    shiftRepository.saveAndFlush(shiftMapper.toEntity(dto));
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
