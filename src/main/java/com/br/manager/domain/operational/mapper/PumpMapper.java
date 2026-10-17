package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.PumpInputDTO;
import com.br.manager.domain.operational.dto.PumpResponseDTO;
import com.br.manager.domain.operational.entity.Pump;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PumpMapper {
    public Pump pumpInputDTOToPump(PumpInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        Pump entity = new Pump();
        BeanUtils.copyProperties(inputDTO, entity);
        return entity;
    }

    public PumpResponseDTO pumpToPumpResponseDTO(Pump entity) {
        if (entity == null) {
            return null;
        }
        PumpResponseDTO dto = new PumpResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public List<PumpResponseDTO> listPumpToListPumpResponseDTO(List<Pump> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(this::pumpToPumpResponseDTO).toList();
    }

    public void updatePumpFromDto(PumpInputDTO dto, Pump entity) {
        if (dto == null || entity == null) {
            return;
        }
        BeanUtils.copyProperties(dto, entity);
    }
}
