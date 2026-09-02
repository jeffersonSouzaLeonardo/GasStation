package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.PumpInputDTO;
import com.br.manager.domain.operational.dto.PumpResponseDTO;
import com.br.manager.domain.operational.entity.Pump;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PumpMapper {
    Pump pumpInputDTOToPump(PumpInputDTO inputDTO);

    PumpResponseDTO pumpToPumpResponseDTO(Pump entity);

    List<PumpResponseDTO> listPumpToListPumpResponseDTO(List<Pump> entities);

    void updatePumpFromDto(PumpInputDTO dto, @MappingTarget Pump entity);
}
