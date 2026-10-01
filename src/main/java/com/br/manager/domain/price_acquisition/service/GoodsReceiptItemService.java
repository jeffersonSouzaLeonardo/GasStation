package com.br.manager.domain.price_acquisition.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.price_acquisition.dto.GoodsReceiptItemInputDTO;
import com.br.manager.domain.price_acquisition.dto.GoodsReceiptItemResponseDTO;
import com.br.manager.domain.price_acquisition.entity.GoodsReceipt;
import com.br.manager.domain.price_acquisition.entity.GoodsReceiptItem;
import com.br.manager.domain.price_acquisition.mapper.GoodsReceiptItemMapper;
import com.br.manager.domain.price_acquisition.repository.GoodsReceiptItemRepository;
import com.br.manager.domain.price_acquisition.repository.GoodsReceiptRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class GoodsReceiptItemService {

    @Autowired
    private GoodsReceiptItemRepository goodsReceiptItemRepository;

    @Autowired
    private GoodsReceiptItemMapper goodsReceiptItemMapper;

    @Autowired
    private GoodsReceiptRepository goodsReceiptRepository;

    public GoodsReceiptItemResponseDTO saveGoodsReceiptItem(GoodsReceiptItemInputDTO inputDTO) {
        try {
            GoodsReceipt goodsReceipt = goodsReceiptRepository.findById(inputDTO.getGoodsReceiptId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Recebimento de mercadorias com ID %s não encontrado", inputDTO.getGoodsReceiptId())));

            GoodsReceiptItem entity = goodsReceiptItemMapper.goodsReceiptItemInputDTOToGoodsReceiptItem(inputDTO);
            entity.setGoodsReceipt(goodsReceipt);
            return goodsReceiptItemMapper.goodsReceiptItemToGoodsReceiptItemResponseDTO(goodsReceiptItemRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Erro ao salvar item do recebimento de mercadorias", e);
        }
    }

    public List<GoodsReceiptItemResponseDTO> saveGoodsReceiptItems(UUID goodsReceiptId, List<GoodsReceiptItemInputDTO> inputDTOs) {
        try {
            GoodsReceipt goodsReceipt = goodsReceiptRepository.findById(goodsReceiptId)
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Recebimento de mercadorias com ID %s não encontrado", goodsReceiptId)));

            if (inputDTOs == null || inputDTOs.isEmpty()) {
                return List.of();
            }

            List<GoodsReceiptItem> entities = inputDTOs.stream().map(dto -> {
                GoodsReceiptItem item = goodsReceiptItemMapper.goodsReceiptItemInputDTOToGoodsReceiptItem(dto);
                item.setGoodsReceipt(goodsReceipt);
                return item;
            }).toList();

            return goodsReceiptItemMapper.listGoodsReceiptItemToListGoodsReceiptItemResponseDTO(goodsReceiptItemRepository.saveAllAndFlush(entities));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Erro ao salvar itens do recebimento de mercadorias " + goodsReceiptId, e);
        }
    }

    public List<GoodsReceiptItemResponseDTO> replaceByGoodsReceipt(UUID goodsReceiptId, List<GoodsReceiptItemInputDTO> inputDTOs) {
        try {
            GoodsReceipt goodsReceipt = goodsReceiptRepository.findById(goodsReceiptId)
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Recebimento de mercadorias com ID %s não encontrado", goodsReceiptId)));

            List<GoodsReceiptItem> currentItems = goodsReceiptItemRepository.findByGoodsReceiptId(goodsReceiptId);
            goodsReceiptItemRepository.deleteAll(currentItems);

            if (inputDTOs == null || inputDTOs.isEmpty()) {
                return List.of();
            }

            List<GoodsReceiptItem> items = inputDTOs.stream().map(dto -> {
                GoodsReceiptItem item = goodsReceiptItemMapper.goodsReceiptItemInputDTOToGoodsReceiptItem(dto);
                item.setGoodsReceipt(goodsReceipt);
                return item;
            }).toList();

            return goodsReceiptItemMapper.listGoodsReceiptItemToListGoodsReceiptItemResponseDTO(goodsReceiptItemRepository.saveAllAndFlush(items));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Erro ao substituir itens do recebimento de mercadorias " + goodsReceiptId, e);
        }
    }

    public GoodsReceiptItemResponseDTO update(GoodsReceiptItemInputDTO inputDTO) {
        try {
            GoodsReceiptItem entity = goodsReceiptItemRepository.findById(inputDTO.getId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Item do recebimento de mercadorias com ID %s não encontrado", inputDTO.getId())));

            goodsReceiptItemMapper.updateGoodsReceiptItemFromDto(inputDTO, entity);
            if (inputDTO.getGoodsReceiptId() != null) {
                GoodsReceipt goodsReceipt = goodsReceiptRepository.findById(inputDTO.getGoodsReceiptId())
                        .orElseThrow(() -> new NotFoundBusinessException(String.format("Recebimento de mercadorias com ID %s não encontrado", inputDTO.getGoodsReceiptId())));
                entity.setGoodsReceipt(goodsReceipt);
            }

            return goodsReceiptItemMapper.goodsReceiptItemToGoodsReceiptItemResponseDTO(goodsReceiptItemRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Erro ao atualizar item do recebimento de mercadorias", e);
        }
    }

    public void delete(UUID id) {
        try {
            GoodsReceiptItem item = goodsReceiptItemRepository.findById(id)
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Item do recebimento de mercadorias com ID %s não encontrado", id)));
            goodsReceiptItemRepository.delete(item);
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Erro ao excluir item do recebimento de mercadorias", e);
        }
    }

    public List<GoodsReceiptItemResponseDTO> findByGoodsReceipt(UUID goodsReceiptId) {
        return goodsReceiptItemMapper.listGoodsReceiptItemToListGoodsReceiptItemResponseDTO(goodsReceiptItemRepository.findByGoodsReceiptId(goodsReceiptId));
    }

    public GoodsReceiptItemResponseDTO find(UUID id) {
        GoodsReceiptItem entity = goodsReceiptItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Item do recebimento de mercadorias com ID %s não encontrado", id)));
        return goodsReceiptItemMapper.goodsReceiptItemToGoodsReceiptItemResponseDTO(entity);
    }

    public List<GoodsReceiptItemResponseDTO> findAll() {
        return goodsReceiptItemMapper.listGoodsReceiptItemToListGoodsReceiptItemResponseDTO(goodsReceiptItemRepository.findAll());
    }
}
