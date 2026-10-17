package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.NozzleInputDTO;
import com.br.manager.domain.operational.dto.NozzleResponseDTO;
import com.br.manager.domain.operational.entity.Nozzle;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NozzleMapper {
    public Nozzle nozzleInputDTOToNozzle(NozzleInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        Nozzle entity = new Nozzle();
        BeanUtils.copyProperties(inputDTO, entity);
        return entity;
    }

    public NozzleResponseDTO nozzleToNozzleResponseDTO(Nozzle entity) {
        if (entity == null) {
            return null;
        }
        NozzleResponseDTO dto = new NozzleResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public List<NozzleResponseDTO> listNozzleToListNozzleResponseDTO(List<Nozzle> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(this::nozzleToNozzleResponseDTO).toList();
    }

    public void updateNozzleFromDto(NozzleInputDTO dto, Nozzle entity) {
        if (dto == null || entity == null) {
            return;
        }
        BeanUtils.copyProperties(dto, entity);
    }
}
