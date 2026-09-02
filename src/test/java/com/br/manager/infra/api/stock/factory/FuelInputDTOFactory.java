package com.br.manager.infra.api.stock.factory;

import com.br.manager.domain.operational.dto.FuelInputDTO;
import com.br.manager.domain.operational.enums.StatusFuelEnum;
import com.br.manager.domain.operational.enums.UnitFuelEnum;
import org.springframework.stereotype.Component;

@Component
public class FuelInputDTOFactory {

    public FuelInputDTO getFuelInputDTOFactory(){
        FuelInputDTO fuelInputDTO = new FuelInputDTO();
        fuelInputDTO.setName("Nome combustivel");
        fuelInputDTO.setStatus(StatusFuelEnum.ACTIVE);
        fuelInputDTO.setUnit(UnitFuelEnum.LITERS);
        fuelInputDTO.setIdAnp("1020BC5");
        return fuelInputDTO;

    }
}
