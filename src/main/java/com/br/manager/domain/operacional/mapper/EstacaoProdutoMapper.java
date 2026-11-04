package com.br.manager.domain.operacional.mapper;

import com.br.manager.domain.operacional.dto.EstacaoProdutoInputDTO;
import com.br.manager.domain.operacional.dto.EstacaoProdutoResponseDTO;
import com.br.manager.domain.operacional.entity.EstacaoProduto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EstacaoProdutoMapper {
    public EstacaoProduto estacaoProdutoInputDTOToEstacaoProduto(EstacaoProdutoInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        EstacaoProduto entity = new EstacaoProduto();
        BeanUtils.copyProperties(inputDTO, entity);
        return entity;
    }

    public EstacaoProdutoResponseDTO estacaoProdutoToEstacaoProdutoResponseDTO(EstacaoProduto entity) {
        if (entity == null) {
            return null;
        }
        EstacaoProdutoResponseDTO dto = new EstacaoProdutoResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public List<EstacaoProdutoResponseDTO> listEstacaoProdutoToListEstacaoProdutoResponseDTO(List<EstacaoProduto> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(this::estacaoProdutoToEstacaoProdutoResponseDTO).toList();
    }

    public void updateEstacaoProdutoFromDto(EstacaoProdutoInputDTO dto, EstacaoProduto entity) {
        if (dto == null || entity == null) {
            return;
        }
        BeanUtils.copyProperties(dto, entity);
    }
}
