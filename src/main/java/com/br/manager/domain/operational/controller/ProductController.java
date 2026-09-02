package com.br.manager.domain.operational.controller;

import com.br.manager.domain.operational.dto.ProductInputDTO;
import com.br.manager.domain.operational.dto.ProductResponseDTO;
import com.br.manager.domain.operational.usecase.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductInputDTO inputDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> searchByDescription(@RequestParam String description) {
        return ResponseEntity.ok(productService.findByDescription(description));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.find(id));
    }

    @PutMapping
    public ResponseEntity<ProductResponseDTO> update(@Valid @RequestBody ProductInputDTO inputDTO) {
        return ResponseEntity.ok(productService.update(inputDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        productService.delete(id);
    }
}
