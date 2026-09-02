package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.TankInputDTO;
import com.br.manager.domain.operational.dto.TankResponseDTO;
import com.br.manager.domain.operational.entity.Tank;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TankMapper {
    @Mapping(target = "fuel", ignore = true)
    Tank tankInputToTankEntity(TankInputDTO inputDTO);

    TankResponseDTO tankEntityToTankResponseDTO(Tank tank);

    @Mapping(target = "fuel", ignore = true)
    void updateTankFromDto(TankInputDTO inputDTO, @MappingTarget Tank entity);

    List<TankResponseDTO> listTankEntityToListTankResponseDTO(List<Tank> tank);

}
