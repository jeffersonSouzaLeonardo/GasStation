package com.br.manager.domain.venda_recibo.mapper;

import com.br.manager.domain.venda_recibo.dto.VendaItemResponseDTO;
import com.br.manager.domain.venda_recibo.dto.VendaPagamentoResponseDTO;
import com.br.manager.domain.venda_recibo.dto.VendaReembolsoResponseDTO;
import com.br.manager.domain.venda_recibo.dto.VendaResponseDTO;
import com.br.manager.domain.venda_recibo.entity.Venda;
import com.br.manager.domain.venda_recibo.entity.VendaItem;
import com.br.manager.domain.venda_recibo.entity.PagamentoVenda;
import com.br.manager.domain.venda_recibo.entity.ReembolsoVenda;
import org.springframework.stereotype.Component;

@Component
public class VendaMapper {

    public VendaResponseDTO toResponse(Venda venda) {
        return new VendaResponseDTO(
                venda.getId(),
                venda.getEstacaoId(),
                venda.getTurnoId(),
                venda.getNumeroVenda(),
                venda.getStatus(),
                venda.getVendidoEm(),
                venda.getClienteId(),
                venda.getVeiculoId(),
                venda.getFrentistaUsuarioId(),
                venda.getCaixeiroUsuarioId(),
                venda.getSubtotal(),
                venda.getValorDesconto(),
                venda.getSituacaoFiscal(),
                venda.getObservacoes(),
                venda.getItens().stream().map(this::toItemResponse).toList(),
                venda.getPagamentos().stream().map(this::toPagamentoResponse).toList(),
                venda.getReembolsos().stream().map(this::toReembolsoResponse).toList()
        );
    }

    public VendaItemResponseDTO toItemResponse(VendaItem item) {
        return new VendaItemResponseDTO(
                item.getId(),
                item.getLinhaNumero(),
                item.getProdutoId(),
                item.getBicoId(),
                item.getTanqueId(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                item.getValorDesconto(),
                item.getMedidorInicial(),
                item.getMedidorFinal()
        );
    }

    public VendaPagamentoResponseDTO toPagamentoResponse(PagamentoVenda pagamento) {
        return new VendaPagamentoResponseDTO(
                pagamento.getId(),
                pagamento.getPagamentoMethodId(),
                pagamento.getValor(),
                pagamento.getStatus(),
                pagamento.getReferenciaTransacao(),
                pagamento.getAutorizadoEm(),
                pagamento.getRecebidoEm()
        );
    }

    public VendaReembolsoResponseDTO toReembolsoResponse(ReembolsoVenda reembolso) {
        return new VendaReembolsoResponseDTO(
                reembolso.getId(),
                reembolso.getMotivo(),
                reembolso.getRequestedBy(),
                reembolso.getAprovadoPor(),
                reembolso.getReembolsadoEm(),
                reembolso.getObservacoes()
        );
    }

}
