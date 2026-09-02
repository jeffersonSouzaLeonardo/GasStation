package com.br.manager.domain.operational.controller;

import com.br.manager.domain.operational.dto.CustomerInputDTO;
import com.br.manager.domain.operational.dto.CustomerResponseDTO;
import com.br.manager.domain.operational.usecase.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired private CustomerService customerService;
    @PostMapping public ResponseEntity<CustomerResponseDTO> create(@Valid @RequestBody CustomerInputDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(dto)); }
    @GetMapping public ResponseEntity<List<CustomerResponseDTO>> getAll() { return ResponseEntity.ok(customerService.findAll()); }
    @GetMapping("/search") public ResponseEntity<List<CustomerResponseDTO>> search(@RequestParam String description) { return ResponseEntity.ok(customerService.findByDescription(description)); }
    @GetMapping("/{id}") public ResponseEntity<CustomerResponseDTO> getById(@PathVariable UUID id) { return ResponseEntity.ok(customerService.find(id)); }
    @PutMapping public ResponseEntity<CustomerResponseDTO> update(@Valid @RequestBody CustomerInputDTO dto) { return ResponseEntity.ok(customerService.update(dto)); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { customerService.delete(id); }
}
