package com.br.manager.domain.shift_cachs.controller;

import com.br.manager.domain.shift_cachs.dto.ShiftSummaryResponseDTO;
import com.br.manager.domain.shift_cachs.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shifts")
public class ShiftSummaryController {

    @Autowired
    private ShiftService shiftService;

    @GetMapping("/{id}/summary")
    public ResponseEntity<ShiftSummaryResponseDTO> getShiftSummary(@PathVariable UUID id) {
        return ResponseEntity.ok(shiftService.getShiftSummary(id));
    }
}
