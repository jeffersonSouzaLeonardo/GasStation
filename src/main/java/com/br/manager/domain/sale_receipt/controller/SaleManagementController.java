package com.br.manager.domain.sale_receipt.controller;

import com.br.manager.domain.sale_receipt.dto.SaleInputDTO;
import com.br.manager.domain.sale_receipt.dto.SaleResponseDTO;
import com.br.manager.domain.sale_receipt.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales")
public class SaleManagementController {

    @Autowired
    private SaleService saleService;

    @PostMapping
    public ResponseEntity<SaleResponseDTO> create(@Valid @RequestBody SaleInputDTO inputDTO) {
        return ResponseEntity.ok(saleService.create(inputDTO));
    }

    @PatchMapping
    public ResponseEntity<SaleResponseDTO> update(@Valid @RequestBody SaleInputDTO inputDTO) {
        return ResponseEntity.ok(saleService.update(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<SaleResponseDTO>> findAll() {
        return ResponseEntity.ok(saleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponseDTO> find(@PathVariable UUID id) {
        return ResponseEntity.ok(saleService.find(id));
    }

    @GetMapping("/station/{stationId}")
    public ResponseEntity<List<SaleResponseDTO>> findByStation(@PathVariable UUID stationId) {
        return ResponseEntity.ok(saleService.findByStation(stationId));
    }
}
