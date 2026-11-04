package com.br.manager.domain.venda_recibo.mapper;

import com.br.manager.domain.venda_recibo.dto.VendaReembolsoInputDTO;
import com.br.manager.domain.venda_recibo.dto.VendaReembolsoResponseDTO;
import com.br.manager.domain.venda_recibo.entity.ReembolsoVenda;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReembolsoVendaMapper {

    public ReembolsoVenda toEntity(VendaReembolsoInputDTO inputDTO) {
        ReembolsoVenda entity = new ReembolsoVenda();
        entity.setId(inputDTO.getId());
        entity.setMotivo(inputDTO.getMotivo());
        entity.setRequestedBy(inputDTO.getRequestedBy());
        entity.setAprovadoPor(inputDTO.getAprovadoPor());
        entity.setReembolsadoEm(inputDTO.getReembolsadoEm());
        entity.setObservacoes(inputDTO.getObservacoes());
        return entity;
    }

    public VendaReembolsoResponseDTO toResponse(ReembolsoVenda reembolso) {
        return new VendaReembolsoResponseDTO(
                reembolso.getId(), reembolso.getMotivo(), reembolso.getRequestedBy(), reembolso.getAprovadoPor(),
                reembolso.getReembolsadoEm(), reembolso.getObservacoes()
        );
    }

    public List<VendaReembolsoResponseDTO> toResponseList(List<ReembolsoVenda> reembolsos) {
        return reembolsos.stream().map(this::toResponse).toList();
    }
}
