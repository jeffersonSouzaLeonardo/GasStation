package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.StationProductInputDTO;
import com.br.manager.domain.operational.dto.StationProductResponseDTO;
import com.br.manager.domain.operational.entity.StationProduct;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StationProductMapper {
    public StationProduct stationProductInputDTOToStationProduct(StationProductInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        StationProduct entity = new StationProduct();
        BeanUtils.copyProperties(inputDTO, entity);
        return entity;
    }

    public StationProductResponseDTO stationProductToStationProductResponseDTO(StationProduct entity) {
        if (entity == null) {
            return null;
        }
        StationProductResponseDTO dto = new StationProductResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public List<StationProductResponseDTO> listStationProductToListStationProductResponseDTO(List<StationProduct> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(this::stationProductToStationProductResponseDTO).toList();
    }

    public void updateStationProductFromDto(StationProductInputDTO dto, StationProduct entity) {
        if (dto == null || entity == null) {
            return;
        }
        BeanUtils.copyProperties(dto, entity);
    }
}
