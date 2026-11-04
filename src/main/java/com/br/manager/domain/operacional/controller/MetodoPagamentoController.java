package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.operacional.dto.MetodoPagamentoInputDTO;
import com.br.manager.domain.operacional.dto.MetodoPagamentoResponseDTO;
import com.br.manager.domain.operacional.usecase.MetodoPagamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/metodos-pagamento")
public class MetodoPagamentoController {
    @Autowired private MetodoPagamentoService pagamentoMethodService;
    @PostMapping public ResponseEntity<MetodoPagamentoResponseDTO> create(@Valid @RequestBody MetodoPagamentoInputDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(pagamentoMethodService.create(dto)); }
    @GetMapping public ResponseEntity<List<MetodoPagamentoResponseDTO>> getAll() { return ResponseEntity.ok(pagamentoMethodService.findAll()); }
    @GetMapping("/busca") public ResponseEntity<List<MetodoPagamentoResponseDTO>> busca(@RequestParam String descricao) { return ResponseEntity.ok(pagamentoMethodService.findByDescricao(descricao)); }
    @GetMapping("/{id}") public ResponseEntity<MetodoPagamentoResponseDTO> getById(@PathVariable UUID id) { return ResponseEntity.ok(pagamentoMethodService.find(id)); }
    @PutMapping public ResponseEntity<MetodoPagamentoResponseDTO> update(@Valid @RequestBody MetodoPagamentoInputDTO dto) { return ResponseEntity.ok(pagamentoMethodService.update(dto)); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { pagamentoMethodService.delete(id); }
}
