package com.br.manager.domain.price_acquisition.controller;

import com.br.manager.domain.price_acquisition.dto.PriceItemInputDTO;
import com.br.manager.domain.price_acquisition.dto.PriceItemResponseDTO;
import com.br.manager.domain.price_acquisition.service.PriceItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/price-item")
public class PriceItemController {

    @Autowired
    private PriceItemService priceItemService;

    @Valid
    @PostMapping
    public ResponseEntity<PriceItemResponseDTO> create(@RequestBody PriceItemInputDTO inputDTO) {
        return ResponseEntity.ok(priceItemService.savePriceItem(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<PriceItemResponseDTO>> getAll(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(priceItemService.findAll());
    }

    @GetMapping("/by-price-table/{priceTableId}")
    public ResponseEntity<List<PriceItemResponseDTO>> getByPriceTable(@PathVariable("priceTableId") UUID priceTableId) {
        return ResponseEntity.ok(priceItemService.findByPriceTable(priceTableId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PriceItemResponseDTO> getId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(priceItemService.find(id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") UUID id) {
        priceItemService.delete(id);
    }

    @Valid
    @PatchMapping
    public ResponseEntity<PriceItemResponseDTO> update(@RequestBody PriceItemInputDTO inputDTO) {
        return ResponseEntity.ok(priceItemService.update(inputDTO));
    }

    // Batch save for a given price table
    @PostMapping("/price-table/{priceTableId}")
    public ResponseEntity<List<PriceItemResponseDTO>> saveByPriceTable(@PathVariable("priceTableId") UUID priceTableId,
                                                                       @RequestBody List<PriceItemInputDTO> inputDTOs) {
        return ResponseEntity.ok(priceItemService.savePriceItems(priceTableId, inputDTOs));
    }
}
