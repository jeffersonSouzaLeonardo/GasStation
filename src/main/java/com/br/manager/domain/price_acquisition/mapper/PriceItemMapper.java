package com.br.manager.domain.price_acquisition.mapper;

import com.br.manager.domain.price_acquisition.dto.PriceItemInputDTO;
import com.br.manager.domain.price_acquisition.dto.PriceItemResponseDTO;
import com.br.manager.domain.price_acquisition.entity.PriceItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class PriceItemMapper {

    public PriceItem priceItemInputDTOToPriceItem(PriceItemInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }

        PriceItem entity = new PriceItem();
        entity.setId(inputDTO.getId());
        entity.setProductId(inputDTO.getProductId());
        entity.setNozzleId(inputDTO.getNozzleId());
        entity.setUnitPrice(inputDTO.getUnitPrice());
        entity.setMinPrice(inputDTO.getMinPrice());
        entity.setMaxDiscountPercent(inputDTO.getMaxDiscountPercent());
        entity.setActive(inputDTO.getActive() != null ? inputDTO.getActive() : true);
        return entity;
    }

    public PriceItemResponseDTO priceItemToPriceItemResponseDTO(PriceItem priceItem) {
        if (priceItem == null) {
            return null;
        }

        PriceItemResponseDTO dto = new PriceItemResponseDTO();
        dto.setId(priceItem.getId());
        dto.setPriceTableId(priceItem.getPriceTable() != null ? priceItem.getPriceTable().getId() : null);
        dto.setProductId(priceItem.getProductId());
        dto.setNozzleId(priceItem.getNozzleId());
        dto.setUnitPrice(priceItem.getUnitPrice());
        dto.setMinPrice(priceItem.getMinPrice());
        dto.setMaxDiscountPercent(priceItem.getMaxDiscountPercent());
        dto.setActive(priceItem.getActive());
        return dto;
    }

    public List<PriceItemResponseDTO> listPriceItemToListPriceItemResponseDTO(List<PriceItem> priceItems) {
        if (priceItems == null) {
            return null;
        }

        return priceItems.stream()
                .filter(Objects::nonNull)
                .map(this::priceItemToPriceItemResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PriceItem> listPriceItemInputDTOToListPriceItem(List<PriceItemInputDTO> inputDTOs) {
        if (inputDTOs == null) {
            return null;
        }

        return inputDTOs.stream()
                .filter(Objects::nonNull)
                .map(this::priceItemInputDTOToPriceItem)
                .collect(Collectors.toList());
    }

    public void updatePriceItemFromDto(PriceItemInputDTO dto, PriceItem entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getProductId() != null) {
            entity.setProductId(dto.getProductId());
        }
        if (dto.getNozzleId() != null) {
            entity.setNozzleId(dto.getNozzleId());
        }
        if (dto.getUnitPrice() != null) {
            entity.setUnitPrice(dto.getUnitPrice());
        }
        if (dto.getMinPrice() != null) {
            entity.setMinPrice(dto.getMinPrice());
        }
        if (dto.getMaxDiscountPercent() != null) {
            entity.setMaxDiscountPercent(dto.getMaxDiscountPercent());
        }
        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }
    }
}
