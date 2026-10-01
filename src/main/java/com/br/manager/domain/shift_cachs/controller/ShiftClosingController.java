package com.br.manager.domain.shift_cachs.controller;

import com.br.manager.domain.shift_cachs.dto.ShiftCloseRequestDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftResponseDTO;
import com.br.manager.domain.shift_cachs.service.ShiftService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shifts")
public class ShiftClosingController {

    @Autowired
    private ShiftService shiftService;

    @PostMapping("/{id}/close")
    public ResponseEntity<ShiftResponseDTO> closeShift(
            @PathVariable UUID id,
            @Valid @RequestBody ShiftCloseRequestDTO request) {
        return ResponseEntity.ok(shiftService.closeShift(id, request));
    }
}
