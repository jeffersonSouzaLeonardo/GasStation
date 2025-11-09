package com.br.manager.infra.api.stock.controller;

import com.br.manager.domain.stock.dto.FuelInputDTO;
import com.br.manager.domain.stock.dto.FuelResponseDTO;
import com.br.manager.domain.stock.dto.TankResponseDTO;
import com.br.manager.domain.stock.service.FuelService;
import com.br.manager.domain.stock.service.TankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tank")
public class TankController {

    @Autowired
    private TankService fuelService;

/*
    @PostMapping
    public ResponseEntity<TankResponseDTO> create(@RequestBody TankInputDTO inputDTO){
        TankResponseDTO tankResponseDTO = tankService.save(inputDTO);
        return ResponseEntity.ok(tankResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<FankResponseDTO>> getAll(){
        return ResponseEntity.ok(tankService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TankResponseDTO> getId(@PathVariable("id") Long id){
        return ResponseEntity.ok(tankService.find(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long id){
        tankService.delete(id);
    }

    @PutMapping()
    public ResponseEntity<TankResponseDTO> edit(@RequestBody FankInputDTO inputDTO){
        TankResponseDTO tankResponseDTO = tankService.save(inputDTO);
        return ResponseEntity.ok(tankResponseDTO);
    }
*/

}
