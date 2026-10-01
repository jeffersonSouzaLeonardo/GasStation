package com.br.manager.domain.shift_cachs.controller;

import com.br.manager.domain.shift_cachs.dto.CashMovementInputDTO;
import com.br.manager.domain.shift_cachs.dto.CashMovementResponseDTO;
import com.br.manager.domain.shift_cachs.service.ShiftService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shifts")
public class ShiftCashMovementController {

    @Autowired
    private ShiftService shiftService;

    @PostMapping("/{id}/cash-movements")
    public ResponseEntity<CashMovementResponseDTO> registerCashMovement(
            @PathVariable UUID id,
            @Valid @RequestBody CashMovementInputDTO request) {
        return ResponseEntity.ok(shiftService.registerCashMovement(id, request));
    }
}
