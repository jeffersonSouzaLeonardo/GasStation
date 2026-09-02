package com.br.manager.domain.organization.controller;

import com.br.manager.domain.organization.dto.AuditInputDTO;
import com.br.manager.domain.organization.dto.AuditResponseDTO;
import com.br.manager.domain.organization.usecase.AuditService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @Valid
    @PostMapping
    public ResponseEntity<AuditResponseDTO> create(@RequestBody AuditInputDTO inputDTO) {
        return ResponseEntity.ok(auditService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<AuditResponseDTO>> getAll(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(auditService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<AuditResponseDTO>> searchByDescription(
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "entityName", required = false) String entityName) {
        String searchValue = description != null ? description : name != null ? name : entityName;
        if (searchValue == null || searchValue.isBlank()) {
            return ResponseEntity.ok(auditService.findAll());
        }
        return ResponseEntity.ok(auditService.findByDescription(searchValue));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditResponseDTO> getId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(auditService.find(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") UUID id) {
        auditService.delete(id);
    }

    @Valid
    @PutMapping
    public ResponseEntity<AuditResponseDTO> update(@RequestBody AuditInputDTO inputDTO) {
        return ResponseEntity.ok(auditService.update(inputDTO));
    }
}
