package com.br.manager.domain.operacional.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.operacional.dto.EstacaoProdutoInputDTO;
import com.br.manager.domain.operacional.dto.EstacaoProdutoResponseDTO;
import com.br.manager.domain.operacional.entity.EstacaoProduto;
import com.br.manager.domain.operacional.mapper.EstacaoProdutoMapper;
import com.br.manager.domain.operacional.repository.EstacaoProdutoRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.br.manager.domain.comum.UtilStrings;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EstacaoProdutoService {

    @Autowired
    private EstacaoProdutoRepository estacaoProdutoRepository;

    @Autowired
    private EstacaoProdutoMapper estacaoProdutoMapper;

    public EstacaoProdutoResponseDTO create(EstacaoProdutoInputDTO inputDTO) {
        try {
            EstacaoProduto entity = estacaoProdutoMapper.estacaoProdutoInputDTOToEstacaoProduto(inputDTO);
            if (entity.getId() == null) {
                            }
            return estacaoProdutoMapper.estacaoProdutoToEstacaoProdutoResponseDTO(estacaoProdutoRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (Exception e) {
            String produtoLabel = inputDTO != null && inputDTO.getProdutoId() != null ? inputDTO.getProdutoId().toString() : "";
            throw new ExcecaoNegocio("Erro ao salvar produto do posto " + produtoLabel, e);
        }
    }

    public EstacaoProdutoResponseDTO update(EstacaoProdutoInputDTO inputDTO) {
        try {
            EstacaoProduto entity = estacaoProdutoRepository.findByIdAndAtivoTrue(inputDTO.getId());
            if (entity == null) {
                throw new ExcecaoNegocioNaoEncontrado(String.format("Produto do posto com ID %s não encontrado", inputDTO.getId()));
            }
            estacaoProdutoMapper.updateEstacaoProdutoFromDto(inputDTO, entity);
            return estacaoProdutoMapper.estacaoProdutoToEstacaoProdutoResponseDTO(estacaoProdutoRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            String produtoLabel = inputDTO != null && inputDTO.getProdutoId() != null ? inputDTO.getProdutoId().toString() : "";
            throw new ExcecaoNegocio("Erro ao salvar produto do posto " + produtoLabel, e);
        }
    }

    public List<EstacaoProdutoResponseDTO> findAll() {
        return estacaoProdutoMapper.listEstacaoProdutoToListEstacaoProdutoResponseDTO(estacaoProdutoRepository.findAllByAtivoTrue());
    }

    public List<EstacaoProdutoResponseDTO> findByDescricao(String descricao) {
        if (!UtilStrings.hasText(descricao)) {
            return findAll();
        }
        try {
            UUID produtoId = UUID.fromString(descricao);
            return estacaoProdutoMapper.listEstacaoProdutoToListEstacaoProdutoResponseDTO(
                    estacaoProdutoRepository.findByProdutoIdAndAtivoTrue(produtoId));
        } catch (IllegalArgumentException exception) {
            return List.of();
        }
    }

    public EstacaoProdutoResponseDTO find(UUID id) {
        EstacaoProduto entity = estacaoProdutoRepository.findByIdAndAtivoTrue(id);
        if (entity == null) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Produto do posto com ID %s não encontrado", id));
        }
        return estacaoProdutoMapper.estacaoProdutoToEstacaoProdutoResponseDTO(entity);
    }

    public void delete(UUID id) {
        try {
            EstacaoProduto entity = estacaoProdutoRepository.findById(id)
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Produto do posto com ID %s não encontrado", id)));
            entity.setAtivo(false);
            estacaoProdutoRepository.saveAndFlush(entity);
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao excluir produto do posto", e);
        }
    }

    public EstacaoProduto getEstacaoProdutoEntityById(UUID id) {
        EstacaoProduto entity = estacaoProdutoRepository.findByIdAndAtivoTrue(id);
        if (entity == null) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Produto do posto com ID %s não encontrado", id));
        }
        return entity;
    }
}
