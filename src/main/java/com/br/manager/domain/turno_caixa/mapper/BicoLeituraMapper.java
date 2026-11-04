package com.br.manager.domain.turno_caixa.mapper;

import com.br.manager.domain.turno_caixa.dto.BicoLeituraInputDTO;
import com.br.manager.domain.turno_caixa.dto.BicoLeituraResponseDTO;
import com.br.manager.domain.turno_caixa.entity.LeituraBico;
import com.br.manager.domain.turno_caixa.entity.Turno;
import org.springframework.stereotype.Component;

@Component
public class BicoLeituraMapper {

    public LeituraBico toEntity(BicoLeituraInputDTO dto, Turno turno) {
        if (dto == null) {
            return null;
        }

        LeituraBico leitura = new LeituraBico();
        leitura.setTurno(turno);
        leitura.setBicoId(dto.getBicoId());
        leitura.setTipoLeitura(dto.getTipoLeitura());
        leitura.setLeituraMedidor(dto.getLeituraMedidor());
        leitura.setLidoEm(dto.getLidoEm());
        leitura.setRegistradoPor(dto.getRegistradoPor());
        leitura.setObservacoes(dto.getObservacoes());
        return leitura;
    }

    public BicoLeituraResponseDTO toResponse(LeituraBico leitura) {
        if (leitura == null) {
            return null;
        }

        BicoLeituraResponseDTO dto = new BicoLeituraResponseDTO();
        dto.setId(leitura.getId());
        dto.setTurnoId(leitura.getTurno() != null ? leitura.getTurno().getId() : null);
        dto.setBicoId(leitura.getBicoId());
        dto.setTipoLeitura(leitura.getTipoLeitura());
        dto.setLeituraMedidor(leitura.getLeituraMedidor());
        dto.setLidoEm(leitura.getLidoEm());
        dto.setRegistradoPor(leitura.getRegistradoPor());
        dto.setObservacoes(leitura.getObservacoes());
        return dto;
    }
}
