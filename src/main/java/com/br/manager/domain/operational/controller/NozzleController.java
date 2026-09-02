package com.br.manager.domain.operational.controller;

import com.br.manager.domain.operational.dto.NozzleInputDTO;
import com.br.manager.domain.operational.dto.NozzleResponseDTO;
import com.br.manager.domain.operational.usecase.NozzleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/nozzles")
public class NozzleController {

    @Autowired
    private NozzleService nozzleService;

    @PostMapping
    public ResponseEntity<NozzleResponseDTO> create(@Valid @RequestBody NozzleInputDTO inputDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(nozzleService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<NozzleResponseDTO>> getAll() {
        return ResponseEntity.ok(nozzleService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<NozzleResponseDTO>> searchByDescription(@RequestParam String description) {
        return ResponseEntity.ok(nozzleService.findByDescription(description));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NozzleResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(nozzleService.find(id));
    }

    @PutMapping
    public ResponseEntity<NozzleResponseDTO> update(@Valid @RequestBody NozzleInputDTO inputDTO) {
        return ResponseEntity.ok(nozzleService.update(inputDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        nozzleService.delete(id);
    }
}
