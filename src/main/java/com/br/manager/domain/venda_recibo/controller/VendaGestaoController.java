package com.br.manager.domain.venda_recibo.controller;

import com.br.manager.domain.venda_recibo.dto.VendaInputDTO;
import com.br.manager.domain.venda_recibo.dto.VendaResponseDTO;
import com.br.manager.domain.venda_recibo.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vendas")
public class VendaGestaoController {

    @Autowired
    private VendaService vendaService;

    @PostMapping
    public ResponseEntity<VendaResponseDTO> create(@Valid @RequestBody VendaInputDTO inputDTO) {
        return ResponseEntity.ok(vendaService.create(inputDTO));
    }

    @PatchMapping
    public ResponseEntity<VendaResponseDTO> update(@Valid @RequestBody VendaInputDTO inputDTO) {
        return ResponseEntity.ok(vendaService.update(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<VendaResponseDTO>> findAll() {
        return ResponseEntity.ok(vendaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaResponseDTO> find(@PathVariable UUID id) {
        return ResponseEntity.ok(vendaService.find(id));
    }

    @GetMapping("/estacao/{estacaoId}")
    public ResponseEntity<List<VendaResponseDTO>> findByEstacao(@PathVariable UUID estacaoId) {
        return ResponseEntity.ok(vendaService.findByEstacao(estacaoId));
    }
}
