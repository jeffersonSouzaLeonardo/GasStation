package com.br.manager.domain.operacional.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.comum.service.ServicoSincronizacao;
import com.br.manager.domain.operacional.dto.BicoInputDTO;
import com.br.manager.domain.operacional.dto.BicoResponseDTO;
import com.br.manager.domain.operacional.entity.Bico;
import com.br.manager.domain.operacional.entity.Produto;
import com.br.manager.domain.operacional.enums.ProdutoTypeEnum;
import com.br.manager.domain.operacional.mapper.BicoMapper;
import com.br.manager.domain.operacional.repository.BicoRepository;
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
public class BicoService {

    @Autowired
    private BicoRepository bicoRepository;

    @Autowired
    private BicoMapper bicoMapper;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ServicoSincronizacao sincronizacaoService;

    public BicoResponseDTO create(BicoInputDTO inputDTO) {
        try {
            Produto produto = produtoService.getProdutoEntityById(inputDTO.getProdutoId());
            if (produto.getProdutoType() != ProdutoTypeEnum.COMBUSTIVEL) {
                throw new ExcecaoNegocio("Apenas produtos do tipo COMBUSTÍVEL podem ser vendidos pela bomba.");
            }
            Bico entity = bicoMapper.bicoInputDTOToBico(inputDTO);
            if (entity.getId() == null) {
                            }
            return bicoMapper.bicoToBicoResponseDTO(bicoRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocio exception) {
            throw exception;
        } catch (Exception e) {
            String bicoCode = inputDTO != null && UtilStrings.hasText(inputDTO.getCodigo()) ? inputDTO.getCodigo() : "";
            throw new ExcecaoNegocio("Erro ao salvar bomba " + bicoCode, e);
        }
    }

    public BicoResponseDTO update(BicoInputDTO inputDTO) {
        try {
            Bico entity = bicoRepository.findByIdAndAtivoTrue(inputDTO.getId());
            if (entity == null) {
                throw new ExcecaoNegocioNaoEncontrado(String.format("Bomba com ID %s não encontrada", inputDTO.getId()));
            }
            Produto produto = produtoService.getProdutoEntityById(inputDTO.getProdutoId());
            if (produto.getProdutoType() != ProdutoTypeEnum.COMBUSTIVEL) {
                throw new ExcecaoNegocio("Apenas produtos do tipo COMBUSTÍVEL podem ser vendidos pela bomba.");
            }
            bicoMapper.updateBicoFromDto(inputDTO, entity);
            return bicoMapper.bicoToBicoResponseDTO(bicoRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (ExcecaoNegocio exception) {
            throw exception;
        } catch (Exception e) {
            String bicoCode = inputDTO != null && UtilStrings.hasText(inputDTO.getCodigo()) ? inputDTO.getCodigo() : "";
            throw new ExcecaoNegocio("Erro ao salvar bomba " + bicoCode, e);
        }
    }

    public List<BicoResponseDTO> findAll() {
        return bicoMapper.listBicoToListBicoResponseDTO(bicoRepository.findAllByAtivoTrue());
    }

    public List<BicoResponseDTO> findByDescricao(String descricao) {
        if (!UtilStrings.hasText(descricao)) {
            return findAll();
        }
        return bicoMapper.listBicoToListBicoResponseDTO(bicoRepository.findByCodeContainingIgnoreCaseAndAtivoTrue(descricao));
    }

    public BicoResponseDTO find(UUID id) {
        Bico entity = bicoRepository.findByIdAndAtivoTrue(id);
        if (entity == null) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Bomba com ID %s não encontrada", id));
        }
        return bicoMapper.bicoToBicoResponseDTO(entity);
    }

    public void delete(UUID id) {
        try {
            Bico entity = bicoRepository.findById(id)
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Bomba com ID %s não encontrada", id)));
            entity.setAtivo(false);
            entity.setExcluidoEm(LocalDateTime.now());
            bicoRepository.saveAndFlush(entity);
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

    public Bico getBicoEntityById(UUID id) {
        Bico entity = bicoRepository.findByIdAndAtivoTrue(id);
        if (entity == null) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Bomba com ID %s não encontrada", id));
        }
        return entity;
    }

    /**
     * Retorna registros criados, atualizados ou deletados após a última sincronização da tabela informada.
     */
    public List<BicoResponseDTO> buscarAlteradosDesdeSincronizacao(String tabelaName) {
        var sincronizacao = sincronizacaoService.findByTabelaName(tabelaName);
        var since = sincronizacao.getUltimaSincronizacao();
        if (since == null) {
            // se não houver data, considerar desde a época Unix para garantir consistência
            since = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
        }
        List<Bico> changed = bicoRepository.findAllChangedSince(since);
        return bicoMapper.listBicoToListBicoResponseDTO(changed);
    }
}
