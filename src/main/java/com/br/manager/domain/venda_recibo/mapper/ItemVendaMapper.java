package com.br.manager.domain.venda_recibo.mapper;

import com.br.manager.domain.venda_recibo.dto.VendaItemInputDTO;
import com.br.manager.domain.venda_recibo.dto.VendaItemResponseDTO;
import com.br.manager.domain.venda_recibo.entity.VendaItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemVendaMapper {

    public VendaItem toEntity(VendaItemInputDTO inputDTO) {
        VendaItem entity = new VendaItem();
        entity.setId(inputDTO.getId());
        entity.setLinhaNumero(inputDTO.getLinhaNumero());
        entity.setProdutoId(inputDTO.getProdutoId());
        entity.setBicoId(inputDTO.getBicoId());
        entity.setTanqueId(inputDTO.getTanqueId());
        entity.setQuantidade(inputDTO.getQuantidade());
        entity.setPrecoUnitario(inputDTO.getPrecoUnitario());
        entity.setValorDesconto(inputDTO.getValorDesconto());
        entity.setMedidorInicial(inputDTO.getMedidorInicial());
        entity.setMedidorFinal(inputDTO.getMedidorFinal());
        return entity;
    }

    public VendaItemResponseDTO toResponse(VendaItem item) {
        return new VendaItemResponseDTO(
                item.getId(), item.getLinhaNumero(), item.getProdutoId(), item.getBicoId(), item.getTanqueId(),
                item.getQuantidade(), item.getPrecoUnitario(), item.getValorDesconto(),
                item.getMedidorInicial(), item.getMedidorFinal()
        );
    }

    public List<VendaItemResponseDTO> toResponseList(List<VendaItem> itens) {
        return itens.stream().map(this::toResponse).toList();
    }
}
