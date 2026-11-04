package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.operacional.dto.BombaInputDTO;
import com.br.manager.domain.operacional.dto.BombaResponseDTO;
import com.br.manager.domain.operacional.usecase.BombaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bombas")
public class BombaController {

    @Autowired
    private BombaService bombaService;

    @PostMapping
    public ResponseEntity<BombaResponseDTO> create(@Valid @RequestBody BombaInputDTO inputDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bombaService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<BombaResponseDTO>> getAll() {
        return ResponseEntity.ok(bombaService.findAll());
    }

    @GetMapping("/busca")
    public ResponseEntity<List<BombaResponseDTO>> buscarPorDescricao(@RequestParam String descricao) {
        return ResponseEntity.ok(bombaService.findByDescricao(descricao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BombaResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(bombaService.find(id));
    }

    @PutMapping
    public ResponseEntity<BombaResponseDTO> update(@Valid @RequestBody BombaInputDTO inputDTO) {
        return ResponseEntity.ok(bombaService.update(inputDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        bombaService.delete(id);
    }
}
