package com.br.manager.domain.sale_receipt.mapper;

import com.br.manager.domain.sale_receipt.dto.SaleItemInputDTO;
import com.br.manager.domain.sale_receipt.dto.SaleItemResponseDTO;
import com.br.manager.domain.sale_receipt.entity.SaleItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SaleItemMapper {

    public SaleItem toEntity(SaleItemInputDTO inputDTO) {
        SaleItem entity = new SaleItem();
        entity.setId(inputDTO.getId());
        entity.setLineNumber(inputDTO.getLineNumber());
        entity.setProductId(inputDTO.getProductId());
        entity.setNozzleId(inputDTO.getNozzleId());
        entity.setTankId(inputDTO.getTankId());
        entity.setQuantity(inputDTO.getQuantity());
        entity.setUnitPrice(inputDTO.getUnitPrice());
        entity.setDiscountAmount(inputDTO.getDiscountAmount());
        entity.setMeterStart(inputDTO.getMeterStart());
        entity.setMeterEnd(inputDTO.getMeterEnd());
        return entity;
    }

    public SaleItemResponseDTO toResponse(SaleItem item) {
        return new SaleItemResponseDTO(
                item.getId(), item.getLineNumber(), item.getProductId(), item.getNozzleId(), item.getTankId(),
                item.getQuantity(), item.getUnitPrice(), item.getDiscountAmount(),
                item.getMeterStart(), item.getMeterEnd()
        );
    }

    public List<SaleItemResponseDTO> toResponseList(List<SaleItem> items) {
        return items.stream().map(this::toResponse).toList();
    }
}
