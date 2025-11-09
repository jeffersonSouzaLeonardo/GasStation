package com.br.manager.infra.api.stock.factory;

import com.br.manager.domain.stock.dto.FuelInputDTO;
import org.springframework.stereotype.Component;

@Component
public class FuelInputDTOFactory {

    public FuelInputDTO getFuelInputDTOFactory(){
        FuelInputDTO fuelInputDTO = new FuelInputDTO();
        fuelInputDTO.setName("Nome combustivel");
        fuelInputDTO.setActive(true);
        fuelInputDTO.setUnit("Litros");
        fuelInputDTO.setIdAnp("1020BC5");
        return fuelInputDTO;

    }
}
