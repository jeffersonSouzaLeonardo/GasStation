package com.br.manager.infra.api.stock.controller;

import com.br.manager.domain.stock.dto.FuelInputDTO;
import com.br.manager.domain.stock.dto.FuelResponseDTO;
import com.br.manager.domain.stock.service.FuelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/fuel")
public class FuelController {

    @Valid
    @PostMapping
    public ResponseEntity<FuelResponseDTO> create(@RequestBody FuelInputDTO inputDTO){
        FuelResponseDTO fuelResponseDTO = fuelService.create(inputDTO);
        return ResponseEntity.ok(fuelResponseDTO);
    }

    @Autowired
    private FuelService fuelService;

    @GetMapping
    public ResponseEntity<List<FuelResponseDTO>> getAll(@RequestHeader HttpHeaders headers){
        return ResponseEntity.ok(fuelService.findAll());
    }

    @GetMapping("/search")
        public ResponseEntity<List<FuelResponseDTO>> searchByDescription(@RequestParam(name = "description", required = false) String description){
        if(description == null || description.isBlank()){
             return ResponseEntity.ok(fuelService.findAll());
        }
        return ResponseEntity.ok(fuelService.findByDescription(description));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuelResponseDTO> getId(@PathVariable("id") Long id){
        return ResponseEntity.ok(fuelService.find(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long id){
        fuelService.delete(id);
    }

    @Valid
    @PutMapping()
    public ResponseEntity<FuelResponseDTO> update(@RequestBody FuelInputDTO inputDTO){
        FuelResponseDTO fuelResponseDTO = fuelService.update(inputDTO);
        return ResponseEntity.ok(fuelResponseDTO);
    }

}
