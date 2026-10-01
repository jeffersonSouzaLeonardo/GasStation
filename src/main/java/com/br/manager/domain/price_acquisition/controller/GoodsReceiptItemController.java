package com.br.manager.domain.price_acquisition.controller;

import com.br.manager.domain.price_acquisition.dto.GoodsReceiptItemInputDTO;
import com.br.manager.domain.price_acquisition.dto.GoodsReceiptItemResponseDTO;
import com.br.manager.domain.price_acquisition.service.GoodsReceiptItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/goods-receipt-item")
public class GoodsReceiptItemController {

    @Autowired
    private GoodsReceiptItemService goodsReceiptItemService;

    @Valid
    @PostMapping
    public ResponseEntity<GoodsReceiptItemResponseDTO> create(@RequestBody GoodsReceiptItemInputDTO inputDTO) {
        return ResponseEntity.ok(goodsReceiptItemService.saveGoodsReceiptItem(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<GoodsReceiptItemResponseDTO>> getAll(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(goodsReceiptItemService.findAll());
    }

    @GetMapping("/by-goods-receipt/{goodsReceiptId}")
    public ResponseEntity<List<GoodsReceiptItemResponseDTO>> getByGoodsReceipt(@PathVariable("goodsReceiptId") UUID goodsReceiptId) {
        return ResponseEntity.ok(goodsReceiptItemService.findByGoodsReceipt(goodsReceiptId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoodsReceiptItemResponseDTO> getId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(goodsReceiptItemService.find(id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") UUID id) {
        goodsReceiptItemService.delete(id);
    }

    @Valid
    @PatchMapping
    public ResponseEntity<GoodsReceiptItemResponseDTO> update(@RequestBody GoodsReceiptItemInputDTO inputDTO) {
        return ResponseEntity.ok(goodsReceiptItemService.update(inputDTO));
    }

    @PostMapping("/goods-receipt/{goodsReceiptId}")
    public ResponseEntity<List<GoodsReceiptItemResponseDTO>> saveByGoodsReceipt(@PathVariable("goodsReceiptId") UUID goodsReceiptId,
                                                                             @RequestBody List<GoodsReceiptItemInputDTO> inputDTOs) {
        return ResponseEntity.ok(goodsReceiptItemService.saveGoodsReceiptItems(goodsReceiptId, inputDTOs));
    }
}
