package com.br.manager.domain.organization.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.organization.dto.AuditInputDTO;
import com.br.manager.domain.organization.dto.AuditResponseDTO;
import com.br.manager.domain.organization.entity.Audit;
import com.br.manager.domain.organization.mapper.AuditMapper;
import com.br.manager.domain.organization.repository.AuditRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuditService {

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    private AuditMapper auditMapper;

    public AuditResponseDTO create(AuditInputDTO inputDTO) {
        try {
            Audit audit = auditMapper.auditInputDTOToAudit(inputDTO);
            if (audit.getId() == null) {
                audit.setId(UUID.randomUUID());
            }
            if (audit.getCreatedAt() == null) {
                audit.setCreatedAt(OffsetDateTime.now());
            }
            return auditMapper.auditToAuditResponseDTO(auditRepository.saveAndFlush(audit));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (Exception e) {
            String entityName = inputDTO != null && StringUtils.hasText(inputDTO.getEntityName()) ? inputDTO.getEntityName() : "";
            throw new BusinessException("Error while saving audit " + entityName, e);
        }
    }

    public AuditResponseDTO update(AuditInputDTO inputDTO) {
        try {
            Audit audit = auditRepository.findById(inputDTO.getId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Audit with ID %s not found", inputDTO.getId())));
            auditMapper.updateAuditFromDto(inputDTO, audit);
            return auditMapper.auditToAuditResponseDTO(auditRepository.saveAndFlush(audit));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException e) {
            throw e;
        } catch (Exception e) {
            String entityName = inputDTO != null && StringUtils.hasText(inputDTO.getEntityName()) ? inputDTO.getEntityName() : "";
            throw new BusinessException("Error while saving audit " + entityName, e);
        }
    }

    public List<AuditResponseDTO> findAll() {
        return auditMapper.listAuditToListAuditResponseDTO(auditRepository.findAllByOrderByCreatedAtDesc());
    }

    public List<AuditResponseDTO> findByDescription(String description) {
        if (!StringUtils.hasText(description)) {
            return findAll();
        }
        return auditMapper.listAuditToListAuditResponseDTO(
                auditRepository.findByEntityNameContainingIgnoreCaseAndOrderByCreatedAtDesc(description));
    }

    public List<AuditResponseDTO> findByName(String name) {
        return findByDescription(name);
    }

    public AuditResponseDTO find(UUID id) {
        Audit audit = auditRepository.findById(id)
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Audit with ID %s not found", id)));
        return auditMapper.auditToAuditResponseDTO(audit);
    }

    public void delete(UUID id) {
        try {
            Audit audit = auditRepository.findById(id)
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Audit with ID %s not found", id)));
            auditRepository.delete(audit);
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Error while deleting audit", e);
        }
    }

    public Audit getAuditEntityById(UUID id) {
        return auditRepository.findById(id)
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Audit with ID %s not found", id)));
    }
}
