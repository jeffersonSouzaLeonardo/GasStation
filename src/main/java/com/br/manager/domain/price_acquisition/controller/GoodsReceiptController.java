package com.br.manager.domain.price_acquisition.controller;

import com.br.manager.domain.price_acquisition.dto.GoodsReceiptInputDTO;
import com.br.manager.domain.price_acquisition.dto.GoodsReceiptResponseDTO;
import com.br.manager.domain.price_acquisition.service.GoodsReceiptService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/goods-receipt")
public class GoodsReceiptController {

    @Autowired
    private GoodsReceiptService goodsReceiptService;

    @Valid
    @PostMapping
    public ResponseEntity<GoodsReceiptResponseDTO> create(@RequestBody GoodsReceiptInputDTO inputDTO) {
        return ResponseEntity.ok(goodsReceiptService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<GoodsReceiptResponseDTO>> getAll(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(goodsReceiptService.findAll());
    }

    @GetMapping("/by-supplier/{supplierId}")
    public ResponseEntity<List<GoodsReceiptResponseDTO>> getBySupplier(@PathVariable("supplierId") UUID supplierId) {
        return ResponseEntity.ok(goodsReceiptService.findBySupplier(supplierId));
    }

    @GetMapping("/by-purchase-order/{purchaseOrderId}")
    public ResponseEntity<List<GoodsReceiptResponseDTO>> getByPurchaseOrder(@PathVariable("purchaseOrderId") UUID purchaseOrderId) {
        return ResponseEntity.ok(goodsReceiptService.findByPurchaseOrder(purchaseOrderId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoodsReceiptResponseDTO> getId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(goodsReceiptService.find(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") UUID id) {
        goodsReceiptService.delete(id);
    }

    @Valid
    @PatchMapping
    public ResponseEntity<GoodsReceiptResponseDTO> update(@RequestBody GoodsReceiptInputDTO inputDTO) {
        return ResponseEntity.ok(goodsReceiptService.update(inputDTO));
    }
}
