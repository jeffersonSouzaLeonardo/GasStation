package com.br.manager.infra.api.stock.factory;

import com.br.manager.domain.operational.dto.FuelResponseDTO;
import com.br.manager.domain.operational.entity.Fuel;
import com.br.manager.domain.operational.repository.FuelRepository;
import com.br.manager.domain.operational.usecase.FuelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FuelEntityFactory {

    @Autowired
    private FuelInputDTOFactory fuelInputDTOFactory;

    @Autowired
    private FuelService service;

    @Autowired
    private FuelRepository fuelRepository;

    public Fuel getFuel(){
        FuelResponseDTO dto = service.create(fuelInputDTOFactory.getFuelInputDTOFactory());
        return fuelRepository.findById(dto.getId()).get();
    }

}
