package com.br.manager.domain.aquisicao_preco.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.aquisicao_preco.dto.ItemRecebimentoMercadoriaInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemRecebimentoMercadoriaResponseDTO;
import com.br.manager.domain.aquisicao_preco.entity.RecebimentoMercadoria;
import com.br.manager.domain.aquisicao_preco.entity.RecebimentoMercadoriaItem;
import com.br.manager.domain.aquisicao_preco.mapper.ItemRecebimentoMercadoriaMapper;
import com.br.manager.domain.aquisicao_preco.repository.ItemRecebimentoMercadoriaRepository;
import com.br.manager.domain.aquisicao_preco.repository.RecebimentoMercadoriaRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ItemRecebimentoMercadoriaService {

    @Autowired
    private ItemRecebimentoMercadoriaRepository mercadoriaReciboItemRepository;

    @Autowired
    private ItemRecebimentoMercadoriaMapper mercadoriaReciboItemMapper;

    @Autowired
    private RecebimentoMercadoriaRepository mercadoriaReciboRepository;

    public ItemRecebimentoMercadoriaResponseDTO saveRecebimentoMercadoriaItem(ItemRecebimentoMercadoriaInputDTO inputDTO) {
        try {
            RecebimentoMercadoria recebimentoMercadoria = mercadoriaReciboRepository.findById(inputDTO.getMercadoriaReciboId())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Recebimento de mercadorias com ID %s não encontrado", inputDTO.getMercadoriaReciboId())));

            RecebimentoMercadoriaItem entity = mercadoriaReciboItemMapper.mercadoriaReciboItemInputDTOToRecebimentoMercadoriaItem(inputDTO);
            entity.setRecebimentoMercadoria(recebimentoMercadoria);
            return mercadoriaReciboItemMapper.mercadoriaReciboItemToItemRecebimentoMercadoriaResponseDTO(mercadoriaReciboItemRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao salvar item do recebimento de mercadorias", e);
        }
    }

    public List<ItemRecebimentoMercadoriaResponseDTO> saveRecebimentoMercadoriaItens(UUID mercadoriaReciboId, List<ItemRecebimentoMercadoriaInputDTO> inputDTOs) {
        try {
            RecebimentoMercadoria recebimentoMercadoria = mercadoriaReciboRepository.findById(mercadoriaReciboId)
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Recebimento de mercadorias com ID %s não encontrado", mercadoriaReciboId)));

            if (inputDTOs == null || inputDTOs.isEmpty()) {
                return List.of();
            }

            List<RecebimentoMercadoriaItem> entities = inputDTOs.stream().map(dto -> {
                RecebimentoMercadoriaItem item = mercadoriaReciboItemMapper.mercadoriaReciboItemInputDTOToRecebimentoMercadoriaItem(dto);
                item.setRecebimentoMercadoria(recebimentoMercadoria);
                return item;
            }).toList();

            return mercadoriaReciboItemMapper.listRecebimentoMercadoriaItemToListItemRecebimentoMercadoriaResponseDTO(mercadoriaReciboItemRepository.saveAllAndFlush(entities));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao salvar itens do recebimento de mercadorias " + mercadoriaReciboId, e);
        }
    }

    public List<ItemRecebimentoMercadoriaResponseDTO> replaceByRecebimentoMercadoria(UUID mercadoriaReciboId, List<ItemRecebimentoMercadoriaInputDTO> inputDTOs) {
        try {
            RecebimentoMercadoria recebimentoMercadoria = mercadoriaReciboRepository.findById(mercadoriaReciboId)
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Recebimento de mercadorias com ID %s não encontrado", mercadoriaReciboId)));

            List<RecebimentoMercadoriaItem> currentItens = mercadoriaReciboItemRepository.findByRecebimentoMercadoriaId(mercadoriaReciboId);
            mercadoriaReciboItemRepository.deleteAll(currentItens);

            if (inputDTOs == null || inputDTOs.isEmpty()) {
                return List.of();
            }

            List<RecebimentoMercadoriaItem> itens = inputDTOs.stream().map(dto -> {
                RecebimentoMercadoriaItem item = mercadoriaReciboItemMapper.mercadoriaReciboItemInputDTOToRecebimentoMercadoriaItem(dto);
                item.setRecebimentoMercadoria(recebimentoMercadoria);
                return item;
            }).toList();

            return mercadoriaReciboItemMapper.listRecebimentoMercadoriaItemToListItemRecebimentoMercadoriaResponseDTO(mercadoriaReciboItemRepository.saveAllAndFlush(itens));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao substituir itens do recebimento de mercadorias " + mercadoriaReciboId, e);
        }
    }

    public ItemRecebimentoMercadoriaResponseDTO update(ItemRecebimentoMercadoriaInputDTO inputDTO) {
        try {
            RecebimentoMercadoriaItem entity = mercadoriaReciboItemRepository.findById(inputDTO.getId())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Item do recebimento de mercadorias com ID %s não encontrado", inputDTO.getId())));

            mercadoriaReciboItemMapper.updateRecebimentoMercadoriaItemFromDto(inputDTO, entity);
            if (inputDTO.getMercadoriaReciboId() != null) {
                RecebimentoMercadoria recebimentoMercadoria = mercadoriaReciboRepository.findById(inputDTO.getMercadoriaReciboId())
                        .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Recebimento de mercadorias com ID %s não encontrado", inputDTO.getMercadoriaReciboId())));
                entity.setRecebimentoMercadoria(recebimentoMercadoria);
            }

            return mercadoriaReciboItemMapper.mercadoriaReciboItemToItemRecebimentoMercadoriaResponseDTO(mercadoriaReciboItemRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao atualizar item do recebimento de mercadorias", e);
        }
    }

    public void delete(UUID id) {
        try {
            RecebimentoMercadoriaItem item = mercadoriaReciboItemRepository.findById(id)
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Item do recebimento de mercadorias com ID %s não encontrado", id)));
            mercadoriaReciboItemRepository.delete(item);
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao excluir item do recebimento de mercadorias", e);
        }
    }

    public List<ItemRecebimentoMercadoriaResponseDTO> findByRecebimentoMercadoria(UUID mercadoriaReciboId) {
        return mercadoriaReciboItemMapper.listRecebimentoMercadoriaItemToListItemRecebimentoMercadoriaResponseDTO(mercadoriaReciboItemRepository.findByRecebimentoMercadoriaId(mercadoriaReciboId));
    }

    public ItemRecebimentoMercadoriaResponseDTO find(UUID id) {
        RecebimentoMercadoriaItem entity = mercadoriaReciboItemRepository.findById(id)
                .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Item do recebimento de mercadorias com ID %s não encontrado", id)));
        return mercadoriaReciboItemMapper.mercadoriaReciboItemToItemRecebimentoMercadoriaResponseDTO(entity);
    }

    public List<ItemRecebimentoMercadoriaResponseDTO> findAll() {
        return mercadoriaReciboItemMapper.listRecebimentoMercadoriaItemToListItemRecebimentoMercadoriaResponseDTO(mercadoriaReciboItemRepository.findAll());
    }
}
