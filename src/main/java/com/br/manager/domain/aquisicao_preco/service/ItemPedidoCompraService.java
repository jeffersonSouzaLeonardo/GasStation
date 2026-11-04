package com.br.manager.domain.aquisicao_preco.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.aquisicao_preco.dto.ItemPedidoCompraInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemPedidoCompraResponseDTO;
import com.br.manager.domain.aquisicao_preco.entity.PedidoCompra;
import com.br.manager.domain.aquisicao_preco.entity.PedidoCompraItem;
import com.br.manager.domain.aquisicao_preco.mapper.ItemPedidoCompraMapper;
import com.br.manager.domain.aquisicao_preco.repository.ItemPedidoCompraRepository;
import com.br.manager.domain.aquisicao_preco.repository.PedidoCompraRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ItemPedidoCompraService {

    @Autowired
    private ItemPedidoCompraRepository compraOrderItemRepository;

    @Autowired
    private PedidoCompraRepository compraOrderRepository;

    @Autowired
    private ItemPedidoCompraMapper compraOrderItemMapper;

    public ItemPedidoCompraResponseDTO create(ItemPedidoCompraInputDTO inputDTO) {
        try {
            validatePayload(inputDTO);

            PedidoCompra pedidoCompra = compraOrderRepository.findById(inputDTO.getCompraOrderId())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Pedido de compra com ID %s não encontrado", inputDTO.getCompraOrderId())));
            PedidoCompraItem entity = compraOrderItemMapper.toEntity(inputDTO);
            entity.setPedidoCompra(pedidoCompra);
            return compraOrderItemMapper.toResponseDTO(compraOrderItemRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao criar item do pedido de compra", e);
        }
    }

    public ItemPedidoCompraResponseDTO update(ItemPedidoCompraInputDTO inputDTO) {
        try {
            if (inputDTO == null || inputDTO.getId() == null) {
                throw new ExcecaoNegocio("O ID do item do pedido de compra é obrigatório para atualização.");
            }

            PedidoCompraItem entity = compraOrderItemRepository.findById(inputDTO.getId())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Item do pedido de compra com ID %s não encontrado", inputDTO.getId())));

            compraOrderItemMapper.updateEntityFromDto(inputDTO, entity);

            if (inputDTO.getCompraOrderId() != null) {
                PedidoCompra pedidoCompra = compraOrderRepository.findById(inputDTO.getCompraOrderId())
                        .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Pedido de compra com ID %s não encontrado", inputDTO.getCompraOrderId())));
                entity.setPedidoCompra(pedidoCompra);
            }

            return compraOrderItemMapper.toResponseDTO(compraOrderItemRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao atualizar item do pedido de compra", e);
        }
    }

    public void delete(UUID id) {
        try {
            if (!compraOrderItemRepository.existsById(id)) {
                throw new ExcecaoNegocioNaoEncontrado(String.format("Item do pedido de compra com ID %s não encontrado", id));
            }
            compraOrderItemRepository.deleteById(id);
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao excluir item do pedido de compra", e);
        }
    }

    public ItemPedidoCompraResponseDTO findById(UUID id) {
        PedidoCompraItem entity = compraOrderItemRepository.findById(id)
                .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Item do pedido de compra com ID %s não encontrado", id)));
        return compraOrderItemMapper.toResponseDTO(entity);
    }

    public List<ItemPedidoCompraResponseDTO> findAll() {
        return compraOrderItemMapper.toResponseDTOList(compraOrderItemRepository.findAll());
    }

    public List<ItemPedidoCompraResponseDTO> findByPedidoCompraId(UUID compraOrderId) {
        return compraOrderItemMapper.toResponseDTOList(compraOrderItemRepository.findByPedidoCompraId(compraOrderId));
    }

    public List<ItemPedidoCompraResponseDTO> replaceByPedidoCompra(PedidoCompra pedidoCompra, List<ItemPedidoCompraInputDTO> inputDTOs) {
        try {
            compraOrderItemRepository.deleteByPedidoCompra(pedidoCompra);

            if (inputDTOs == null || inputDTOs.isEmpty()) {
                return List.of();
            }

            List<PedidoCompraItem> itens = inputDTOs.stream()
                    .map(dto -> {
                        PedidoCompraItem entity = compraOrderItemMapper.toEntity(dto);
                        if (entity.getId() != null) {
                            entity.setId(null);
                        }
                        entity.setPedidoCompra(pedidoCompra);
                        return entity;
                    })
                    .toList();

            return compraOrderItemMapper.toResponseDTOList(compraOrderItemRepository.saveAllAndFlush(itens));
        } catch (jakarta.validation.ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao substituir itens do pedido de compra " + pedidoCompra.getId(), e);
        }
    }

    public List<ItemPedidoCompraResponseDTO> saveAllByPedidoCompra(UUID compraOrderId, List<ItemPedidoCompraInputDTO> inputDTOs) {
        try {
            PedidoCompra pedidoCompra = compraOrderRepository.findById(compraOrderId)
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Pedido de compra com ID %s não encontrado", compraOrderId)));

            List<PedidoCompraItem> itens = inputDTOs == null ? List.of() : inputDTOs.stream()
                    .map(dto -> {
                        PedidoCompraItem entity = compraOrderItemMapper.toEntity(dto);
                        entity.setPedidoCompra(pedidoCompra);
                        return entity;
                    })
                    .toList();

            return compraOrderItemMapper.toResponseDTOList(compraOrderItemRepository.saveAllAndFlush(itens));
        } catch (jakarta.validation.ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao salvar itens do pedido de compra " + compraOrderId, e);
        }
    }

    public List<ItemPedidoCompraResponseDTO> savePedidoCompraItens(UUID compraOrderId, List<ItemPedidoCompraInputDTO> inputDTOs) {
        return saveAllByPedidoCompra(compraOrderId, inputDTOs);
    }

    private void validatePayload(ItemPedidoCompraInputDTO inputDTO) {
        if (inputDTO == null) {
            throw new ExcecaoNegocio("O payload do item do pedido de compra é obrigatório.");
        }
        if (inputDTO.getCompraOrderId() == null) {
            throw new ExcecaoNegocio("O ID do pedido de compra é obrigatório.");
        }
        if (inputDTO.getProdutoId() == null) {
            throw new ExcecaoNegocio("O ID do produto é obrigatório.");
        }
        if (inputDTO.getQuantidade() == null || inputDTO.getQuantidade() <= 0) {
            throw new ExcecaoNegocio("A quantidade deve ser maior que zero.");
        }
        if (inputDTO.getCustoUnitario() == null) {
            throw new ExcecaoNegocio("O custo unidadeário é obrigatório.");
        }
    }
}
