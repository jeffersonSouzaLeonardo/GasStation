package com.br.manager.domain.price_acquisition.mapper;

import com.br.manager.domain.organization.entity.Station;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.price_acquisition.dto.PriceItemResponseDTO;
import com.br.manager.domain.price_acquisition.dto.PriceTableInputDTO;
import com.br.manager.domain.price_acquisition.dto.PriceTableResponseDTO;
import com.br.manager.domain.price_acquisition.entity.PriceItem;
import com.br.manager.domain.price_acquisition.entity.PriceTable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class PriceTableMapper {

    public PriceTable priceTableInputDTOToPriceTable(PriceTableInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }

        PriceTable entity = new PriceTable();
        entity.setId(inputDTO.getId());
        entity.setName(inputDTO.getName());
        entity.setValidFrom(inputDTO.getValidFrom());
        entity.setValidTo(inputDTO.getValidTo());
        entity.setStatus(inputDTO.getStatus());

        if (inputDTO.getCreatedBy() != null) {
            User user = new User();
            user.setId(inputDTO.getCreatedBy());
            entity.setCreatedBy(user);
        }

        if (inputDTO.getStationId() != null) {
            Station station = new Station();
            station.setId(inputDTO.getStationId());
            entity.setStation(station);
        }

        return entity;
    }

    public PriceTableResponseDTO priceTableToPriceTableResponseDTO(PriceTable priceTable) {
        if (priceTable == null) {
            return null;
        }

        PriceTableResponseDTO dto = new PriceTableResponseDTO();
        dto.setId(priceTable.getId());
        dto.setStationId(priceTable.getStation() != null ? priceTable.getStation().getId() : null);
        dto.setName(priceTable.getName());
        dto.setValidFrom(priceTable.getValidFrom() != null ? priceTable.getValidFrom().toString() : null);
        dto.setValidTo(priceTable.getValidTo() != null ? priceTable.getValidTo().toString() : null);
        dto.setStatus(priceTable.getStatus());
        dto.setCreatedBy(priceTable.getCreatedBy() != null ? priceTable.getCreatedBy().getId() : null);

        if (priceTable.getPriceItems() != null) {
            dto.setPriceItems(priceTable.getPriceItems().stream()
                    .filter(Objects::nonNull)
                    .map(this::toPriceItemResponseDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public List<PriceTableResponseDTO> listPriceTableToListPriceTableResponseDTO(List<PriceTable> priceTables) {
        if (priceTables == null) {
            return null;
        }

        return priceTables.stream()
                .filter(Objects::nonNull)
                .map(this::priceTableToPriceTableResponseDTO)
                .collect(Collectors.toList());
    }

    public void updatePriceTableFromDto(PriceTableInputDTO dto, PriceTable entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getValidFrom() != null) {
            entity.setValidFrom(dto.getValidFrom());
        }
        if (dto.getValidTo() != null) {
            entity.setValidTo(dto.getValidTo());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
    }

    private PriceItemResponseDTO toPriceItemResponseDTO(PriceItem priceItem) {
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
}
