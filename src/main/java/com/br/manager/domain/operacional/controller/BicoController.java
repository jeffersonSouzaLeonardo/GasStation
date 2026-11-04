package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.operacional.dto.BicoInputDTO;
import com.br.manager.domain.operacional.dto.BicoResponseDTO;
import com.br.manager.domain.operacional.usecase.BicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bicos")
public class BicoController {

    @Autowired
    private BicoService bicoService;

    @PostMapping
    public ResponseEntity<BicoResponseDTO> create(@Valid @RequestBody BicoInputDTO inputDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bicoService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<BicoResponseDTO>> getAll() {
        return ResponseEntity.ok(bicoService.findAll());
    }

    @GetMapping("/busca")
    public ResponseEntity<List<BicoResponseDTO>> buscarPorDescricao(@RequestParam String descricao) {
        return ResponseEntity.ok(bicoService.findByDescricao(descricao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BicoResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(bicoService.find(id));
    }

    @PutMapping
    public ResponseEntity<BicoResponseDTO> update(@Valid @RequestBody BicoInputDTO inputDTO) {
        return ResponseEntity.ok(bicoService.update(inputDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        bicoService.delete(id);
    }
}
