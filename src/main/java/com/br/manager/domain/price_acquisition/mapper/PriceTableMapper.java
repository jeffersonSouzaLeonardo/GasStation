package com.br.manager.domain.price_acquisition.mapper;

import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.price_acquisition.dto.PriceTableInputDTO;
import com.br.manager.domain.price_acquisition.dto.PriceTableResponseDTO;
import com.br.manager.domain.price_acquisition.entity.PriceTable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PriceTableMapper {

    @Mapping(target = "createdBy", source = "createdBy")
    PriceTable priceTableInputDTOToPriceTable(PriceTableInputDTO inputDTO);

    @Mapping(target = "createdBy", source = "createdBy.id")
    PriceTableResponseDTO priceTableToPriceTableResponseDTO(PriceTable priceTable);

    List<PriceTableResponseDTO> listPriceTableToListPriceTableResponseDTO(List<PriceTable> priceTables);

    @Mapping(target = "createdBy", source = "createdBy")
    void updatePriceTableFromDto(PriceTableInputDTO dto, @MappingTarget PriceTable entity);

    default User map(UUID value) {
        if (value == null) {
            return null;
        }

        User user = new User();
        user.setId(value);
        return user;
    }

    default UUID map(User value) {
        if (value == null) {
            return null;
        }

        return value.getId();
    }
}
