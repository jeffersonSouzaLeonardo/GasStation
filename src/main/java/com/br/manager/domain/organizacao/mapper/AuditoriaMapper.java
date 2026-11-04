package com.br.manager.domain.organizacao.mapper;

import com.br.manager.domain.organizacao.dto.AuditoriaInputDTO;
import com.br.manager.domain.organizacao.dto.AuditoriaResponseDTO;
import com.br.manager.domain.organizacao.entity.Auditoria;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuditoriaMapper {
    public Auditoria auditoriaInputDTOToAuditoria(AuditoriaInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        Auditoria entity = new Auditoria();
        BeanUtils.copyProperties(inputDTO, entity);
        return entity;
    }

    public AuditoriaResponseDTO auditoriaToAuditoriaResponseDTO(Auditoria auditoria) {
        if (auditoria == null) {
            return null;
        }
        AuditoriaResponseDTO dto = new AuditoriaResponseDTO();
        BeanUtils.copyProperties(auditoria, dto);
        return dto;
    }

    public List<AuditoriaResponseDTO> listAuditoriaToListAuditoriaResponseDTO(List<Auditoria> auditorias) {
        if (auditorias == null) {
            return null;
        }
        return auditorias.stream().map(this::auditoriaToAuditoriaResponseDTO).toList();
    }

    public void updateAuditoriaFromDto(AuditoriaInputDTO dto, Auditoria entity) {
        if (dto == null || entity == null) {
            return;
        }
        BeanUtils.copyProperties(dto, entity);
    }
}
