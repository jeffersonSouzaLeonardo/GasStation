package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.SupplyRequestDTO;
import com.br.manager.domain.operational.entity.Supply;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SupplyMapper {

    public Supply supplyRequestDTOToSupply(SupplyRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Supply entity = new Supply();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    public List<Supply> listSupplyRequestDTOToListSupply(List<SupplyRequestDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream().map(this::supplyRequestDTOToSupply).toList();
    }
}
