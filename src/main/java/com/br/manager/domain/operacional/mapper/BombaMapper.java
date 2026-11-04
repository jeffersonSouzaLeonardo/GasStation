package com.br.manager.domain.operacional.mapper;

import com.br.manager.domain.operacional.dto.BombaInputDTO;
import com.br.manager.domain.operacional.dto.BombaResponseDTO;
import com.br.manager.domain.operacional.entity.Bomba;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BombaMapper {
    public Bomba bombaInputDTOToBomba(BombaInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        Bomba entity = new Bomba();
        BeanUtils.copyProperties(inputDTO, entity);
        return entity;
    }

    public BombaResponseDTO bombaToBombaResponseDTO(Bomba entity) {
        if (entity == null) {
            return null;
        }
        BombaResponseDTO dto = new BombaResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public List<BombaResponseDTO> listBombaToListBombaResponseDTO(List<Bomba> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(this::bombaToBombaResponseDTO).toList();
    }

    public void updateBombaFromDto(BombaInputDTO dto, Bomba entity) {
        if (dto == null || entity == null) {
            return;
        }
        BeanUtils.copyProperties(dto, entity);
    }
}
