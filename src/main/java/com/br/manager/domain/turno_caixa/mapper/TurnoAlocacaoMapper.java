package com.br.manager.domain.turno_caixa.mapper;

import com.br.manager.domain.turno_caixa.dto.TurnoAlocacaoInputDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoAlocacaoResponseDTO;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.entity.AlocacaoTurno;
import org.springframework.stereotype.Component;

@Component
public class TurnoAlocacaoMapper {

    public AlocacaoTurno toEntity(TurnoAlocacaoInputDTO dto, Turno turno) {
        if (dto == null) {
            return null;
        }

        AlocacaoTurno alocacao = new AlocacaoTurno();
        alocacao.setTurno(turno);
        alocacao.setUsuarioId(dto.getUsuarioId());
        alocacao.setCargo(dto.getCargo());
        return alocacao;
    }

    public TurnoAlocacaoResponseDTO toResponse(AlocacaoTurno alocacao) {
        if (alocacao == null) {
            return null;
        }

        TurnoAlocacaoResponseDTO dto = new TurnoAlocacaoResponseDTO();
        dto.setId(alocacao.getId());
        dto.setTurnoId(alocacao.getTurno() != null ? alocacao.getTurno().getId() : null);
        dto.setUsuarioId(alocacao.getUsuarioId());
        dto.setCargo(alocacao.getCargo());
        return dto;
    }
}
