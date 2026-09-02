package com.br.manager.domain.operational.controller;

import com.br.manager.domain.operational.dto.CustomerVehicleInputDTO;
import com.br.manager.domain.operational.dto.CustomerVehicleResponseDTO;
import com.br.manager.domain.operational.usecase.CustomerVehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customer-vehicles")
public class CustomerVehicleController {
    @Autowired private CustomerVehicleService customerVehicleService;
    @PostMapping public ResponseEntity<CustomerVehicleResponseDTO> create(@Valid @RequestBody CustomerVehicleInputDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(customerVehicleService.create(dto)); }
    @GetMapping public ResponseEntity<List<CustomerVehicleResponseDTO>> getAll() { return ResponseEntity.ok(customerVehicleService.findAll()); }
    @GetMapping("/search") public ResponseEntity<List<CustomerVehicleResponseDTO>> search(@RequestParam String description) { return ResponseEntity.ok(customerVehicleService.findByDescription(description)); }
    @GetMapping("/{id}") public ResponseEntity<CustomerVehicleResponseDTO> getById(@PathVariable UUID id) { return ResponseEntity.ok(customerVehicleService.find(id)); }
    @PutMapping public ResponseEntity<CustomerVehicleResponseDTO> update(@Valid @RequestBody CustomerVehicleInputDTO dto) { return ResponseEntity.ok(customerVehicleService.update(dto)); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { customerVehicleService.delete(id); }
}
