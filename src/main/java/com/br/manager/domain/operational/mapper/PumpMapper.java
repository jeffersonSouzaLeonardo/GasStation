package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.PumpInputDTO;
import com.br.manager.domain.operational.dto.PumpResponseDTO;
import com.br.manager.domain.operational.entity.Pump;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class PumpMapper {
    public abstract Pump pumpInputDTOToPump(PumpInputDTO inputDTO);

    public abstract PumpResponseDTO pumpToPumpResponseDTO(Pump entity);

    public abstract List<PumpResponseDTO> listPumpToListPumpResponseDTO(List<Pump> entities);

    public abstract void updatePumpFromDto(PumpInputDTO dto, @MappingTarget Pump entity);
}
