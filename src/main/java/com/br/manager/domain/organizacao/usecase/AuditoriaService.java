package com.br.manager.domain.organizacao.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.organizacao.dto.AuditoriaInputDTO;
import com.br.manager.domain.organizacao.dto.AuditoriaResponseDTO;
import com.br.manager.domain.organizacao.entity.Auditoria;
import com.br.manager.domain.organizacao.mapper.AuditoriaMapper;
import com.br.manager.domain.organizacao.repository.AuditoriaRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.br.manager.domain.comum.UtilStrings;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuditoriaService {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private AuditoriaMapper auditoriaMapper;

    public AuditoriaResponseDTO create(AuditoriaInputDTO inputDTO) {
        try {
            Auditoria auditoria = auditoriaMapper.auditoriaInputDTOToAuditoria(inputDTO);
            if (auditoria.getCriadoEm() == null) {
                auditoria.setCriadoEm(OffsetDateTime.now());
            }
            return auditoriaMapper.auditoriaToAuditoriaResponseDTO(auditoriaRepository.saveAndFlush(auditoria));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (Exception e) {
            String entityName = inputDTO != null && UtilStrings.hasText(inputDTO.getEntityName()) ? inputDTO.getEntityName() : "";
            throw new ExcecaoNegocio("Erro ao salvar auditoria " + entityName, e);
        }
    }

    public AuditoriaResponseDTO update(AuditoriaInputDTO inputDTO) {
        try {
            Auditoria auditoria = auditoriaRepository.findById(inputDTO.getId())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Auditoria com ID %s não encontrada", inputDTO.getId())));
            auditoriaMapper.updateAuditoriaFromDto(inputDTO, auditoria);
            return auditoriaMapper.auditoriaToAuditoriaResponseDTO(auditoriaRepository.saveAndFlush(auditoria));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado e) {
            throw e;
        } catch (Exception e) {
            String entityName = inputDTO != null && UtilStrings.hasText(inputDTO.getEntityName()) ? inputDTO.getEntityName() : "";
            throw new ExcecaoNegocio("Erro ao salvar auditoria " + entityName, e);
        }
    }

    public List<AuditoriaResponseDTO> findAll() {
        return auditoriaMapper.listAuditoriaToListAuditoriaResponseDTO(auditoriaRepository.findAllByOrderByCriadoEmDesc());
    }

    public List<AuditoriaResponseDTO> findByDescricao(String descricao) {
        if (!UtilStrings.hasText(descricao)) {
            return findAll();
        }
        return auditoriaMapper.listAuditoriaToListAuditoriaResponseDTO(
                auditoriaRepository.findByEntityNameContainingIgnoreCaseAndOrderByCriadoEmDesc(descricao));
    }

    public List<AuditoriaResponseDTO> findByName(String nome) {
        return findByDescricao(nome);
    }

    public AuditoriaResponseDTO find(UUID id) {
        Auditoria auditoria = auditoriaRepository.findById(id)
                .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Auditoria com ID %s não encontrada", id)));
        return auditoriaMapper.auditoriaToAuditoriaResponseDTO(auditoria);
    }

    public void delete(UUID id) {
        try {
            Auditoria auditoria = auditoriaRepository.findById(id)
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Auditoria com ID %s não encontrada", id)));
            auditoriaRepository.delete(auditoria);
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado e) {
            throw e;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao excluir auditoria", e);
        }
    }

    public Auditoria getAuditoriaEntityById(UUID id) {
        return auditoriaRepository.findById(id)
                .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Auditoria com ID %s não encontrada", id)));
    }
}
