package com.br.manager.domain.shift_cachs.controller;

import com.br.manager.domain.shift_cachs.dto.ShiftAllocationInputDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftAllocationResponseDTO;
import com.br.manager.domain.shift_cachs.service.ShiftService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shifts")
public class ShiftAllocationController {

    @Autowired
    private ShiftService shiftService;

    @PostMapping("/{id}/allocations")
    public ResponseEntity<ShiftAllocationResponseDTO> addAllocation(
            @PathVariable UUID id,
            @Valid @RequestBody ShiftAllocationInputDTO request) {
        return ResponseEntity.ok(shiftService.addOrUpdateAllocation(id, request));
    }
}
