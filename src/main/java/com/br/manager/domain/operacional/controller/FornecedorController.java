package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.operacional.dto.FornecedorInputDTO;
import com.br.manager.domain.operacional.dto.FornecedorResponseDTO;
import com.br.manager.domain.operacional.usecase.FornecedorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/fornecedores")
public class FornecedorController {
    @Autowired private FornecedorService fornecedorService;
    @PostMapping public ResponseEntity<FornecedorResponseDTO> create(@Valid @RequestBody FornecedorInputDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(fornecedorService.create(dto)); }
    @GetMapping public ResponseEntity<List<FornecedorResponseDTO>> getAll() { return ResponseEntity.ok(fornecedorService.findAll()); }
    @GetMapping("/busca") public ResponseEntity<List<FornecedorResponseDTO>> busca(@RequestParam String descricao) { return ResponseEntity.ok(fornecedorService.findByDescricao(descricao)); }
    @GetMapping("/{id}") public ResponseEntity<FornecedorResponseDTO> getById(@PathVariable UUID id) { return ResponseEntity.ok(fornecedorService.find(id)); }
    @PutMapping public ResponseEntity<FornecedorResponseDTO> update(@Valid @RequestBody FornecedorInputDTO dto) { return ResponseEntity.ok(fornecedorService.update(dto)); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { fornecedorService.delete(id); }
}
