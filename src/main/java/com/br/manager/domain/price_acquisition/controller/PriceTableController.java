package com.br.manager.domain.price_acquisition.controller;

import com.br.manager.domain.price_acquisition.dto.PriceTableInputDTO;
import com.br.manager.domain.price_acquisition.dto.PriceTableResponseDTO;
import com.br.manager.domain.price_acquisition.service.PriceTableService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/price-table")
public class PriceTableController {

    @Autowired
    private PriceTableService priceTableService;

    @Valid
    @PostMapping
    public ResponseEntity<PriceTableResponseDTO> create(@RequestBody PriceTableInputDTO inputDTO) {
        return ResponseEntity.ok(priceTableService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<PriceTableResponseDTO>> getAll(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(priceTableService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<PriceTableResponseDTO>> searchByDescription(
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "name", required = false) String name) {
        String searchValue = description != null ? description : name;
        if (searchValue == null || searchValue.isBlank()) {
            return ResponseEntity.ok(priceTableService.findAll());
        }
        return ResponseEntity.ok(priceTableService.findByDescription(searchValue));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PriceTableResponseDTO> getId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(priceTableService.find(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") UUID id) {
        priceTableService.delete(id);
    }

    @Valid
    @PutMapping
    public ResponseEntity<PriceTableResponseDTO> update(@RequestBody PriceTableInputDTO inputDTO) {
        return ResponseEntity.ok(priceTableService.update(inputDTO));
    }
}
