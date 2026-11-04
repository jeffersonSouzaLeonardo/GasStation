package com.br.manager.domain.aquisicao_preco.mapper;

import com.br.manager.domain.operacional.entity.Fornecedor;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.aquisicao_preco.dto.PedidoCompraInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemPedidoCompraResponseDTO;
import com.br.manager.domain.aquisicao_preco.dto.PedidoCompraResponseDTO;
import com.br.manager.domain.aquisicao_preco.entity.PedidoCompra;
import com.br.manager.domain.aquisicao_preco.entity.PedidoCompraItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class PedidoCompraMapper {

    public PedidoCompra toEntity(PedidoCompraInputDTO dto) {
        if (dto == null) {
            return null;
        }

        PedidoCompra pedidoCompra = new PedidoCompra();
        pedidoCompra.setId(dto.getId());
        pedidoCompra.setNumero(dto.getNumero());
        pedidoCompra.setStatus(dto.getStatus() != null ? dto.getStatus() : PedidoCompra.Situacao.DRAFT);
        pedidoCompra.setPedidoEm(dto.getPedidoEm());
        pedidoCompra.setPrevistoEm(dto.getPrevistoEm());

        if (dto.getCriadoPor() != null) {
            Usuario usuario = new Usuario();
            usuario.setId(dto.getCriadoPor());
            pedidoCompra.setCriadoPor(usuario);
        }

        pedidoCompra.setItens(new java.util.ArrayList<>());

        return pedidoCompra;
    }

    public PedidoCompraResponseDTO toResponseDTO(PedidoCompra pedidoCompra) {
        if (pedidoCompra == null) {
            return null;
        }

        PedidoCompraResponseDTO dto = new PedidoCompraResponseDTO();
        dto.setId(pedidoCompra.getId());
        dto.setFornecedorId(pedidoCompra.getFornecedor() != null ? pedidoCompra.getFornecedor().getId() : null);
        dto.setNumero(pedidoCompra.getNumero());
        dto.setStatus(pedidoCompra.getStatus());
        dto.setPedidoEm(pedidoCompra.getPedidoEm());
        dto.setPrevistoEm(pedidoCompra.getPrevistoEm());
        dto.setCriadoPor(pedidoCompra.getCriadoPor() != null ? pedidoCompra.getCriadoPor().getId() : null);
        dto.setValorTotal(pedidoCompra.getValorTotal());

        if (pedidoCompra.getItens() != null) {
            dto.setItens(pedidoCompra.getItens().stream()
                    .filter(Objects::nonNull)
                    .map(this::toItemResponseDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public ItemPedidoCompraResponseDTO toItemResponseDTO(PedidoCompraItem item) {
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
}
