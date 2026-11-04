package com.br.manager.domain.aquisicao_preco.mapper;

import com.br.manager.domain.aquisicao_preco.dto.ItemRecebimentoMercadoriaInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemRecebimentoMercadoriaResponseDTO;
import com.br.manager.domain.aquisicao_preco.entity.RecebimentoMercadoria;
import com.br.manager.domain.aquisicao_preco.entity.RecebimentoMercadoriaItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ItemRecebimentoMercadoriaMapper {

    public RecebimentoMercadoriaItem mercadoriaReciboItemInputDTOToRecebimentoMercadoriaItem(ItemRecebimentoMercadoriaInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }

        RecebimentoMercadoriaItem entity = new RecebimentoMercadoriaItem();
        entity.setId(inputDTO.getId());
        entity.setProdutoId(inputDTO.getProdutoId());
        entity.setTanqueId(inputDTO.getTanqueId());
        entity.setQuantidade(inputDTO.getQuantidade());
        entity.setCustoUnitario(inputDTO.getCustoUnitario());
        return entity;
    }

    public ItemRecebimentoMercadoriaResponseDTO mercadoriaReciboItemToItemRecebimentoMercadoriaResponseDTO(RecebimentoMercadoriaItem item) {
        if (item == null) {
            return null;
        }

        ItemRecebimentoMercadoriaResponseDTO dto = new ItemRecebimentoMercadoriaResponseDTO();
        dto.setId(item.getId());
        dto.setMercadoriaReciboId(item.getRecebimentoMercadoria() != null ? item.getRecebimentoMercadoria().getId() : null);
        dto.setProdutoId(item.getProdutoId());
        dto.setTanqueId(item.getTanqueId());
        dto.setQuantidade(item.getQuantidade());
        dto.setCustoUnitario(item.getCustoUnitario());
        return dto;
    }

    public List<ItemRecebimentoMercadoriaResponseDTO> listRecebimentoMercadoriaItemToListItemRecebimentoMercadoriaResponseDTO(List<RecebimentoMercadoriaItem> itens) {
        if (itens == null) {
            return null;
        }

        return itens.stream()
                .filter(Objects::nonNull)
                .map(this::mercadoriaReciboItemToItemRecebimentoMercadoriaResponseDTO)
                .collect(Collectors.toList());
    }

    public void updateRecebimentoMercadoriaItemFromDto(ItemRecebimentoMercadoriaInputDTO dto, RecebimentoMercadoriaItem entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getProdutoId() != null) {
            entity.setProdutoId(dto.getProdutoId());
        }
        if (dto.getTanqueId() != null) {
            entity.setTanqueId(dto.getTanqueId());
        }
        if (dto.getQuantidade() != null) {
            entity.setQuantidade(dto.getQuantidade());
        }
        if (dto.getCustoUnitario() != null) {
            entity.setCustoUnitario(dto.getCustoUnitario());
        }
    }
}
