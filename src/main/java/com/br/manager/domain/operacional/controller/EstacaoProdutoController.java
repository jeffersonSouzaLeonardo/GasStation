package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.operacional.dto.EstacaoProdutoInputDTO;
import com.br.manager.domain.operacional.dto.EstacaoProdutoResponseDTO;
import com.br.manager.domain.operacional.usecase.EstacaoProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/estacao-produtos")
public class EstacaoProdutoController {

    @Autowired
    private EstacaoProdutoService estacaoProdutoService;

    @PostMapping
    public ResponseEntity<EstacaoProdutoResponseDTO> create(@Valid @RequestBody EstacaoProdutoInputDTO inputDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estacaoProdutoService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<EstacaoProdutoResponseDTO>> getAll() {
        return ResponseEntity.ok(estacaoProdutoService.findAll());
    }

    @GetMapping("/busca")
    public ResponseEntity<List<EstacaoProdutoResponseDTO>> buscarPorDescricao(@RequestParam String descricao) {
        return ResponseEntity.ok(estacaoProdutoService.findByDescricao(descricao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstacaoProdutoResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(estacaoProdutoService.find(id));
    }

    @PutMapping
    public ResponseEntity<EstacaoProdutoResponseDTO> update(@Valid @RequestBody EstacaoProdutoInputDTO inputDTO) {
        return ResponseEntity.ok(estacaoProdutoService.update(inputDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        estacaoProdutoService.delete(id);
    }
}
