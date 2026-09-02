package com.br.manager.domain.organization.controller;

import com.br.manager.domain.organization.dto.StationInputDTO;
import com.br.manager.domain.organization.dto.StationResponseDTO;
import com.br.manager.domain.organization.usecase.StationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/station")
public class StationController {

    @Autowired
    private StationService stationService;

    @Valid
    @PostMapping
    public ResponseEntity<StationResponseDTO> create(@RequestBody StationInputDTO inputDTO) {
        return ResponseEntity.ok(stationService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<StationResponseDTO>> getAll(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(stationService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<StationResponseDTO>> searchByDescription(
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "name", required = false) String name) {
        String searchValue = description != null ? description : name;
        if (searchValue == null || searchValue.isBlank()) {
            return ResponseEntity.ok(stationService.findAll());
        }
        return ResponseEntity.ok(stationService.findByDescription(searchValue));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationResponseDTO> getId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(stationService.find(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") UUID id) {
        stationService.delete(id);
    }

    @Valid
    @PutMapping
    public ResponseEntity<StationResponseDTO> update(@RequestBody StationInputDTO inputDTO) {
        return ResponseEntity.ok(stationService.update(inputDTO));
    }
}
