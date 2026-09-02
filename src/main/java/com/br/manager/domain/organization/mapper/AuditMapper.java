package com.br.manager.domain.organization.mapper;

import com.br.manager.domain.organization.dto.AuditInputDTO;
import com.br.manager.domain.organization.dto.AuditResponseDTO;
import com.br.manager.domain.organization.entity.Audit;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuditMapper {

    Audit auditInputDTOToAudit(AuditInputDTO inputDTO);

    AuditResponseDTO auditToAuditResponseDTO(Audit audit);

    List<AuditResponseDTO> listAuditToListAuditResponseDTO(List<Audit> audits);

    void updateAuditFromDto(AuditInputDTO dto, @MappingTarget Audit entity);
}
