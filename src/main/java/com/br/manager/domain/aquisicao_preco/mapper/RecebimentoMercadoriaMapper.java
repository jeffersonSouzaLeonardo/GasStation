package com.br.manager.domain.aquisicao_preco.mapper;

import com.br.manager.domain.operacional.entity.Fornecedor;
import com.br.manager.domain.organizacao.entity.Estacao;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.aquisicao_preco.dto.RecebimentoMercadoriaInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemRecebimentoMercadoriaResponseDTO;
import com.br.manager.domain.aquisicao_preco.dto.RecebimentoMercadoriaResponseDTO;
import com.br.manager.domain.aquisicao_preco.entity.RecebimentoMercadoria;
import com.br.manager.domain.aquisicao_preco.entity.RecebimentoMercadoriaItem;
import com.br.manager.domain.aquisicao_preco.entity.PedidoCompra;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class RecebimentoMercadoriaMapper {

    public RecebimentoMercadoria mercadoriaReciboInputDTOToRecebimentoMercadoria(RecebimentoMercadoriaInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }

        RecebimentoMercadoria entity = new RecebimentoMercadoria();
        entity.setId(inputDTO.getId());
        entity.setNumeroNota(inputDTO.getNumeroNota());
        entity.setChaveNota(inputDTO.getChaveNota());
        entity.setRecebidoEm(inputDTO.getRecebidoEm());
        entity.setStatus(inputDTO.getStatus());
        entity.setValorFrete(inputDTO.getValorFrete());
        entity.setOutrosCustos(inputDTO.getOutrosCustos());

        if (inputDTO.getFornecedorId() != null) {
            Fornecedor fornecedor = new Fornecedor();
            fornecedor.setId(inputDTO.getFornecedorId());
            entity.setFornecedor(fornecedor);
        }

        if (inputDTO.getCompraOrderId() != null) {
            PedidoCompra pedidoCompra = new PedidoCompra();
            pedidoCompra.setId(inputDTO.getCompraOrderId());
            entity.setPedidoCompra(pedidoCompra);
        }

        if (inputDTO.getRecebidoPor() != null) {
            Usuario usuario = new Usuario();
            usuario.setId(inputDTO.getRecebidoPor());
            entity.setRecebidoPor(usuario);
        }

        return entity;
    }

    public RecebimentoMercadoriaResponseDTO mercadoriaReciboToRecebimentoMercadoriaResponseDTO(RecebimentoMercadoria recebimentoMercadoria) {
        if (recebimentoMercadoria == null) {
            return null;
        }

        RecebimentoMercadoriaResponseDTO dto = new RecebimentoMercadoriaResponseDTO();
        dto.setId(recebimentoMercadoria.getId());
        dto.setFornecedorId(recebimentoMercadoria.getFornecedor() != null ? recebimentoMercadoria.getFornecedor().getId() : null);
        dto.setCompraOrderId(recebimentoMercadoria.getPedidoCompra() != null ? recebimentoMercadoria.getPedidoCompra().getId() : null);
        dto.setNumeroNota(recebimentoMercadoria.getNumeroNota());
        dto.setChaveNota(recebimentoMercadoria.getChaveNota());
        dto.setRecebidoEm(recebimentoMercadoria.getRecebidoEm());
        dto.setStatus(recebimentoMercadoria.getStatus());
        dto.setValorFrete(recebimentoMercadoria.getValorFrete());
        dto.setOutrosCustos(recebimentoMercadoria.getOutrosCustos());
        dto.setRecebidoPor(recebimentoMercadoria.getRecebidoPor() != null ? recebimentoMercadoria.getRecebidoPor().getId() : null);

        if (recebimentoMercadoria.getItens() != null) {
            dto.setItens(recebimentoMercadoria.getItens().stream()
                    .filter(Objects::nonNull)
                    .map(this::toItemRecebimentoMercadoriaResponseDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public List<RecebimentoMercadoriaResponseDTO> listRecebimentoMercadoriaToListRecebimentoMercadoriaResponseDTO(List<RecebimentoMercadoria> mercadoriaReceipts) {
        if (mercadoriaReceipts == null) {
            return null;
        }

        return mercadoriaReceipts.stream()
                .filter(Objects::nonNull)
                .map(this::mercadoriaReciboToRecebimentoMercadoriaResponseDTO)
                .collect(Collectors.toList());
    }

    public void updateRecebimentoMercadoriaFromDto(RecebimentoMercadoriaInputDTO dto, RecebimentoMercadoria entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getNumeroNota() != null) {
            entity.setNumeroNota(dto.getNumeroNota());
        }
        if (dto.getChaveNota() != null) {
            entity.setChaveNota(dto.getChaveNota());
        }
        if (dto.getRecebidoEm() != null) {
            entity.setRecebidoEm(dto.getRecebidoEm());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        if (dto.getValorFrete() != null) {
            entity.setValorFrete(dto.getValorFrete());
        }
        if (dto.getOutrosCustos() != null) {
            entity.setOutrosCustos(dto.getOutrosCustos());
        }
    }

    private ItemRecebimentoMercadoriaResponseDTO toItemRecebimentoMercadoriaResponseDTO(RecebimentoMercadoriaItem item) {
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
}
