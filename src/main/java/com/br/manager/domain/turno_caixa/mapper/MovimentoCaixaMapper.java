package com.br.manager.domain.turno_caixa.mapper;

import com.br.manager.domain.turno_caixa.dto.MovimentoCaixaInputDTO;
import com.br.manager.domain.turno_caixa.dto.MovimentoCaixaResponseDTO;
import com.br.manager.domain.turno_caixa.entity.MovimentoCaixa;
import com.br.manager.domain.turno_caixa.entity.Turno;
import org.springframework.stereotype.Component;

@Component
public class MovimentoCaixaMapper {

    public MovimentoCaixa toEntity(MovimentoCaixaInputDTO dto, Turno turno) {
        if (dto == null) {
            return null;
        }

        MovimentoCaixa movimento = new MovimentoCaixa();
        movimento.setTurno(turno);
        movimento.setTipoMovimento(dto.getTipoMovimento());
        movimento.setPagamentoMethodId(dto.getPagamentoMethodId());
        movimento.setValor(dto.getValor());
        movimento.setOcorridoEm(dto.getOcorridoEm());
        movimento.setReferencia(dto.getReferencia());
        movimento.setMotivo(dto.getMotivo());
        movimento.setCriadoPor(dto.getCriadoPor());
        movimento.setAprovadoPor(dto.getAprovadoPor());
        return movimento;
    }

    public MovimentoCaixaResponseDTO toResponse(MovimentoCaixa movimento) {
        if (movimento == null) {
            return null;
        }

        MovimentoCaixaResponseDTO dto = new MovimentoCaixaResponseDTO();
        dto.setId(movimento.getId());
        dto.setTurnoId(movimento.getTurno() != null ? movimento.getTurno().getId() : null);
        dto.setTipoMovimento(movimento.getTipoMovimento());
        dto.setPagamentoMethodId(movimento.getPagamentoMethodId());
        dto.setValor(movimento.getValor());
        dto.setOcorridoEm(movimento.getOcorridoEm());
        dto.setReferencia(movimento.getReferencia());
        dto.setMotivo(movimento.getMotivo());
        dto.setCriadoPor(movimento.getCriadoPor());
        dto.setAprovadoPor(movimento.getAprovadoPor());
        return dto;
    }
}
