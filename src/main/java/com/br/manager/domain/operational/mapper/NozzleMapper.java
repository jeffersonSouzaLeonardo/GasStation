package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.NozzleInputDTO;
import com.br.manager.domain.operational.dto.NozzleResponseDTO;
import com.br.manager.domain.operational.entity.Nozzle;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NozzleMapper {
    Nozzle nozzleInputDTOToNozzle(NozzleInputDTO inputDTO);

    NozzleResponseDTO nozzleToNozzleResponseDTO(Nozzle entity);

    List<NozzleResponseDTO> listNozzleToListNozzleResponseDTO(List<Nozzle> entities);

    void updateNozzleFromDto(NozzleInputDTO dto, @MappingTarget Nozzle entity);
}
