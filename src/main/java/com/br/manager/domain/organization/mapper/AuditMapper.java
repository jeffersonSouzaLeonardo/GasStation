package com.br.manager.domain.organization.mapper;

import com.br.manager.domain.organization.dto.AuditInputDTO;
import com.br.manager.domain.organization.dto.AuditResponseDTO;
import com.br.manager.domain.organization.entity.Audit;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class AuditMapper {
    public abstract Audit auditInputDTOToAudit(AuditInputDTO inputDTO);

    public abstract AuditResponseDTO auditToAuditResponseDTO(Audit audit);

    public abstract List<AuditResponseDTO> listAuditToListAuditResponseDTO(List<Audit> audits);

    public abstract void updateAuditFromDto(AuditInputDTO dto, @MappingTarget Audit entity);
}
