package com.br.manager.domain.sale_receipt.controller;

import com.br.manager.domain.sale_receipt.dto.SalePaymentInputDTO;
import com.br.manager.domain.sale_receipt.dto.SalePaymentResponseDTO;
import com.br.manager.domain.sale_receipt.service.SalePaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sale-payments")
public class SalePaymentController {

    @Autowired
    private SalePaymentService salePaymentService;

    @PostMapping
    public ResponseEntity<SalePaymentResponseDTO> create(@Valid @RequestBody SalePaymentInputDTO inputDTO) {
        return ResponseEntity.ok(salePaymentService.create(inputDTO));
    }

    @GetMapping("/sale/{saleId}")
    public ResponseEntity<List<SalePaymentResponseDTO>> findBySale(@PathVariable UUID saleId) {
        return ResponseEntity.ok(salePaymentService.findBySale(saleId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalePaymentResponseDTO> find(@PathVariable UUID id) {
        return ResponseEntity.ok(salePaymentService.find(id));
    }
}
