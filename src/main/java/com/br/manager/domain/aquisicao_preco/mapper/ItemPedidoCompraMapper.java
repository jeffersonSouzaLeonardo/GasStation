package com.br.manager.domain.aquisicao_preco.mapper;

import com.br.manager.domain.aquisicao_preco.dto.ItemPedidoCompraInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemPedidoCompraResponseDTO;
import com.br.manager.domain.aquisicao_preco.entity.PedidoCompra;
import com.br.manager.domain.aquisicao_preco.entity.PedidoCompraItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ItemPedidoCompraMapper {

    public PedidoCompraItem toEntity(ItemPedidoCompraInputDTO dto) {
        if (dto == null) {
            return null;
        }

        PedidoCompraItem entity = new PedidoCompraItem();
        entity.setId(dto.getId());

        PedidoCompra pedidoCompra = new PedidoCompra();
        pedidoCompra.setId(dto.getCompraOrderId());
        entity.setPedidoCompra(pedidoCompra);

        entity.setProdutoId(dto.getProdutoId());
        entity.setQuantidade(dto.getQuantidade());
        entity.setCustoUnitario(dto.getCustoUnitario());
        return entity;
    }

    public ItemPedidoCompraResponseDTO toResponseDTO(PedidoCompraItem item) {
        if (item == null) {
            return null;
        }

        ItemPedidoCompraResponseDTO dto = new ItemPedidoCompraResponseDTO();
        dto.setId(item.getId());
        dto.setCompraOrderId(item.getPedidoCompra() != null ? item.getPedidoCompra().getId() : null);
        dto.setProdutoId(item.getProdutoId());
        dto.setQuantidade(item.getQuantidade());
        dto.setCustoUnitario(item.getCustoUnitario());
        dto.setCustoTotal(item.getCustoTotal());
        return dto;
    }

    public List<ItemPedidoCompraResponseDTO> toResponseDTOList(List<PedidoCompraItem> itens) {
        if (itens == null) {
            return null;
        }

        return itens.stream()
                .filter(Objects::nonNull)
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public void updateEntityFromDto(ItemPedidoCompraInputDTO dto, PedidoCompraItem entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getProdutoId() != null) {
            entity.setProdutoId(dto.getProdutoId());
        }
        if (dto.getQuantidade() != null) {
            entity.setQuantidade(dto.getQuantidade());
        }
        if (dto.getCustoUnitario() != null) {
            entity.setCustoUnitario(dto.getCustoUnitario());
        }
    }
}
