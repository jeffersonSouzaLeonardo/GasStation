package com.br.manager.domain.aquisicao_preco.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.aquisicao_preco.dto.ItemPrecoInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemPrecoResponseDTO;
import com.br.manager.domain.aquisicao_preco.entity.ItemPreco;
import com.br.manager.domain.aquisicao_preco.entity.TabelaPreco;
import com.br.manager.domain.aquisicao_preco.mapper.ItemPrecoMapper;
import com.br.manager.domain.aquisicao_preco.repository.ItemPrecoRepository;
import com.br.manager.domain.aquisicao_preco.repository.TabelaPrecoRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ItemPrecoService {

    @Autowired
    private ItemPrecoRepository precoItemRepository;

    @Autowired
    private ItemPrecoMapper precoItemMapper;

    @Autowired
    private TabelaPrecoRepository precoTabelaRepository;

    public List<ItemPrecoResponseDTO> replaceByTabelaPreco(TabelaPreco tabelaPreco, List<ItemPrecoInputDTO> inputDTOs) {
        try {

            precoItemRepository.deleteByTabelaPreco(tabelaPreco);

            if (inputDTOs == null || inputDTOs.isEmpty()) {
                return List.of();
            }

            List<ItemPreco> itensPreco = inputDTOs.stream()
                    .map(dto -> {
                        ItemPreco entity = precoItemMapper.precoItemInputDTOToItemPreco(dto);
                        // If the client sent an id but rows were excluido above, clear the id so JPA will insert a new row
                        if (entity.getId() != null) {
                            entity.setId(null);
                        }
                        entity.setTabelaPreco(tabelaPreco);
                        entity.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
                        return entity;
                    })
                    .toList();

            return precoItemMapper.listItemPrecoToListItemPrecoResponseDTO(precoItemRepository.saveAllAndFlush(itensPreco));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao substituir itens de preço da tabela " + tabelaPreco.getNome(), e);
        }
    }

    public List<ItemPrecoResponseDTO> saveAllByTabelaPreco(UUID precoTabelaId, List<ItemPrecoInputDTO> inputDTOs) {
        try {
            TabelaPreco tabelaPreco = precoTabelaRepository.findById(precoTabelaId)
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Tabela de preços com ID %s não encontrada", precoTabelaId)));
            List<ItemPreco> itensPreco = inputDTOs == null ? List.of() : inputDTOs.stream()
                    .map(dto -> {
                        ItemPreco entity = precoItemMapper.precoItemInputDTOToItemPreco(dto);
                        if (entity.getId() == null) {
                                                    }
                        entity.setTabelaPreco(tabelaPreco);
                        return entity;
                    })
                    .toList();

            return precoItemMapper.listItemPrecoToListItemPrecoResponseDTO(precoItemRepository.saveAllAndFlush(itensPreco));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao salvar itens de preço da tabela " + precoTabelaId, e);
        }
    }


    public ItemPrecoResponseDTO saveItemPreco(ItemPrecoInputDTO inputDTO) {
        try {
            TabelaPreco tabelaPreco = precoTabelaRepository.findById(inputDTO.getPrecoTabelaId())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Tabela de preços com ID %s não encontrada", inputDTO.getPrecoTabelaId())));
            ItemPreco entity = precoItemMapper.precoItemInputDTOToItemPreco(inputDTO);
            entity.setTabelaPreco(tabelaPreco);
            return precoItemMapper.precoItemToItemPrecoResponseDTO(precoItemRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao salvar itens de preço da tabela " + inputDTO.getPrecoTabelaId(), e);
        }
    }

    public List<ItemPrecoResponseDTO> saveItemPrecos(UUID precoTabelaId, List<ItemPrecoInputDTO> inputDTOs) {
        return saveAllByTabelaPreco(precoTabelaId, inputDTOs);
    }

    public ItemPrecoResponseDTO update(ItemPrecoInputDTO inputDTO) {
        try {
            ItemPreco entity = precoItemRepository.findByIdAndAtivoTrue(inputDTO.getId());
            if (entity == null) {
                throw new ExcecaoNegocioNaoEncontrado(String.format("Item de preço com ID %s não encontrado", inputDTO.getId()));
            }
            precoItemMapper.updateItemPrecoFromDto(inputDTO, entity);
            if (inputDTO.getPrecoTabelaId() != null) {
                TabelaPreco tabelaPreco = precoTabelaRepository.findById(inputDTO.getPrecoTabelaId())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Tabela de preços com ID %s não encontrada", inputDTO.getPrecoTabelaId())));
                entity.setTabelaPreco(tabelaPreco);
            }
            return precoItemMapper.precoItemToItemPrecoResponseDTO(precoItemRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao atualizar item de preço", e);
        }
    }

    public void delete(UUID id) {
        try {
            ItemPreco itemPreco = precoItemRepository.findById(id)
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Item de preço com ID %s não encontrado", id)));
            precoItemRepository.delete(itemPreco);
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao excluir item de preço", e);
        }
    }

    public List<ItemPrecoResponseDTO> findByTabelaPreco(UUID precoTabelaId) {
        return precoItemMapper.listItemPrecoToListItemPrecoResponseDTO(precoItemRepository.findByTabelaPrecoIdAndAtivoTrue(precoTabelaId));
    }

    public List<ItemPrecoResponseDTO> findAllByTabelaPreco(UUID precoTabelaId) {
        return findByTabelaPreco(precoTabelaId);
    }

    public ItemPrecoResponseDTO find(UUID id) {
        ItemPreco entity = precoItemRepository.findByIdAndAtivoTrue(id);
        if (entity == null) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Item de preço com ID %s não encontrado", id));
        }
        return precoItemMapper.precoItemToItemPrecoResponseDTO(entity);
    }

    public List<ItemPrecoResponseDTO> findAll() {
        return precoItemMapper.listItemPrecoToListItemPrecoResponseDTO(precoItemRepository.findAllByAtivoTrue());
    }

    public ItemPreco getItemPrecoEntityById(UUID id) {
        ItemPreco entity = precoItemRepository.findByIdAndAtivoTrue(id);
        if (entity == null) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Item de preço com ID %s não encontrado", id));
        }
        return entity;
    }
}
