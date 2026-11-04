package com.br.manager.domain.turno_caixa.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.turno_caixa.dto.TurnoSincronizacaoDTO;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.mapper.TurnoMapper;
import com.br.manager.domain.turno_caixa.repository.TurnoRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class TurnoSincronizacaoService {

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private TurnoMapper turnoMapper;

    /**
     * Persiste os turnos recebidos do PDV. A operação é idempotente:
     * reenvios do mesmo lote apenas atualizam os registros já existentes.
     */
    public void saveAll(List<TurnoSincronizacaoDTO> turnos) {
        if (turnos == null || turnos.isEmpty()) {
            return;
        }
        try {
            for (TurnoSincronizacaoDTO dto : turnos) {
                if (dto == null) {
                    continue;
                }
                Turno existing = turnoRepository.findById(dto.getId()).orElse(null);
                if (existing != null) {
                    turnoMapper.updateFromSincronizacao(dto, existing);
                    turnoRepository.saveAndFlush(existing);
                } else {
                    turnoRepository.saveAndFlush(turnoMapper.toEntity(dto));
                }
            }
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        }
    }
}
