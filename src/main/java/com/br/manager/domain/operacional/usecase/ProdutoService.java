package com.br.manager.domain.operacional.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.comum.service.ServicoSincronizacao;
import com.br.manager.domain.operacional.dto.ProdutoInputDTO;
import com.br.manager.domain.operacional.dto.ProdutoResponseDTO;
import com.br.manager.domain.operacional.entity.Produto;
import com.br.manager.domain.operacional.enums.ProdutoTypeEnum;
import com.br.manager.domain.operacional.mapper.ProdutoMapper;
import com.br.manager.domain.operacional.repository.ProdutoRepository;
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
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoMapper produtoMapper;

    @Autowired
    private ServicoSincronizacao sincronizacaoService;

    public ProdutoResponseDTO create(ProdutoInputDTO inputDTO) {
        try {
            Produto produto = produtoMapper.produtoInputDTOToProduto(inputDTO);
            return produtoMapper.produtoToProdutoResponseDTO(produtoRepository.saveAndFlush(produto));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (Exception e) {
            String produtoName = inputDTO != null && UtilStrings.hasText(inputDTO.getNome()) ? inputDTO.getNome() : "";
            throw new ExcecaoNegocio("Erro ao salvar produto " + produtoName, e);
        }
    }

    public ProdutoResponseDTO update(ProdutoInputDTO inputDTO) {
        try {
            Produto produto = produtoRepository.findByIdAndAtivoTrue(inputDTO.getId());
            if (produto == null) {
                throw new ExcecaoNegocioNaoEncontrado(String.format("Produto com ID %s não encontrado", inputDTO.getId()));
            }
            produtoMapper.updateProdutoFromDto(inputDTO, produto);
            return produtoMapper.produtoToProdutoResponseDTO(produtoRepository.saveAndFlush(produto));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            String produtoName = inputDTO != null && UtilStrings.hasText(inputDTO.getNome()) ? inputDTO.getNome() : "";
            throw new ExcecaoNegocio("Erro ao salvar produto " + produtoName, e);
        }
    }

    public List<ProdutoResponseDTO> findAll() {
        return produtoMapper.listProdutoToListProdutoResponseDTO(produtoRepository.findAllByAtivoTrue());
    }

    public List<ProdutoResponseDTO> findProdutoTypeCombustivel() {
        return produtoMapper.listProdutoToListProdutoResponseDTO(produtoRepository.findByProdutoTypeAndAtivoTrue(ProdutoTypeEnum.COMBUSTIVEL));
    }


    public List<ProdutoResponseDTO> findByDescricao(String descricao) {
        if (!UtilStrings.hasText(descricao)) {
            return findAll();
        }
        return produtoMapper.listProdutoToListProdutoResponseDTO(
                produtoRepository.findByNameContainingIgnoreCaseAndAtivoTrue(descricao));
    }

    public ProdutoResponseDTO find(UUID id) {
        Produto produto = produtoRepository.findByIdAndAtivoTrue(id);
        if (produto == null) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Produto com ID %s não encontrado", id));
        }
        return produtoMapper.produtoToProdutoResponseDTO(produto);
    }

    public void delete(UUID id) {
        try {
            Produto produto = produtoRepository.findById(id)
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Produto com ID %s não encontrado", id)));
            produto.setAtivo(false);
            produto.setExcluidoEm(LocalDateTime.now());
            produtoRepository.saveAndFlush(produto);
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao excluir produto", e);
        }
    }

    public Produto getProdutoEntityById(UUID id) {
        Produto produto = produtoRepository.findByIdAndAtivoTrue(id);
        if (produto == null) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Produto com ID %s não encontrado", id));
        }
        return produto;
    }

    /**
     * Retorna registros criados, atualizados ou deletados após a última sincronização da tabela informada.
     */
    public List<ProdutoResponseDTO> buscarAlteradosDesdeSincronizacao(String tabelaName) {
        var sincronizacao = sincronizacaoService.findByTabelaName(tabelaName);
        var since = sincronizacao.getUltimaSincronizacao();
        if (since == null) {
            // se não houver data, considerar desde a época Unix para garantir consistência
            since = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
        }
        List<Produto> changed = produtoRepository.findAllChangedSince(since);
        return produtoMapper.listProdutoToListProdutoResponseDTO(changed);
    }
}
