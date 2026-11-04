package com.br.manager.domain.venda_recibo.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.operacional.entity.Bico;
import com.br.manager.domain.operacional.entity.Produto;
import com.br.manager.domain.operacional.entity.Tanque;
import com.br.manager.domain.operacional.repository.BicoRepository;
import com.br.manager.domain.operacional.repository.ProdutoRepository;
import com.br.manager.domain.operacional.repository.TanqueRepository;
import com.br.manager.domain.venda_recibo.dto.VendaItemInputDTO;
import com.br.manager.domain.venda_recibo.dto.VendaItemResponseDTO;
import com.br.manager.domain.venda_recibo.entity.Venda;
import com.br.manager.domain.venda_recibo.entity.VendaItem;
import com.br.manager.domain.venda_recibo.mapper.ItemVendaMapper;
import com.br.manager.domain.venda_recibo.repository.VendaItemRepository;
import com.br.manager.domain.venda_recibo.repository.VendaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class ItemVendaService {

    @Autowired
    private VendaItemRepository vendaItemRepository;
    @Autowired
    private VendaRepository vendaRepository;
    @Autowired
    private ItemVendaMapper vendaItemMapper;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private BicoRepository bicoRepository;
    @Autowired
    private TanqueRepository tanqueRepository;

    public List<VendaItemResponseDTO> saveVendaItens(UUID vendaId, List<VendaItemInputDTO> inputDTOs) {
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Venda com ID %s não encontrada", vendaId)));
        validateVendaMutable(venda);
        validateLinhaNumbers(inputDTOs);

        List<VendaItem> itens = inputDTOs.stream().map(dto -> buildVendaItem(venda, dto)).toList();
        return vendaItemMapper.toResponseList(vendaItemRepository.saveAllAndFlush(itens));
    }

    public List<VendaItemResponseDTO> findByVenda(UUID vendaId) {
        return vendaItemMapper.toResponseList(vendaItemRepository.findByVendaIdOrderByLinhaNumero(vendaId));
    }

    private VendaItem buildVendaItem(Venda venda, VendaItemInputDTO dto) {
        Produto produto = produtoRepository.findById(dto.getProdutoId())
                .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Produto com ID %s não encontrado", dto.getProdutoId())));
        if (!Boolean.TRUE.equals(produto.getAtivo())) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Produto com ID %s não encontrado", dto.getProdutoId()));
        }

        VendaItem item = vendaItemMapper.toEntity(dto);
        item.setVenda(venda);
        BigDecimal desconto = dto.getValorDesconto() == null ? BigDecimal.ZERO : dto.getValorDesconto();
        item.setValorDesconto(desconto);

        if (dto.getBicoId() != null) {
            Bico bico = bicoRepository.findById(dto.getBicoId())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Bomba com ID %s não encontrada", dto.getBicoId())));
            Tanque tanque = tanqueRepository.findById(bico.getTanqueId())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Tanque com ID %s não encontrado", bico.getTanqueId())));

            if (dto.getMedidorInicial() == null || dto.getMedidorFinal() == null) {
                throw new ExcecaoNegocio("Itens de combustível exigem leitura inicial e final do medidor.");
            }
            if (dto.getMedidorFinal().compareTo(dto.getMedidorInicial()) <= 0) {
                throw new ExcecaoNegocio("A leitura final deve ser maior que a leitura inicial.");
            }
            item.setTanqueId(tanque.getId());
            item.setQuantidade(dto.getMedidorFinal().subtract(dto.getMedidorInicial()));
        } else {
            if (dto.getQuantidade() == null || dto.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ExcecaoNegocio("A quantidade do item deve ser maior que zero.");
            }
            item.setQuantidade(dto.getQuantidade());
        }

        if (dto.getPrecoUnitario() == null || dto.getPrecoUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ExcecaoNegocio("O preço unidadeário deve ser maior que zero.");
        }
        return item;
    }

    private void validateVendaMutable(Venda venda) {
        if (venda.estaBloqueadaFinanceiramente()) {
            throw new ExcecaoNegocio("Vendas pagas ou com crédito pendente não podem ser alteradas.");
        }
    }

    private void validateLinhaNumbers(List<VendaItemInputDTO> inputDTOs) {
        Set<Integer> lines = new HashSet<>();
        for (VendaItemInputDTO dto : inputDTOs) {
            if (!lines.add(dto.getLinhaNumero())) {
                throw new ExcecaoNegocio("Os números de linha dos itens da venda devem ser únicos.");
            }
        }
    }
}
