package com.br.manager.domain.operacional.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.comum.service.ServicoSincronizacao;
import com.br.manager.domain.operacional.dto.FornecedorInputDTO;
import com.br.manager.domain.operacional.dto.FornecedorResponseDTO;
import com.br.manager.domain.operacional.entity.Fornecedor;
import com.br.manager.domain.operacional.mapper.FornecedorMapper;
import com.br.manager.domain.operacional.repository.FornecedorRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.br.manager.domain.comum.UtilStrings;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FornecedorService {
    @Autowired private FornecedorRepository fornecedorRepository;
    @Autowired private FornecedorMapper fornecedorMapper;
    @Autowired private ServicoSincronizacao sincronizacaoService;

    public FornecedorResponseDTO create(FornecedorInputDTO inputDTO) {
        try {
            Fornecedor entity = fornecedorMapper.fornecedorInputDTOToFornecedor(inputDTO);
            return fornecedorMapper.fornecedorToFornecedorResponseDTO(fornecedorRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException e) {
            throw new ExcecaoNegocio(e.getConstraintViolations().stream().map(v -> v.getMessage()).toList().toString());
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao salvar fornecedor", e);
        }
    }

    public FornecedorResponseDTO update(FornecedorInputDTO inputDTO) {
        try {
            Fornecedor entity = fornecedorRepository.findByIdAndAtivoTrue(inputDTO.getId());
            if (entity == null) { throw new ExcecaoNegocioNaoEncontrado(String.format("Fornecedor com ID %s não encontrado", inputDTO.getId())); }
            fornecedorMapper.updateFornecedorFromDto(inputDTO, entity);
            return fornecedorMapper.fornecedorToFornecedorResponseDTO(fornecedorRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException e) {
            throw new ExcecaoNegocio(e.getConstraintViolations().stream().map(v -> v.getMessage()).toList().toString());
        } catch (ExcecaoNegocioNaoEncontrado e) { throw e; } catch (Exception e) { throw new ExcecaoNegocio("Erro ao atualizar fornecedor", e); }
    }

    public List<FornecedorResponseDTO> findAll() { return fornecedorMapper.listFornecedorToListFornecedorResponseDTO(fornecedorRepository.findAllByAtivoTrue()); }

    public List<FornecedorResponseDTO> findByDescricao(String descricao) {
        if (!UtilStrings.hasText(descricao)) return findAll();
        return fornecedorMapper.listFornecedorToListFornecedorResponseDTO(fornecedorRepository.findByLegalNameContainingIgnoreCaseAndAtivoTrue(descricao));
    }

    public FornecedorResponseDTO find(UUID id) {
        Fornecedor entity = fornecedorRepository.findByIdAndAtivoTrue(id);
        if (entity == null) throw new ExcecaoNegocioNaoEncontrado(String.format("Fornecedor com ID %s não encontrado", id));
        return fornecedorMapper.fornecedorToFornecedorResponseDTO(entity);
    }

    public void delete(UUID id) {
        Fornecedor entity = fornecedorRepository.findById(id).orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Fornecedor com ID %s não encontrado", id)));
        entity.setAtivo(false);
        entity.setExcluidoEm(LocalDateTime.now());
        fornecedorRepository.saveAndFlush(entity);
    }

    /**
     * Retorna registros criados, atualizados ou deletados após a última sincronização da tabela informada.
     */
    public List<FornecedorResponseDTO> buscarAlteradosDesdeSincronizacao(String tabelaName) {
        var sincronizacao = sincronizacaoService.findByTabelaName(tabelaName);
        var since = sincronizacao.getUltimaSincronizacao();
        if (since == null) {
            // se não houver data, considerar desde a época Unix para garantir consistência
            since = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
        }
        List<Fornecedor> changed = fornecedorRepository.findAllChangedSince(since);
        return fornecedorMapper.listFornecedorToListFornecedorResponseDTO(changed);
    }
}
