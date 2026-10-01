package com.br.manager.domain.sale_receipt.controller;

import com.br.manager.domain.sale_receipt.dto.SaleRefundInputDTO;
import com.br.manager.domain.sale_receipt.dto.SaleRefundResponseDTO;
import com.br.manager.domain.sale_receipt.service.SaleRefundService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sale-refunds")
public class SaleRefundController {

    @Autowired
    private SaleRefundService saleRefundService;

    @PostMapping
    public ResponseEntity<SaleRefundResponseDTO> create(@Valid @RequestBody SaleRefundInputDTO inputDTO) {
        return ResponseEntity.ok(saleRefundService.create(inputDTO));
    }

    @GetMapping("/sale/{saleId}")
    public ResponseEntity<List<SaleRefundResponseDTO>> findBySale(@PathVariable UUID saleId) {
        return ResponseEntity.ok(saleRefundService.findBySale(saleId));
    }
}
