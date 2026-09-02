package com.br.manager.domain.operational.controller;

import com.br.manager.domain.operational.dto.StationProductInputDTO;
import com.br.manager.domain.operational.dto.StationProductResponseDTO;
import com.br.manager.domain.operational.usecase.StationProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/station-products")
public class StationProductController {

    @Autowired
    private StationProductService stationProductService;

    @PostMapping
    public ResponseEntity<StationProductResponseDTO> create(@Valid @RequestBody StationProductInputDTO inputDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stationProductService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<StationProductResponseDTO>> getAll() {
        return ResponseEntity.ok(stationProductService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<StationProductResponseDTO>> searchByDescription(@RequestParam String description) {
        return ResponseEntity.ok(stationProductService.findByDescription(description));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationProductResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(stationProductService.find(id));
    }

    @PutMapping
    public ResponseEntity<StationProductResponseDTO> update(@Valid @RequestBody StationProductInputDTO inputDTO) {
        return ResponseEntity.ok(stationProductService.update(inputDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        stationProductService.delete(id);
    }
}
