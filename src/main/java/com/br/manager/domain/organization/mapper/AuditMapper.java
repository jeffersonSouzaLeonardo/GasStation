package com.br.manager.domain.organization.mapper;

import com.br.manager.domain.organization.dto.AuditInputDTO;
import com.br.manager.domain.organization.dto.AuditResponseDTO;
import com.br.manager.domain.organization.entity.Audit;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuditMapper {
    public Audit auditInputDTOToAudit(AuditInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        Audit entity = new Audit();
        BeanUtils.copyProperties(inputDTO, entity);
        return entity;
    }

    public AuditResponseDTO auditToAuditResponseDTO(Audit audit) {
        if (audit == null) {
            return null;
        }
        AuditResponseDTO dto = new AuditResponseDTO();
        BeanUtils.copyProperties(audit, dto);
        return dto;
    }

    public List<AuditResponseDTO> listAuditToListAuditResponseDTO(List<Audit> audits) {
        if (audits == null) {
            return null;
        }
        return audits.stream().map(this::auditToAuditResponseDTO).toList();
    }

    public void updateAuditFromDto(AuditInputDTO dto, Audit entity) {
        if (dto == null || entity == null) {
            return;
        }
        BeanUtils.copyProperties(dto, entity);
    }
}
