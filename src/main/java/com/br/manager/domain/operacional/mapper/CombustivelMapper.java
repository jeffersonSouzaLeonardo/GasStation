package com.br.manager.domain.operacional.mapper;

import com.br.manager.domain.operacional.dto.CombustivelInputDTO;
import com.br.manager.domain.operacional.dto.CombustivelResponseDTO;
import com.br.manager.domain.operacional.entity.Combustivel;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CombustivelMapper {
    public Combustivel combustivelInputToCombustivelEntity(CombustivelInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        Combustivel entity = new Combustivel();
        BeanUtils.copyProperties(inputDTO, entity);
        return entity;
    }

    public CombustivelResponseDTO combustivelEntityToCombustivelResponseDTO(Combustivel combustivel) {
        if (combustivel == null) {
            return null;
        }
        CombustivelResponseDTO dto = new CombustivelResponseDTO();
        BeanUtils.copyProperties(combustivel, dto);
        return dto;
    }

    public List<CombustivelResponseDTO> listCombustivelEntityToListCombustivelResponseDTO(List<Combustivel> combustivel) {
        if (combustivel == null) {
            return null;
        }
        return combustivel.stream().map(this::combustivelEntityToCombustivelResponseDTO).toList();
    }

    public void updateCombustivelFromDto(CombustivelInputDTO dto, Combustivel entity) {
        if (dto == null || entity == null) {
            return;
        }
        BeanUtils.copyProperties(dto, entity);
    }
}
