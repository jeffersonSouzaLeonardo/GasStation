package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.operacional.dto.VeiculoClienteInputDTO;
import com.br.manager.domain.operacional.dto.VeiculoClienteResponseDTO;
import com.br.manager.domain.operacional.usecase.VeiculoClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/veiculos-cliente")
public class VeiculoClienteController {
    @Autowired private VeiculoClienteService clienteVeiculoService;
    @PostMapping public ResponseEntity<VeiculoClienteResponseDTO> create(@Valid @RequestBody VeiculoClienteInputDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(clienteVeiculoService.create(dto)); }
    @GetMapping public ResponseEntity<List<VeiculoClienteResponseDTO>> getAll() { return ResponseEntity.ok(clienteVeiculoService.findAll()); }
    @GetMapping("/busca") public ResponseEntity<List<VeiculoClienteResponseDTO>> busca(@RequestParam String descricao) { return ResponseEntity.ok(clienteVeiculoService.findByDescricao(descricao)); }
    @GetMapping("/{id}") public ResponseEntity<VeiculoClienteResponseDTO> getById(@PathVariable UUID id) { return ResponseEntity.ok(clienteVeiculoService.find(id)); }
    @PutMapping public ResponseEntity<VeiculoClienteResponseDTO> update(@Valid @RequestBody VeiculoClienteInputDTO dto) { return ResponseEntity.ok(clienteVeiculoService.update(dto)); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { clienteVeiculoService.delete(id); }
}
