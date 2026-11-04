package com.br.manager.domain.organizacao.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.organizacao.dto.EmpresaInputDTO;
import com.br.manager.domain.organizacao.dto.EmpresaResponseDTO;
import com.br.manager.domain.organizacao.entity.Empresa;
import com.br.manager.domain.organizacao.mapper.EmpresaMapper;
import com.br.manager.domain.organizacao.repository.EmpresaRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.br.manager.domain.comum.UtilStrings;

import java.util.List;
import java.util.UUID;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private EmpresaMapper empresaMapper;

    public EmpresaResponseDTO create(EmpresaInputDTO inputDTO) {
        try {
            Empresa empresa = empresaMapper.empresaInputDTOToEmpresa(inputDTO);
            return empresaMapper.empresaToEmpresaResponseDTO(empresaRepository.saveAndFlush(empresa));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (Exception e) {
            String empresaName = inputDTO != null && UtilStrings.hasText(inputDTO.getLegalName()) ? inputDTO.getLegalName() : "";
            throw new ExcecaoNegocio("Erro ao salvar empresa " + empresaName, e);
        }
    }

    public EmpresaResponseDTO update(EmpresaInputDTO inputDTO) {
        try {
            Empresa empresa = empresaRepository.findByIdAndAtivoTrue(inputDTO.getId());
            if (empresa == null) {
                throw new ExcecaoNegocioNaoEncontrado(String.format("Empresa com ID %s não encontrada", inputDTO.getId()));
            }
            empresaMapper.updateEmpresaFromDto(inputDTO, empresa);
            return empresaMapper.empresaToEmpresaResponseDTO(empresaRepository.saveAndFlush(empresa));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            String empresaName = inputDTO != null && UtilStrings.hasText(inputDTO.getLegalName()) ? inputDTO.getLegalName() : "";
            throw new ExcecaoNegocio("Erro ao salvar empresa " + empresaName, e);
        }
    }

    public List<EmpresaResponseDTO> findAll() {
        return empresaMapper.listEmpresaToListEmpresaResponseDTO(empresaRepository.findAllByAtivoTrue());
    }

    public List<EmpresaResponseDTO> findByDescricao(String descricao) {
        if (!UtilStrings.hasText(descricao)) {
            return findAll();
        }
        return empresaMapper.listEmpresaToListEmpresaResponseDTO(
                empresaRepository.findByLegalNameContainingIgnoreCaseAndAtivoTrue(descricao));
    }

    public List<EmpresaResponseDTO> findByName(String nome) {
        return findByDescricao(nome);
    }

    public EmpresaResponseDTO find(UUID id) {
        Empresa empresa = empresaRepository.findByIdAndAtivoTrue(id);
        if (empresa == null) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Empresa com ID %s não encontrada", id));
        }
        return empresaMapper.empresaToEmpresaResponseDTO(empresa);
    }

    public void delete(UUID id) {
        try {
            Empresa empresa = empresaRepository.findById(id)
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Empresa com ID %s não encontrada", id)));
            empresa.setAtivo(false);
            empresaRepository.saveAndFlush(empresa);
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao excluir empresa", e);
        }
    }

    public Empresa getEmpresaEntityById(UUID id) {
        Empresa empresa = empresaRepository.findByIdAndAtivoTrue(id);
        if (empresa == null) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Empresa com ID %s não encontrada", id));
        }
        return empresa;
    }
}
