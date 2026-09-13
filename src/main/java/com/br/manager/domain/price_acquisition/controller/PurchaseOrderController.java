package com.br.manager.domain.price_acquisition.controller;

import com.br.manager.domain.price_acquisition.dto.PurchaseOrderInputDTO;
import com.br.manager.domain.price_acquisition.dto.PurchaseOrderResponseDTO;
import com.br.manager.domain.price_acquisition.service.PurchaseOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/purchase-order")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<PurchaseOrderResponseDTO> create(@Valid @RequestBody PurchaseOrderInputDTO inputDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseOrderService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrderResponseDTO>> findAll() {
        return ResponseEntity.ok(purchaseOrderService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(purchaseOrderService.findById(id));
    }

    @GetMapping("/station/{stationId}")
    public ResponseEntity<List<PurchaseOrderResponseDTO>> findByStation(@PathVariable UUID stationId) {
        return ResponseEntity.ok(purchaseOrderService.findByStation(stationId));
    }

    @PatchMapping()
    public ResponseEntity<PurchaseOrderResponseDTO> update(@Valid @RequestBody PurchaseOrderInputDTO inputDTO) {
        return ResponseEntity.ok(purchaseOrderService.update(inputDTO));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<PurchaseOrderResponseDTO> approve(@PathVariable UUID id) {
        return ResponseEntity.ok(purchaseOrderService.approve(id));
    }

    @PatchMapping("/{id}/send")
    public ResponseEntity<PurchaseOrderResponseDTO> send(@PathVariable UUID id) {
        return ResponseEntity.ok(purchaseOrderService.send(id));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<PurchaseOrderResponseDTO> complete(@PathVariable UUID id) {
        return ResponseEntity.ok(purchaseOrderService.complete(id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<PurchaseOrderResponseDTO> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(purchaseOrderService.cancel(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        purchaseOrderService.delete(id);
    }
}
