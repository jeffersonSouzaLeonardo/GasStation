package com.br.manager.domain.price_acquisition.mapper;

import com.br.manager.domain.operational.entity.Supplier;
import com.br.manager.domain.organization.entity.Station;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.price_acquisition.dto.GoodsReceiptInputDTO;
import com.br.manager.domain.price_acquisition.dto.GoodsReceiptItemResponseDTO;
import com.br.manager.domain.price_acquisition.dto.GoodsReceiptResponseDTO;
import com.br.manager.domain.price_acquisition.entity.GoodsReceipt;
import com.br.manager.domain.price_acquisition.entity.GoodsReceiptItem;
import com.br.manager.domain.price_acquisition.entity.PurchaseOrder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class GoodsReceiptMapper {

    public GoodsReceipt goodsReceiptInputDTOToGoodsReceipt(GoodsReceiptInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }

        GoodsReceipt entity = new GoodsReceipt();
        entity.setId(inputDTO.getId());
        entity.setInvoiceNumber(inputDTO.getInvoiceNumber());
        entity.setInvoiceKey(inputDTO.getInvoiceKey());
        entity.setReceivedAt(inputDTO.getReceivedAt());
        entity.setStatus(inputDTO.getStatus());
        entity.setFreightAmount(inputDTO.getFreightAmount());
        entity.setOtherCosts(inputDTO.getOtherCosts());

        if (inputDTO.getSupplierId() != null) {
            Supplier supplier = new Supplier();
            supplier.setId(inputDTO.getSupplierId());
            entity.setSupplier(supplier);
        }

        if (inputDTO.getPurchaseOrderId() != null) {
            PurchaseOrder purchaseOrder = new PurchaseOrder();
            purchaseOrder.setId(inputDTO.getPurchaseOrderId());
            entity.setPurchaseOrder(purchaseOrder);
        }

        if (inputDTO.getReceivedBy() != null) {
            User user = new User();
            user.setId(inputDTO.getReceivedBy());
            entity.setReceivedBy(user);
        }

        return entity;
    }

    public GoodsReceiptResponseDTO goodsReceiptToGoodsReceiptResponseDTO(GoodsReceipt goodsReceipt) {
        if (goodsReceipt == null) {
            return null;
        }

        GoodsReceiptResponseDTO dto = new GoodsReceiptResponseDTO();
        dto.setId(goodsReceipt.getId());
        dto.setSupplierId(goodsReceipt.getSupplier() != null ? goodsReceipt.getSupplier().getId() : null);
        dto.setPurchaseOrderId(goodsReceipt.getPurchaseOrder() != null ? goodsReceipt.getPurchaseOrder().getId() : null);
        dto.setInvoiceNumber(goodsReceipt.getInvoiceNumber());
        dto.setInvoiceKey(goodsReceipt.getInvoiceKey());
        dto.setReceivedAt(goodsReceipt.getReceivedAt());
        dto.setStatus(goodsReceipt.getStatus());
        dto.setFreightAmount(goodsReceipt.getFreightAmount());
        dto.setOtherCosts(goodsReceipt.getOtherCosts());
        dto.setReceivedBy(goodsReceipt.getReceivedBy() != null ? goodsReceipt.getReceivedBy().getId() : null);

        if (goodsReceipt.getItems() != null) {
            dto.setItems(goodsReceipt.getItems().stream()
                    .filter(Objects::nonNull)
                    .map(this::toGoodsReceiptItemResponseDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public List<GoodsReceiptResponseDTO> listGoodsReceiptToListGoodsReceiptResponseDTO(List<GoodsReceipt> goodsReceipts) {
        if (goodsReceipts == null) {
            return null;
        }

        return goodsReceipts.stream()
                .filter(Objects::nonNull)
                .map(this::goodsReceiptToGoodsReceiptResponseDTO)
                .collect(Collectors.toList());
    }

    public void updateGoodsReceiptFromDto(GoodsReceiptInputDTO dto, GoodsReceipt entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getInvoiceNumber() != null) {
            entity.setInvoiceNumber(dto.getInvoiceNumber());
        }
        if (dto.getInvoiceKey() != null) {
            entity.setInvoiceKey(dto.getInvoiceKey());
        }
        if (dto.getReceivedAt() != null) {
            entity.setReceivedAt(dto.getReceivedAt());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        if (dto.getFreightAmount() != null) {
            entity.setFreightAmount(dto.getFreightAmount());
        }
        if (dto.getOtherCosts() != null) {
            entity.setOtherCosts(dto.getOtherCosts());
        }
    }

    private GoodsReceiptItemResponseDTO toGoodsReceiptItemResponseDTO(GoodsReceiptItem item) {
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
}
