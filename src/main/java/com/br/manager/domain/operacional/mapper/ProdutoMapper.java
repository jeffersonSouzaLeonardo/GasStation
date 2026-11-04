package com.br.manager.domain.operacional.mapper;

import com.br.manager.domain.operacional.dto.ProdutoInputDTO;
import com.br.manager.domain.operacional.dto.ProdutoResponseDTO;
import com.br.manager.domain.operacional.entity.Produto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProdutoMapper {
    public Produto produtoInputDTOToProduto(ProdutoInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        Produto entity = new Produto();
        BeanUtils.copyProperties(inputDTO, entity);
        return entity;
    }

    public ProdutoResponseDTO produtoToProdutoResponseDTO(Produto produto) {
        if (produto == null) {
            return null;
        }
        ProdutoResponseDTO dto = new ProdutoResponseDTO();
        BeanUtils.copyProperties(produto, dto);
        return dto;
    }

    public List<ProdutoResponseDTO> listProdutoToListProdutoResponseDTO(List<Produto> produtos) {
        if (produtos == null) {
            return null;
        }
        return produtos.stream().map(this::produtoToProdutoResponseDTO).toList();
    }

    public void updateProdutoFromDto(ProdutoInputDTO dto, Produto entity) {
        if (dto == null || entity == null) {
            return;
        }
        BeanUtils.copyProperties(dto, entity);
    }
}
