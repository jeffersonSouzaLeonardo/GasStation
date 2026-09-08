package com.br.manager.domain.price_acquisition.mapper;

import com.br.manager.domain.price_acquisition.dto.PriceItemInputDTO;
import com.br.manager.domain.price_acquisition.dto.PriceItemResponseDTO;
import com.br.manager.domain.price_acquisition.entity.PriceItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PriceItemMapper {

    @Mapping(target = "priceTable", ignore = true)
    PriceItem priceItemInputDTOToPriceItem(PriceItemInputDTO inputDTO);

    @Mapping(target = "priceTableId", source = "priceTable.id")
    PriceItemResponseDTO priceItemToPriceItemResponseDTO(PriceItem priceItem);

    List<PriceItemResponseDTO> listPriceItemToListPriceItemResponseDTO(List<PriceItem> priceItems);

    List<PriceItem> listPriceItemInputDTOToListPriceItem(List<PriceItemInputDTO> inputDTOs);

    @Mapping(target = "priceTable", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updatePriceItemFromDto(PriceItemInputDTO dto, @MappingTarget PriceItem entity);
}
