package com.br.manager.domain.venda_recibo.mapper;

import com.br.manager.domain.venda_recibo.dto.VendaPagamentoInputDTO;
import com.br.manager.domain.venda_recibo.dto.VendaPagamentoResponseDTO;
import com.br.manager.domain.venda_recibo.entity.PagamentoVenda;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PagamentoVendaMapper {

    public PagamentoVenda toEntity(VendaPagamentoInputDTO inputDTO) {
        PagamentoVenda entity = new PagamentoVenda();
        entity.setId(inputDTO.getId());
        entity.setPagamentoMethodId(inputDTO.getPagamentoMethodId());
        entity.setValor(inputDTO.getValor());
        entity.setStatus(inputDTO.getStatus());
        entity.setReferenciaTransacao(inputDTO.getReferenciaTransacao());
        entity.setAutorizadoEm(inputDTO.getAutorizadoEm());
        entity.setRecebidoEm(inputDTO.getRecebidoEm());
        return entity;
    }

    public VendaPagamentoResponseDTO toResponse(PagamentoVenda pagamento) {
        return new VendaPagamentoResponseDTO(
                pagamento.getId(), pagamento.getPagamentoMethodId(), pagamento.getValor(), pagamento.getStatus(),
                pagamento.getReferenciaTransacao(), pagamento.getAutorizadoEm(), pagamento.getRecebidoEm()
        );
    }

    public List<VendaPagamentoResponseDTO> toResponseList(List<PagamentoVenda> pagamentos) {
        return pagamentos.stream().map(this::toResponse).toList();
    }
}
