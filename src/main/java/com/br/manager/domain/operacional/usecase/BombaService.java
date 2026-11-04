package com.br.manager.domain.operacional.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.comum.service.ServicoSincronizacao;
import com.br.manager.domain.operacional.dto.BombaInputDTO;
import com.br.manager.domain.operacional.dto.BombaResponseDTO;
import com.br.manager.domain.operacional.entity.Bomba;
import com.br.manager.domain.operacional.mapper.BombaMapper;
import com.br.manager.domain.operacional.repository.BombaRepository;
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
public class BombaService {

    @Autowired
    private BombaRepository bombaRepository;

    @Autowired
    private BombaMapper bombaMapper;

    @Autowired
    private ServicoSincronizacao sincronizacaoService;

    public BombaResponseDTO create(BombaInputDTO inputDTO) {
        try {
            Bomba entity = bombaMapper.bombaInputDTOToBomba(inputDTO);
            return bombaMapper.bombaToBombaResponseDTO(bombaRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (Exception e) {
            String bombaCode = inputDTO != null && UtilStrings.hasText(inputDTO.getCodigo()) ? inputDTO.getCodigo() : "";
            throw new ExcecaoNegocio("Erro ao salvar bomba " + bombaCode, e);
        }
    }

    public BombaResponseDTO update(BombaInputDTO inputDTO) {
        try {
            Bomba entity = bombaRepository.findByIdAndAtivoTrue(inputDTO.getId());
            if (entity == null) {
                throw new ExcecaoNegocioNaoEncontrado(String.format("Bomba com ID %s não encontrada", inputDTO.getId()));
            }
            bombaMapper.updateBombaFromDto(inputDTO, entity);
            return bombaMapper.bombaToBombaResponseDTO(bombaRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            String bombaCode = inputDTO != null && UtilStrings.hasText(inputDTO.getCodigo()) ? inputDTO.getCodigo() : "";
            throw new ExcecaoNegocio("Erro ao salvar bomba " + bombaCode, e);
        }
    }

    public List<BombaResponseDTO> findAll() {
        return bombaMapper.listBombaToListBombaResponseDTO(bombaRepository.findAllByAtivoTrue());
    }

    public List<BombaResponseDTO> findByDescricao(String descricao) {
        if (!UtilStrings.hasText(descricao)) {
            return findAll();
        }
        return bombaMapper.listBombaToListBombaResponseDTO(bombaRepository.findByCodeContainingIgnoreCaseAndAtivoTrue(descricao));
    }

    public BombaResponseDTO find(UUID id) {
        Bomba entity = bombaRepository.findByIdAndAtivoTrue(id);
        if (entity == null) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Bomba com ID %s não encontrada", id));
        }
        return bombaMapper.bombaToBombaResponseDTO(entity);
    }

    public void delete(UUID id) {
        try {
            Bomba entity = bombaRepository.findById(id)
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Bomba com ID %s não encontrada", id)));
            entity.setAtivo(false);
            entity.setExcluidoEm(LocalDateTime.now());
            bombaRepository.saveAndFlush(entity);
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao excluir bomba", e);
        }
    }

    public Bomba getBombaEntityById(UUID id) {
        Bomba entity = bombaRepository.findByIdAndAtivoTrue(id);
        if (entity == null) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Bomba com ID %s não encontrada", id));
        }
        return entity;
    }

    /**
     * Retorna registros criados, atualizados ou deletados após a última sincronização da tabela informada.
     */
    public List<BombaResponseDTO> buscarAlteradosDesdeSincronizacao(String tabelaName) {
        var sincronizacao = sincronizacaoService.findByTabelaName(tabelaName);
        var since = sincronizacao.getUltimaSincronizacao();
        if (since == null) {
            // se não houver data, considerar desde a época Unix para garantir consistência
            since = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
        }
        List<Bomba> changed = bombaRepository.findAllChangedSince(since);
        return bombaMapper.listBombaToListBombaResponseDTO(changed);
    }
}
