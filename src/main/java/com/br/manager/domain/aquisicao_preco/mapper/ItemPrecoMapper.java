package com.br.manager.domain.aquisicao_preco.mapper;

import com.br.manager.domain.aquisicao_preco.dto.ItemPrecoInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemPrecoResponseDTO;
import com.br.manager.domain.aquisicao_preco.entity.ItemPreco;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ItemPrecoMapper {

    public ItemPreco precoItemInputDTOToItemPreco(ItemPrecoInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }

        ItemPreco entity = new ItemPreco();
        entity.setId(inputDTO.getId());
        entity.setProdutoId(inputDTO.getProdutoId());
        entity.setBicoId(inputDTO.getBicoId());
        entity.setPrecoUnitario(inputDTO.getPrecoUnitario());
        entity.setPrecoMinimo(inputDTO.getPrecoMinimo());
        entity.setDescontoMaximoPercentual(inputDTO.getDescontoMaximoPercentual());
        entity.setAtivo(inputDTO.getAtivo() != null ? inputDTO.getAtivo() : true);
        return entity;
    }

    public ItemPrecoResponseDTO precoItemToItemPrecoResponseDTO(ItemPreco itemPreco) {
        if (itemPreco == null) {
            return null;
        }

        ItemPrecoResponseDTO dto = new ItemPrecoResponseDTO();
        dto.setId(itemPreco.getId());
        dto.setPrecoTabelaId(itemPreco.getTabelaPreco() != null ? itemPreco.getTabelaPreco().getId() : null);
        dto.setProdutoId(itemPreco.getProdutoId());
        dto.setBicoId(itemPreco.getBicoId());
        dto.setPrecoUnitario(itemPreco.getPrecoUnitario());
        dto.setPrecoMinimo(itemPreco.getPrecoMinimo());
        dto.setDescontoMaximoPercentual(itemPreco.getDescontoMaximoPercentual());
        dto.setAtivo(itemPreco.getAtivo());
        return dto;
    }

    public List<ItemPrecoResponseDTO> listItemPrecoToListItemPrecoResponseDTO(List<ItemPreco> itensPreco) {
        if (itensPreco == null) {
            return null;
        }

        return itensPreco.stream()
                .filter(Objects::nonNull)
                .map(this::precoItemToItemPrecoResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ItemPreco> listItemPrecoInputDTOToListItemPreco(List<ItemPrecoInputDTO> inputDTOs) {
        if (inputDTOs == null) {
            return null;
        }

        return inputDTOs.stream()
                .filter(Objects::nonNull)
                .map(this::precoItemInputDTOToItemPreco)
                .collect(Collectors.toList());
    }

    public void updateItemPrecoFromDto(ItemPrecoInputDTO dto, ItemPreco entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getProdutoId() != null) {
            entity.setProdutoId(dto.getProdutoId());
        }
        if (dto.getBicoId() != null) {
            entity.setBicoId(dto.getBicoId());
        }
        if (dto.getPrecoUnitario() != null) {
            entity.setPrecoUnitario(dto.getPrecoUnitario());
        }
        if (dto.getPrecoMinimo() != null) {
            entity.setPrecoMinimo(dto.getPrecoMinimo());
        }
        if (dto.getDescontoMaximoPercentual() != null) {
            entity.setDescontoMaximoPercentual(dto.getDescontoMaximoPercentual());
        }
        if (dto.getAtivo() != null) {
            entity.setAtivo(dto.getAtivo());
        }
    }
}
