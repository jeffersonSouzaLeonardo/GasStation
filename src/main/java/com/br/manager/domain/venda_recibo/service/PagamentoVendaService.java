package com.br.manager.domain.venda_recibo.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.operacional.entity.MetodoPagamento;
import com.br.manager.domain.operacional.enums.TipoMetodoPagamentoEnum;
import com.br.manager.domain.operacional.repository.MetodoPagamentoRepository;
import com.br.manager.domain.venda_recibo.dto.VendaPagamentoInputDTO;
import com.br.manager.domain.venda_recibo.dto.VendaPagamentoResponseDTO;
import com.br.manager.domain.venda_recibo.entity.Venda;
import com.br.manager.domain.venda_recibo.entity.PagamentoVenda;
import com.br.manager.domain.venda_recibo.enums.SituacaoPagamento;
import com.br.manager.domain.venda_recibo.enums.VendaSituacao;
import com.br.manager.domain.venda_recibo.mapper.PagamentoVendaMapper;
import com.br.manager.domain.venda_recibo.repository.VendaPagamentoRepository;
import com.br.manager.domain.venda_recibo.repository.VendaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class PagamentoVendaService {

    private static final Set<SituacaoPagamento> SETTLED_STATUSES = Set.of(SituacaoPagamento.AUTORIZADO, SituacaoPagamento.RECEBIDO);

    @Autowired
    private VendaPagamentoRepository vendaPagamentoRepository;
    @Autowired
    private VendaRepository vendaRepository;
    @Autowired
    private MetodoPagamentoRepository pagamentoMethodRepository;
    @Autowired
    private PagamentoVendaMapper vendaPagamentoMapper;

    public VendaPagamentoResponseDTO create(VendaPagamentoInputDTO inputDTO) {
        try {
            Venda venda = vendaRepository.findDetailedById(inputDTO.getVendaId())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Venda com ID %s não encontrada", inputDTO.getVendaId())));
            if (venda.estaBloqueadaFinanceiramente()) {
                throw new ExcecaoNegocio("Vendas pagas ou com crédito pendente não podem ser alteradas.");
            }

            MetodoPagamento metodoPagamento = pagamentoMethodRepository.findById(inputDTO.getPagamentoMethodId())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Forma de pagamento com ID %s não encontrada", inputDTO.getPagamentoMethodId())));
            if (!Boolean.TRUE.equals(metodoPagamento.getAtivo())) {
                throw new ExcecaoNegocioNaoEncontrado(String.format("Forma de pagamento com ID %s não encontrada", inputDTO.getPagamentoMethodId()));
            }
            if (inputDTO.getValor() == null || inputDTO.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ExcecaoNegocio("O valor do pagamento deve ser maior que zero.");
            }
            if (Boolean.TRUE.equals(metodoPagamento.getExigeReferencia())
                    && (inputDTO.getReferenciaTransacao() == null || inputDTO.getReferenciaTransacao().isBlank())) {
                throw new ExcecaoNegocio("A referência da transação é obrigatória para a forma de pagamento selecionada.");
            }
            if (isCreditoPagamento(metodoPagamento) && (venda.getClienteId() == null || venda.getVeiculoId() == null)) {
                throw new ExcecaoNegocio("Cliente e veículo são obrigatórios para pagamentos a crédito ou convênio.");
            }

            PagamentoVenda pagamento = vendaPagamentoMapper.toEntity(inputDTO);
            pagamento.setId(null);
            pagamento.setVenda(venda);
            PagamentoVenda savedPagamento = vendaPagamentoRepository.saveAndFlush(pagamento);

            BigDecimal settledValor = vendaPagamentoRepository.findByVendaId(venda.getId()).stream()
                    .filter(item -> SETTLED_STATUSES.contains(item.getStatus()))
                    .map(PagamentoVenda::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (isCreditoPagamento(metodoPagamento)) {
                venda.setStatus(VendaSituacao.CREDITO_PENDENTE);
            } else {
                venda.setStatus(VendaSituacao.ABERTO);
            }
            vendaRepository.saveAndFlush(venda);
            return vendaPagamentoMapper.toResponse(savedPagamento);
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao salvar pagamento da venda", e);
        }
    }

    public List<VendaPagamentoResponseDTO> findByVenda(UUID vendaId) {
        return vendaPagamentoMapper.toResponseList(vendaPagamentoRepository.findByVendaId(vendaId));
    }

    public VendaPagamentoResponseDTO find(UUID id) {
        PagamentoVenda pagamento = vendaPagamentoRepository.findById(id)
                .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Pagamento da venda com ID %s não encontrado", id)));
        return vendaPagamentoMapper.toResponse(pagamento);
    }

    private boolean isCreditoPagamento(MetodoPagamento metodoPagamento) {
        return metodoPagamento.getTipo() == TipoMetodoPagamentoEnum.CONVENIO
                || metodoPagamento.getDiasLiquidacao() != null && metodoPagamento.getDiasLiquidacao() > 0;
    }
}
