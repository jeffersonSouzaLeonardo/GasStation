package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.operacional.dto.ClienteInputDTO;
import com.br.manager.domain.operacional.dto.ClienteResponseDTO;
import com.br.manager.domain.operacional.usecase.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    @Autowired private ClienteService clienteService;
    @PostMapping public ResponseEntity<ClienteResponseDTO> create(@Valid @RequestBody ClienteInputDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.create(dto)); }
    @GetMapping public ResponseEntity<List<ClienteResponseDTO>> getAll() { return ResponseEntity.ok(clienteService.findAll()); }
    @GetMapping("/busca") public ResponseEntity<List<ClienteResponseDTO>> busca(@RequestParam String descricao) { return ResponseEntity.ok(clienteService.findByDescricao(descricao)); }
    @GetMapping("/{id}") public ResponseEntity<ClienteResponseDTO> getById(@PathVariable UUID id) { return ResponseEntity.ok(clienteService.find(id)); }
    @PutMapping public ResponseEntity<ClienteResponseDTO> update(@Valid @RequestBody ClienteInputDTO dto) { return ResponseEntity.ok(clienteService.update(dto)); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { clienteService.delete(id); }
}
