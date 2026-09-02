package com.br.manager.domain.operational.controller;

import com.br.manager.domain.operational.dto.TankInputDTO;
import com.br.manager.domain.operational.dto.TankResponseDTO;
import com.br.manager.domain.operational.usecase.TankService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tanks")
public class TankController {

    @Autowired
    private TankService tankService;

    @PostMapping
    public ResponseEntity<TankResponseDTO> create(@Valid @RequestBody TankInputDTO inputDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tankService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<TankResponseDTO>> getAll() {
        return ResponseEntity.ok(tankService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<TankResponseDTO>> searchByDescription(@RequestParam String description) {
        return ResponseEntity.ok(tankService.findByDescription(description));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TankResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(tankService.find(id));
    }

    @PutMapping
    public ResponseEntity<TankResponseDTO> update(@Valid @RequestBody TankInputDTO inputDTO) {
        return ResponseEntity.ok(tankService.update(inputDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        tankService.delete(id);
    }
}
