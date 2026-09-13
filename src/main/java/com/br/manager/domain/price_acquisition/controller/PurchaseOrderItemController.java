package com.br.manager.domain.price_acquisition.controller;

import com.br.manager.domain.price_acquisition.dto.PurchaseOrderItemInputDTO;
import com.br.manager.domain.price_acquisition.dto.PurchaseOrderItemResponseDTO;
import com.br.manager.domain.price_acquisition.service.PurchaseOrderItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/purchase-order-item")
public class PurchaseOrderItemController {

    @Autowired
    private PurchaseOrderItemService purchaseOrderItemService;

    @Valid
    @PostMapping
    public ResponseEntity<PurchaseOrderItemResponseDTO> create(@RequestBody PurchaseOrderItemInputDTO inputDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseOrderItemService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrderItemResponseDTO>> getAll() {
        return ResponseEntity.ok(purchaseOrderItemService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderItemResponseDTO> getId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(purchaseOrderItemService.findById(id));
    }

    @GetMapping("/by-purchase-order/{purchaseOrderId}")
    public ResponseEntity<List<PurchaseOrderItemResponseDTO>> getByPurchaseOrder(@PathVariable("purchaseOrderId") UUID purchaseOrderId) {
        return ResponseEntity.ok(purchaseOrderItemService.findByPurchaseOrderId(purchaseOrderId));
    }

    @Valid
    @PatchMapping
    public ResponseEntity<PurchaseOrderItemResponseDTO> update(@RequestBody PurchaseOrderItemInputDTO inputDTO) {
        return ResponseEntity.ok(purchaseOrderItemService.update(inputDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") UUID id) {
        purchaseOrderItemService.delete(id);
    }
}
