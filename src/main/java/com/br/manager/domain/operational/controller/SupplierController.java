package com.br.manager.domain.operational.controller;

import com.br.manager.domain.operational.dto.SupplierInputDTO;
import com.br.manager.domain.operational.dto.SupplierResponseDTO;
import com.br.manager.domain.operational.usecase.SupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {
    @Autowired private SupplierService supplierService;
    @PostMapping public ResponseEntity<SupplierResponseDTO> create(@Valid @RequestBody SupplierInputDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(supplierService.create(dto)); }
    @GetMapping public ResponseEntity<List<SupplierResponseDTO>> getAll() { return ResponseEntity.ok(supplierService.findAll()); }
    @GetMapping("/search") public ResponseEntity<List<SupplierResponseDTO>> search(@RequestParam String description) { return ResponseEntity.ok(supplierService.findByDescription(description)); }
    @GetMapping("/{id}") public ResponseEntity<SupplierResponseDTO> getById(@PathVariable UUID id) { return ResponseEntity.ok(supplierService.find(id)); }
    @PutMapping public ResponseEntity<SupplierResponseDTO> update(@Valid @RequestBody SupplierInputDTO dto) { return ResponseEntity.ok(supplierService.update(dto)); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { supplierService.delete(id); }
}
