package com.br.manager.infra.api.stock.mapper;

import com.br.manager.domain.stock.dto.TankInputDTO;
import com.br.manager.domain.stock.dto.TankResponseDTO;
import com.br.manager.domain.stock.entity.Tank;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TankMapper {
    Tank tankInputToTankEntity(TankInputDTO inputDTO);

    TankResponseDTO tankEntityToTankResponseDTO(Tank tank);

    List<TankResponseDTO> listTankEntityToListTankResponseDTO(List<Tank> tank);

}
