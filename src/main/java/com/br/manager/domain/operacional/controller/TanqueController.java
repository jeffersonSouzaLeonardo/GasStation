package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.operacional.dto.TanqueInputDTO;
import com.br.manager.domain.operacional.dto.TanqueResponseDTO;
import com.br.manager.domain.operacional.usecase.TanqueService;
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
@RequestMapping("/api/tanques")
public class TanqueController {

    @Autowired
    private TanqueService tanqueService;

    @PostMapping
    public ResponseEntity<TanqueResponseDTO> create(@Valid @RequestBody TanqueInputDTO inputDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tanqueService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<TanqueResponseDTO>> getAll() {
        return ResponseEntity.ok(tanqueService.findAll());
    }

    @GetMapping("/busca")
    public ResponseEntity<List<TanqueResponseDTO>> buscarPorDescricao(@RequestParam String descricao) {
        return ResponseEntity.ok(tanqueService.findByDescricao(descricao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TanqueResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(tanqueService.find(id));
    }

    @PutMapping
    public ResponseEntity<TanqueResponseDTO> update(@Valid @RequestBody TanqueInputDTO inputDTO) {
        return ResponseEntity.ok(tanqueService.update(inputDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        tanqueService.delete(id);
    }
}
