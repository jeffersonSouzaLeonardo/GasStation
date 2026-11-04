package com.br.manager.domain.operacional.mapper;

import com.br.manager.domain.operacional.dto.AbastecimentoRequestDTO;
import com.br.manager.domain.operacional.entity.Abastecimento;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AbastecimentoMapper {

    public Abastecimento abastecimentoRequestDTOToAbastecimento(AbastecimentoRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Abastecimento entity = new Abastecimento();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    public List<Abastecimento> listAbastecimentoRequestDTOToListAbastecimento(List<AbastecimentoRequestDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream().map(this::abastecimentoRequestDTOToAbastecimento).toList();
    }
}
