package com.br.manager.domain.organization.controller;

import com.br.manager.domain.organization.dto.CompanyInputDTO;
import com.br.manager.domain.organization.dto.CompanyResponseDTO;
import com.br.manager.domain.organization.usecase.CompanyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @Valid
    @PostMapping
    public ResponseEntity<CompanyResponseDTO> create(@RequestBody CompanyInputDTO inputDTO) {
        return ResponseEntity.ok(companyService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponseDTO>> getAll(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(companyService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<CompanyResponseDTO>> searchByDescription(
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "name", required = false) String name) {
        String searchValue = description != null ? description : name;
        if (searchValue == null || searchValue.isBlank()) {
            return ResponseEntity.ok(companyService.findAll());
        }
        return ResponseEntity.ok(companyService.findByDescription(searchValue));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> getId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(companyService.find(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") UUID id) {
        companyService.delete(id);
    }

    @Valid
    @PutMapping
    public ResponseEntity<CompanyResponseDTO> update(@RequestBody CompanyInputDTO inputDTO) {
        return ResponseEntity.ok(companyService.update(inputDTO));
    }
}
