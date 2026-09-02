package com.br.manager.domain.operational.controller;

import com.br.manager.domain.operational.dto.PumpInputDTO;
import com.br.manager.domain.operational.dto.PumpResponseDTO;
import com.br.manager.domain.operational.usecase.PumpService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pumps")
public class PumpController {

    @Autowired
    private PumpService pumpService;

    @PostMapping
    public ResponseEntity<PumpResponseDTO> create(@Valid @RequestBody PumpInputDTO inputDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pumpService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<PumpResponseDTO>> getAll() {
        return ResponseEntity.ok(pumpService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<PumpResponseDTO>> searchByDescription(@RequestParam String description) {
        return ResponseEntity.ok(pumpService.findByDescription(description));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PumpResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(pumpService.find(id));
    }

    @PutMapping
    public ResponseEntity<PumpResponseDTO> update(@Valid @RequestBody PumpInputDTO inputDTO) {
        return ResponseEntity.ok(pumpService.update(inputDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        pumpService.delete(id);
    }
}
