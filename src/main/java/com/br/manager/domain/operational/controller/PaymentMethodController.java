package com.br.manager.domain.operational.controller;

import com.br.manager.domain.operational.dto.PaymentMethodInputDTO;
import com.br.manager.domain.operational.dto.PaymentMethodResponseDTO;
import com.br.manager.domain.operational.usecase.PaymentMethodService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment-methods")
public class PaymentMethodController {
    @Autowired private PaymentMethodService paymentMethodService;
    @PostMapping public ResponseEntity<PaymentMethodResponseDTO> create(@Valid @RequestBody PaymentMethodInputDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(paymentMethodService.create(dto)); }
    @GetMapping public ResponseEntity<List<PaymentMethodResponseDTO>> getAll() { return ResponseEntity.ok(paymentMethodService.findAll()); }
    @GetMapping("/search") public ResponseEntity<List<PaymentMethodResponseDTO>> search(@RequestParam String description) { return ResponseEntity.ok(paymentMethodService.findByDescription(description)); }
    @GetMapping("/{id}") public ResponseEntity<PaymentMethodResponseDTO> getById(@PathVariable UUID id) { return ResponseEntity.ok(paymentMethodService.find(id)); }
    @PutMapping public ResponseEntity<PaymentMethodResponseDTO> update(@Valid @RequestBody PaymentMethodInputDTO dto) { return ResponseEntity.ok(paymentMethodService.update(dto)); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { paymentMethodService.delete(id); }
}
