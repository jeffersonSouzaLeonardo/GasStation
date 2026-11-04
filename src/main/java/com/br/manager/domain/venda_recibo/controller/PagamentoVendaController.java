package com.br.manager.domain.venda_recibo.controller;

import com.br.manager.domain.venda_recibo.dto.VendaPagamentoInputDTO;
import com.br.manager.domain.venda_recibo.dto.VendaPagamentoResponseDTO;
import com.br.manager.domain.venda_recibo.service.PagamentoVendaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pagamentos-venda")
public class PagamentoVendaController {

    @Autowired
    private PagamentoVendaService vendaPagamentoService;

    @PostMapping
    public ResponseEntity<VendaPagamentoResponseDTO> create(@Valid @RequestBody VendaPagamentoInputDTO inputDTO) {
        return ResponseEntity.ok(vendaPagamentoService.create(inputDTO));
    }

    @GetMapping("/venda/{vendaId}")
    public ResponseEntity<List<VendaPagamentoResponseDTO>> findByVenda(@PathVariable UUID vendaId) {
        return ResponseEntity.ok(vendaPagamentoService.findByVenda(vendaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaPagamentoResponseDTO> find(@PathVariable UUID id) {
        return ResponseEntity.ok(vendaPagamentoService.find(id));
    }
}
