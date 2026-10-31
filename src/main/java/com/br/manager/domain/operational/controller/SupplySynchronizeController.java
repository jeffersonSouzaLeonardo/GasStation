package com.br.manager.domain.operational.controller;

import com.br.manager.domain.operational.dto.SupplyRequestDTO;
import com.br.manager.domain.operational.usecase.SupplyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/supply-synchronize")
public class SupplySynchronizeController {

    private final SupplyService supplyService;

    public SupplySynchronizeController(SupplyService supplyService) {
        this.supplyService = supplyService;
    }

    @PostMapping
    public ResponseEntity<Void> receive(@Valid @RequestBody List<SupplyRequestDTO> supplies) {
        supplyService.saveAll(supplies);
        return ResponseEntity.ok().build();
    }
}
