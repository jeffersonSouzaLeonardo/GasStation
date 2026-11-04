package com.br.manager.domain.turno_caixa.mapper;

import com.br.manager.domain.turno_caixa.dto.TurnoAberturaRequestDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoResponseDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoSincronizacaoDTO;
import com.br.manager.domain.turno_caixa.entity.Turno;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TurnoMapper {

    public Turno toEntity(TurnoAberturaRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Turno turno = new Turno();
        turno.setEstacaoId(dto.getEstacaoId());
        turno.setCaixeiroUsuarioId(dto.getCaixeiroUsuarioId());
        turno.setFundoAbertura(dto.getFundoAbertura());
        turno.setOpenedBy(dto.getOpenedBy());
        turno.setObservacoes(dto.getObservacoes());
        return turno;
    }

    /**
     * Turno recebido do PDV. Como o schema exige caixeiro_usuario_id/abriring_dinheiro,
     * usa aberto_por como caixa responsável e zero como fundo de troco padrão.
     */
    public Turno toEntity(TurnoSincronizacaoDTO dto) {
        if (dto == null) {
            return null;
        }

        Turno turno = new Turno();
        turno.setId(dto.getId());
        turno.setEstacaoId(dto.getEstacaoId());
        turno.setCaixeiroUsuarioId(dto.getOpenedBy());
        turno.setNome(dto.getNome());
        turno.setOpenedAt(dto.getOpenedAt());
        turno.setFechadoAt(dto.getFechadoAt());
        turno.setFundoAbertura(BigDecimal.ZERO);
        turno.setStatus(dto.getStatus());
        turno.setOpenedBy(dto.getOpenedBy());
        turno.setFechadoBy(dto.getFechadoBy());
        turno.setObservacoes(dto.getObservacoes());
        return turno;
    }

    public void updateFromSincronizacao(TurnoSincronizacaoDTO dto, Turno turno) {
        if (dto == null || turno == null) {
            return;
        }

        turno.setEstacaoId(dto.getEstacaoId());
        turno.setNome(dto.getNome());
        turno.setOpenedAt(dto.getOpenedAt());
        turno.setFechadoAt(dto.getFechadoAt());
        turno.setStatus(dto.getStatus());
        turno.setOpenedBy(dto.getOpenedBy());
        turno.setFechadoBy(dto.getFechadoBy());
        turno.setObservacoes(dto.getObservacoes());
    }

    public TurnoResponseDTO toResponse(Turno turno) {
        if (turno == null) {
            return null;
        }

        TurnoResponseDTO dto = new TurnoResponseDTO();
        dto.setId(turno.getId());
        dto.setEstacaoId(turno.getEstacaoId());
        dto.setCaixeiroUsuarioId(turno.getCaixeiroUsuarioId());
        dto.setOpenedAt(turno.getOpenedAt());
        dto.setFechadoAt(turno.getFechadoAt());
        dto.setFundoAbertura(turno.getFundoAbertura());
        dto.setStatus(turno.getStatus());
        dto.setOpenedBy(turno.getOpenedBy());
        dto.setFechadoBy(turno.getFechadoBy());
        dto.setObservacoes(turno.getObservacoes());
        return dto;
    }
}
