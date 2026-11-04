package com.br.manager.domain.turno_caixa.mapper;

import com.br.manager.domain.turno_caixa.dto.SessaoCaixaInputDTO;
import com.br.manager.domain.turno_caixa.dto.SessaoCaixaResponseDTO;
import com.br.manager.domain.turno_caixa.dto.SessaoCaixaSincronizacaoDTO;
import com.br.manager.domain.turno_caixa.entity.SessaoCaixa;
import com.br.manager.domain.turno_caixa.entity.Turno;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SessaoCaixaMapper {

    public SessaoCaixa toEntity(SessaoCaixaInputDTO dto, Turno turno) {
        if (dto == null) {
            return null;
        }

        SessaoCaixa sessao = new SessaoCaixa();
        sessao.setTurno(turno);
        sessao.setCaixeiroUsuarioId(dto.getCaixeiroUsuarioId());
        sessao.setCargo(dto.getCargo());
        sessao.setFundoAbertura(dto.getFundoAbertura());
        sessao.setObservacoes(dto.getObservacoes());
        return sessao;
    }

    public SessaoCaixa toEntity(SessaoCaixaSincronizacaoDTO dto, Turno turno) {
        if (dto == null) {
            return null;
        }

        SessaoCaixa sessao = new SessaoCaixa();
        sessao.setId(dto.getId());
        sessao.setTurno(turno);
        sessao.setCaixeiroUsuarioId(dto.getCaixeiroUsuarioId());
        sessao.setCargo(dto.getCargo());
        sessao.setFundoAbertura(dto.getFundoAbertura());
        sessao.setStatus(dto.getStatus());
        sessao.setOpenedAt(dto.getOpenedAt());
        sessao.setFechadoAt(dto.getFechadoAt());
        sessao.setTotalDinheiro(dto.getTotalDinheiro());
        sessao.setTotalCartao(dto.getTotalCartao());
        sessao.setTotalPix(dto.getTotalPix());
        sessao.setTotalSangria(dto.getTotalSangria());
        sessao.setObservacoes(dto.getObservacoes());
        return sessao;
    }

    public void updateFromSincronizacao(SessaoCaixaSincronizacaoDTO dto, SessaoCaixa sessao) {
        if (dto == null || sessao == null) {
            return;
        }

        sessao.setCaixeiroUsuarioId(dto.getCaixeiroUsuarioId());
        sessao.setCargo(dto.getCargo());
        sessao.setFundoAbertura(dto.getFundoAbertura());
        sessao.setStatus(dto.getStatus());
        sessao.setOpenedAt(dto.getOpenedAt());
        sessao.setFechadoAt(dto.getFechadoAt());
        sessao.setTotalDinheiro(dto.getTotalDinheiro());
        sessao.setTotalCartao(dto.getTotalCartao());
        sessao.setTotalPix(dto.getTotalPix());
        sessao.setTotalSangria(dto.getTotalSangria());
        sessao.setObservacoes(dto.getObservacoes());
    }

    public SessaoCaixaResponseDTO toResponse(SessaoCaixa sessao) {
        if (sessao == null) {
            return null;
        }

        SessaoCaixaResponseDTO dto = new SessaoCaixaResponseDTO();
        dto.setId(sessao.getId());
        dto.setTurnoId(sessao.getTurno() != null ? sessao.getTurno().getId() : null);
        dto.setCaixeiroUsuarioId(sessao.getCaixeiroUsuarioId());
        dto.setCargo(sessao.getCargo());
        dto.setFundoAbertura(sessao.getFundoAbertura());
        dto.setStatus(sessao.getStatus());
        dto.setOpenedAt(sessao.getOpenedAt());
        dto.setFechadoAt(sessao.getFechadoAt());
        dto.setTotalDinheiro(sessao.getTotalDinheiro());
        dto.setTotalCartao(sessao.getTotalCartao());
        dto.setTotalPix(sessao.getTotalPix());
        dto.setTotalSangria(sessao.getTotalSangria());
        dto.setObservacoes(sessao.getObservacoes());
        dto.setCriadoEm(sessao.getCriadoEm());
        return dto;
    }

    public List<SessaoCaixaResponseDTO> toResponseList(List<SessaoCaixa> sessoes) {
        if (sessoes == null) {
            return List.of();
        }
        return sessoes.stream().map(this::toResponse).toList();
    }
}
