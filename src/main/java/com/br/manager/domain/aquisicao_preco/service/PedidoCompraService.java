package com.br.manager.domain.aquisicao_preco.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.operacional.entity.Fornecedor;
import com.br.manager.domain.operacional.repository.FornecedorRepository;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.organizacao.repository.UsuarioRepository;
import com.br.manager.domain.aquisicao_preco.dto.PedidoCompraInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemPedidoCompraInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.PedidoCompraResponseDTO;
import com.br.manager.domain.aquisicao_preco.entity.PedidoCompra;
import com.br.manager.domain.aquisicao_preco.mapper.PedidoCompraMapper;
import com.br.manager.domain.aquisicao_preco.repository.ItemPedidoCompraRepository;
import com.br.manager.domain.aquisicao_preco.repository.PedidoCompraRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PedidoCompraService {

    @Autowired
    private PedidoCompraRepository compraOrderRepository;

    @Autowired
    private ItemPedidoCompraRepository compraOrderItemRepository;

    @Autowired
    private PedidoCompraMapper compraOrderMapper;

    @Autowired
    private ItemPedidoCompraService compraOrderItemService;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public PedidoCompraResponseDTO create(PedidoCompraInputDTO inputDTO) {
        validateCreate(inputDTO);

        try {
            PedidoCompra pedidoCompra = compraOrderMapper.toEntity(inputDTO);
            pedidoCompra.setId(null);
            pedidoCompra.setStatus(PedidoCompra.Situacao.DRAFT);
            pedidoCompra.setPedidoEm(inputDTO.getPedidoEm() != null ? inputDTO.getPedidoEm() : LocalDateTime.now());
            pedidoCompra.setPrevistoEm(inputDTO.getPrevistoEm());

            Fornecedor fornecedor = fornecedorRepository.findById(inputDTO.getFornecedorId())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Fornecedor com ID %s não encontrado", inputDTO.getFornecedorId())));
            if (!Boolean.TRUE.equals(fornecedor.getAtivo())) {
                throw new ExcecaoNegocioNaoEncontrado(String.format("Fornecedor com ID %s não encontrado", inputDTO.getFornecedorId()));
            }
            pedidoCompra.setFornecedor(fornecedor);
            if (inputDTO.getCriadoPor() != null) {
                Usuario criadoPor = usuarioRepository.findById(inputDTO.getCriadoPor())
                        .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Usuário com ID %s não encontrado", inputDTO.getCriadoPor())));
                if (!Boolean.TRUE.equals(criadoPor.getAtivo())) {
                    throw new ExcecaoNegocioNaoEncontrado(String.format("Usuário com ID %s não encontrado", inputDTO.getCriadoPor()));
                }
                pedidoCompra.setCriadoPor(criadoPor);
            }

            PedidoCompra saved = compraOrderRepository.saveAndFlush(pedidoCompra);
            // save itens via service
            compraOrderItemService.savePedidoCompraItens(saved.getId(), inputDTO.getItens());
            saved.setItens(compraOrderItemRepository.findByPedidoCompraId(saved.getId()));
            return compraOrderMapper.toResponseDTO(saved);
        } catch (jakarta.validation.ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao salvar pedido de compra", e);
        }
    }

    public PedidoCompraResponseDTO findById(UUID id) {
        PedidoCompra pedidoCompra = compraOrderRepository.findById(id)
                .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Pedido de compra com ID %s não encontrado", id)));
        pedidoCompra.setItens(compraOrderItemRepository.findByPedidoCompraId(id));
        return compraOrderMapper.toResponseDTO(pedidoCompra);
    }

    public List<PedidoCompraResponseDTO> findAll() {
        return compraOrderRepository.findAll().stream()
                .peek(order -> order.setItens(compraOrderItemRepository.findByPedidoCompraId(order.getId())))
                .map(compraOrderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PedidoCompraResponseDTO update(PedidoCompraInputDTO inputDTO) {
        try {
            UUID id = inputDTO.getId();
            PedidoCompra pedidoCompra = compraOrderRepository.findById(id)
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Pedido de compra com ID %s não encontrado", id)));

            if (inputDTO == null) {
                throw new ExcecaoNegocio("O payload do pedido de compra é obrigatório.");
            }

            if (inputDTO.getNumero() != null && !inputDTO.getNumero().isBlank()) {
                pedidoCompra.setNumero(inputDTO.getNumero());
            }
            if (inputDTO.getPedidoEm() != null) {
                pedidoCompra.setPedidoEm(inputDTO.getPedidoEm());
            }
            if (inputDTO.getPrevistoEm() != null) {
                pedidoCompra.setPrevistoEm(inputDTO.getPrevistoEm());
            }
            if (inputDTO.getStatus() != null && !inputDTO.getStatus().isBlank()) {
                pedidoCompra.setStatus(inputDTO.getStatus());
            }

            PedidoCompra saved = compraOrderRepository.saveAndFlush(pedidoCompra);

            if (inputDTO.getItens() != null) {
                // replace itens using service
                compraOrderItemService.replaceByPedidoCompra(saved, inputDTO.getItens());
                saved.setItens(compraOrderItemRepository.findByPedidoCompraId(saved.getId()));
            }

            return compraOrderMapper.toResponseDTO(saved);
        } catch (jakarta.validation.ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao atualizar pedido de compra", e);
        }
    }

    @Transactional
    public PedidoCompraResponseDTO approve(UUID id) {
        PedidoCompra pedidoCompra = getObrigatorioEntity(id);
        validateSituacaoTransition(pedidoCompra, PedidoCompra.Situacao.DRAFT, PedidoCompra.Situacao.APROVADO);
        pedidoCompra.setStatus(PedidoCompra.Situacao.APROVADO);
        return compraOrderMapper.toResponseDTO(compraOrderRepository.saveAndFlush(pedidoCompra));
    }

    @Transactional
    public PedidoCompraResponseDTO send(UUID id) {
        PedidoCompra pedidoCompra = getObrigatorioEntity(id);
        validateSituacaoTransition(pedidoCompra, PedidoCompra.Situacao.APROVADO, PedidoCompra.Situacao.SENT);
        pedidoCompra.setStatus(PedidoCompra.Situacao.SENT);
        return compraOrderMapper.toResponseDTO(compraOrderRepository.saveAndFlush(pedidoCompra));
    }

    @Transactional
    public PedidoCompraResponseDTO complete(UUID id) {
        PedidoCompra pedidoCompra = getObrigatorioEntity(id);
        validateSituacaoTransition(pedidoCompra, PedidoCompra.Situacao.SENT, PedidoCompra.Situacao.COMPLETED);
        pedidoCompra.setStatus(PedidoCompra.Situacao.COMPLETED);
        return compraOrderMapper.toResponseDTO(compraOrderRepository.saveAndFlush(pedidoCompra));
    }

    @Transactional
    public PedidoCompraResponseDTO cancel(UUID id) {
        PedidoCompra pedidoCompra = getObrigatorioEntity(id);
        if (!List.of(PedidoCompra.Situacao.DRAFT, PedidoCompra.Situacao.APROVADO, PedidoCompra.Situacao.SENT).contains(pedidoCompra.getStatus())) {
            throw new ExcecaoNegocio("Apenas pedidos em rascunho, aprovados ou enviados podem ser cancelados.");
        }
        pedidoCompra.setStatus(PedidoCompra.Situacao.CANCELADO);
        return compraOrderMapper.toResponseDTO(compraOrderRepository.saveAndFlush(pedidoCompra));
    }

    @Transactional
    public void delete(UUID id) {
        PedidoCompra pedidoCompra = getObrigatorioEntity(id);
        compraOrderRepository.delete(pedidoCompra);
    }

    private PedidoCompra getObrigatorioEntity(UUID id) {
        return compraOrderRepository.findById(id)
                .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Pedido de compra com ID %s não encontrado", id)));
    }

    private void saveItensForPedidoCompra(UUID compraOrderId, List<ItemPedidoCompraInputDTO> itens) {
        if (itens == null || itens.isEmpty()) {
            return;
        }

        itens.stream()
                .filter(Objects::nonNull)
                .forEach(itemDto -> {
                    itemDto.setCompraOrderId(compraOrderId);
                    compraOrderItemService.create(itemDto);
                });
    }

    private void validateCreate(PedidoCompraInputDTO inputDTO) {
        if (inputDTO == null) {
            throw new ExcecaoNegocio("O payload do pedido de compra é obrigatório.");
        }
        if (inputDTO.getFornecedorId() == null) {
            throw new ExcecaoNegocio("O ID do fornecedor é obrigatório.");
        }
        if (inputDTO.getCriadoPor() == null) {
            throw new ExcecaoNegocio("O usuário criador é obrigatório.");
        }
        if (inputDTO.getNumero() == null || inputDTO.getNumero().isBlank()) {
            throw new ExcecaoNegocio("O número do pedido de compra é obrigatório.");
        }
        if (inputDTO.getPrevistoEm() == null) {
            throw new ExcecaoNegocio("A data prevista é obrigatória.");
        }
        if (inputDTO.getItens() == null || inputDTO.getItens().isEmpty()) {
            throw new ExcecaoNegocio("O pedido de compra deve conter pelo menos um item.");
        }
    }

    private void validateSituacaoTransition(PedidoCompra pedidoCompra, String esperadoSituacao, String nextSituacao) {
        if (!Objects.equals(pedidoCompra.getStatus(), esperadoSituacao)) {
            throw new ExcecaoNegocio(String.format("O pedido de compra só pode mudar de %s para %s.", esperadoSituacao, nextSituacao));
        }
    }
}
