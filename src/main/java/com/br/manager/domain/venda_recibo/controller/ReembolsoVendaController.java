package com.br.manager.domain.venda_recibo.controller;

import com.br.manager.domain.venda_recibo.dto.VendaReembolsoInputDTO;
import com.br.manager.domain.venda_recibo.dto.VendaReembolsoResponseDTO;
import com.br.manager.domain.venda_recibo.service.ReembolsoVendaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reembolsos-venda")
public class ReembolsoVendaController {

    @Autowired
    private ReembolsoVendaService vendaReembolsoService;

    @PostMapping
    public ResponseEntity<VendaReembolsoResponseDTO> create(@Valid @RequestBody VendaReembolsoInputDTO inputDTO) {
        return ResponseEntity.ok(vendaReembolsoService.create(inputDTO));
    }

    @GetMapping("/venda/{vendaId}")
    public ResponseEntity<List<VendaReembolsoResponseDTO>> findByVenda(@PathVariable UUID vendaId) {
        return ResponseEntity.ok(vendaReembolsoService.findByVenda(vendaId));
    }
}
