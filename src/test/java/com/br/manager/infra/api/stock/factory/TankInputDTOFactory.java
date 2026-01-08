package com.br.manager.infra.api.stock.factory;

import com.br.manager.domain.stock.dto.TankInputDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TankInputDTOFactory {

    @Autowired
    private FuelEntityFactory fuelEntityFactory;

    public TankInputDTO getTankInputDTOFactory(){
        TankInputDTO tankInputDTO = new TankInputDTO();
        tankInputDTO.setIdentity("Gasolina");
        tankInputDTO.setCapacity(new BigDecimal(10.000));
        tankInputDTO.setFuel(fuelEntityFactory.getFuel());
        tankInputDTO.setVolume(new BigDecimal(2.000));
        return tankInputDTO;

    }
}
