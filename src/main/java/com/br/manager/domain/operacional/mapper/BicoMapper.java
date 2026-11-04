package com.br.manager.domain.operacional.mapper;

import com.br.manager.domain.operacional.dto.BicoInputDTO;
import com.br.manager.domain.operacional.dto.BicoResponseDTO;
import com.br.manager.domain.operacional.entity.Bico;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BicoMapper {
    public Bico bicoInputDTOToBico(BicoInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        Bico entity = new Bico();
        BeanUtils.copyProperties(inputDTO, entity);
        return entity;
    }

    public BicoResponseDTO bicoToBicoResponseDTO(Bico entity) {
        if (entity == null) {
            return null;
        }
        BicoResponseDTO dto = new BicoResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public List<BicoResponseDTO> listBicoToListBicoResponseDTO(List<Bico> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(this::bicoToBicoResponseDTO).toList();
    }

    public void updateBicoFromDto(BicoInputDTO dto, Bico entity) {
        if (dto == null || entity == null) {
            return;
        }
        BeanUtils.copyProperties(dto, entity);
    }
}
