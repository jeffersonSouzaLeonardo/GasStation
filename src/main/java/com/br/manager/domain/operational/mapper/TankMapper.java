package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.TankInputDTO;
import com.br.manager.domain.operational.dto.TankResponseDTO;
import com.br.manager.domain.operational.entity.Tank;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TankMapper {

    public Tank tankInputToTankEntity(TankInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }

        Tank tank = new Tank();
        tank.setId(inputDTO.getId());
        tank.setStationId(inputDTO.getStationId());
        tank.setCode(inputDTO.getCode());
        tank.setProductId(inputDTO.getProductId());
        tank.setCapacityLiters(inputDTO.getCapacityLiters());
        tank.setDeadStockLiters(inputDTO.getDeadStockLiters());
        tank.setCurrentBookLiters(inputDTO.getCurrentBookLiters());
        tank.setActive(inputDTO.getActive() != null ? inputDTO.getActive() : true);
        return tank;
    }

    public TankResponseDTO tankEntityToTankResponseDTO(Tank tank) {
        if (tank == null) {
            return null;
        }

        TankResponseDTO responseDTO = new TankResponseDTO();
        responseDTO.setId(tank.getId());
        responseDTO.setStationId(tank.getStationId());
        responseDTO.setCode(tank.getCode());
        responseDTO.setProductId(tank.getProductId());
        responseDTO.setCapacityLiters(tank.getCapacityLiters());
        responseDTO.setDeadStockLiters(tank.getDeadStockLiters());
        responseDTO.setCurrentBookLiters(tank.getCurrentBookLiters());
        responseDTO.setActive(tank.getActive());
        return responseDTO;
    }

    public void updateTankFromDto(TankInputDTO inputDTO, Tank entity) {
        if (inputDTO == null || entity == null) {
            return;
        }

        if (inputDTO.getStationId() != null) { entity.setStationId(inputDTO.getStationId()); }
        if (inputDTO.getCode() != null) { entity.setCode(inputDTO.getCode()); }
        if (inputDTO.getProductId() != null) { entity.setProductId(inputDTO.getProductId()); }
        if (inputDTO.getCapacityLiters() != null) { entity.setCapacityLiters(inputDTO.getCapacityLiters()); }
        if (inputDTO.getDeadStockLiters() != null) { entity.setDeadStockLiters(inputDTO.getDeadStockLiters()); }
        if (inputDTO.getCurrentBookLiters() != null) { entity.setCurrentBookLiters(inputDTO.getCurrentBookLiters()); }
        if (inputDTO.getActive() != null) { entity.setActive(inputDTO.getActive()); }
    }

    public List<TankResponseDTO> listTankEntityToListTankResponseDTO(List<Tank> tanks) {
        if (tanks == null) {
            return null;
        }
        return tanks.stream().map(this::tankEntityToTankResponseDTO).collect(Collectors.toList());
    }
}
