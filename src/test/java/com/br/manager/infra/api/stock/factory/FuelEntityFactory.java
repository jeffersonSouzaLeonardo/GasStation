package com.br.manager.infra.api.stock.factory;

import com.br.manager.domain.stock.dto.FuelResponseDTO;
import com.br.manager.domain.stock.entity.Fuel;
import com.br.manager.domain.stock.repository.FuelRepository;
import com.br.manager.domain.stock.service.FuelService;
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
        FuelResponseDTO dto = service.save(fuelInputDTOFactory.getFuelInputDTOFactory());
        return fuelRepository.findById(dto.getId()).get();
    }

}
