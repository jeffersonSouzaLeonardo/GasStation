package com.br.manager.domain.price_acquisition.mapper;

import com.br.manager.domain.price_acquisition.dto.GoodsReceiptItemInputDTO;
import com.br.manager.domain.price_acquisition.dto.GoodsReceiptItemResponseDTO;
import com.br.manager.domain.price_acquisition.entity.GoodsReceipt;
import com.br.manager.domain.price_acquisition.entity.GoodsReceiptItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class GoodsReceiptItemMapper {

    public GoodsReceiptItem goodsReceiptItemInputDTOToGoodsReceiptItem(GoodsReceiptItemInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }

        GoodsReceiptItem entity = new GoodsReceiptItem();
        entity.setId(inputDTO.getId());
        entity.setProductId(inputDTO.getProductId());
        entity.setTankId(inputDTO.getTankId());
        entity.setQuantity(inputDTO.getQuantity());
        entity.setUnitCost(inputDTO.getUnitCost());
        return entity;
    }

    public GoodsReceiptItemResponseDTO goodsReceiptItemToGoodsReceiptItemResponseDTO(GoodsReceiptItem item) {
        if (item == null) {
            return null;
        }

        GoodsReceiptItemResponseDTO dto = new GoodsReceiptItemResponseDTO();
        dto.setId(item.getId());
        dto.setGoodsReceiptId(item.getGoodsReceipt() != null ? item.getGoodsReceipt().getId() : null);
        dto.setProductId(item.getProductId());
        dto.setTankId(item.getTankId());
        dto.setQuantity(item.getQuantity());
        dto.setUnitCost(item.getUnitCost());
        return dto;
    }

    public List<GoodsReceiptItemResponseDTO> listGoodsReceiptItemToListGoodsReceiptItemResponseDTO(List<GoodsReceiptItem> items) {
        if (items == null) {
            return null;
        }

        return items.stream()
                .filter(Objects::nonNull)
                .map(this::goodsReceiptItemToGoodsReceiptItemResponseDTO)
                .collect(Collectors.toList());
    }

    public void updateGoodsReceiptItemFromDto(GoodsReceiptItemInputDTO dto, GoodsReceiptItem entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getProductId() != null) {
            entity.setProductId(dto.getProductId());
        }
        if (dto.getTankId() != null) {
            entity.setTankId(dto.getTankId());
        }
        if (dto.getQuantity() != null) {
            entity.setQuantity(dto.getQuantity());
        }
        if (dto.getUnitCost() != null) {
            entity.setUnitCost(dto.getUnitCost());
        }
    }
}
