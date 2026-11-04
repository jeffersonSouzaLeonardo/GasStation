package com.br.manager.domain.operacional.mapper;

import com.br.manager.domain.operacional.dto.MetodoPagamentoInputDTO;
import com.br.manager.domain.operacional.dto.MetodoPagamentoResponseDTO;
import com.br.manager.domain.operacional.entity.MetodoPagamento;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MetodoPagamentoMapper {
    public MetodoPagamento pagamentoMethodInputDTOToMetodoPagamento(MetodoPagamentoInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        MetodoPagamento entity = new MetodoPagamento();
        BeanUtils.copyProperties(inputDTO, entity);
        return entity;
    }

    public MetodoPagamentoResponseDTO pagamentoMethodToMetodoPagamentoResponseDTO(MetodoPagamento entity) {
        if (entity == null) {
            return null;
        }
        MetodoPagamentoResponseDTO dto = new MetodoPagamentoResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public List<MetodoPagamentoResponseDTO> listMetodoPagamentoToListMetodoPagamentoResponseDTO(List<MetodoPagamento> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(this::pagamentoMethodToMetodoPagamentoResponseDTO).toList();
    }

    public void updateMetodoPagamentoFromDto(MetodoPagamentoInputDTO dto, MetodoPagamento entity) {
        if (dto == null || entity == null) {
            return;
        }
        BeanUtils.copyProperties(dto, entity);
    }
}
