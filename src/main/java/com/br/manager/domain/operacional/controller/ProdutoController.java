package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.operacional.dto.ProdutoInputDTO;
import com.br.manager.domain.operacional.dto.ProdutoResponseDTO;
import com.br.manager.domain.operacional.usecase.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> create(@Valid @RequestBody ProdutoInputDTO inputDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> getAll() {
        return ResponseEntity.ok(produtoService.findAll());
    }

    @GetMapping("/combustivel")
    public ResponseEntity<List<ProdutoResponseDTO>> getAllCombustivel() {
        return ResponseEntity.ok(produtoService.findProdutoTypeCombustivel());
    }

    @GetMapping("/busca")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorDescricao(@RequestParam String descricao) {
        return ResponseEntity.ok(produtoService.findByDescricao(descricao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(produtoService.find(id));
    }

    @PutMapping
    public ResponseEntity<ProdutoResponseDTO> update(@Valid @RequestBody ProdutoInputDTO inputDTO) {
        return ResponseEntity.ok(produtoService.update(inputDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        produtoService.delete(id);
    }
}
