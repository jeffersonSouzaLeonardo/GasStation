package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.NozzleInputDTO;
import com.br.manager.domain.operational.dto.NozzleResponseDTO;
import com.br.manager.domain.operational.entity.Nozzle;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class NozzleMapper {
    public abstract Nozzle nozzleInputDTOToNozzle(NozzleInputDTO inputDTO);

    public abstract NozzleResponseDTO nozzleToNozzleResponseDTO(Nozzle entity);

    public abstract List<NozzleResponseDTO> listNozzleToListNozzleResponseDTO(List<Nozzle> entities);

    public abstract void updateNozzleFromDto(NozzleInputDTO dto, @MappingTarget Nozzle entity);
}
