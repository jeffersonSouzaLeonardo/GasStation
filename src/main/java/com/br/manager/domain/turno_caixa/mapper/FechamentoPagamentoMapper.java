package com.br.manager.domain.turno_caixa.mapper;

import com.br.manager.domain.turno_caixa.dto.FechamentoPagamentoInputDTO;
import com.br.manager.domain.turno_caixa.dto.FechamentoPagamentoResponseDTO;
import com.br.manager.domain.turno_caixa.entity.FechamentoPagamento;
import com.br.manager.domain.turno_caixa.entity.Turno;
import org.springframework.stereotype.Component;

@Component
public class FechamentoPagamentoMapper {

    public FechamentoPagamento toEntity(FechamentoPagamentoInputDTO dto, Turno turno) {
        if (dto == null) {
            return null;
        }

        FechamentoPagamento fechamento = new FechamentoPagamento();
        fechamento.setTurno(turno);
        fechamento.setPagamentoMethodId(dto.getPagamentoMethodId());
        fechamento.setValorEsperado(dto.getValorEsperado());
        fechamento.setValorDeclarado(dto.getValorDeclarado());
        fechamento.setValorDiferenca(dto.getValorDeclarado().subtract(dto.getValorEsperado()));
        fechamento.setReferenciaComprovante(dto.getReferenciaComprovante());
        return fechamento;
    }

    public FechamentoPagamentoResponseDTO toResponse(FechamentoPagamento fechamento) {
        if (fechamento == null) {
            return null;
        }

        FechamentoPagamentoResponseDTO dto = new FechamentoPagamentoResponseDTO();
        dto.setId(fechamento.getId());
        dto.setTurnoId(fechamento.getTurno() != null ? fechamento.getTurno().getId() : null);
        dto.setPagamentoMethodId(fechamento.getPagamentoMethodId());
        dto.setValorEsperado(fechamento.getValorEsperado());
        dto.setValorDeclarado(fechamento.getValorDeclarado());
        dto.setValorDiferenca(fechamento.getValorDiferenca());
        dto.setReferenciaComprovante(fechamento.getReferenciaComprovante());
        return dto;
    }
}
