package com.br.manager.domain.shift_cachs.controller;

import com.br.manager.domain.shift_cachs.dto.ShiftOpenRequestDTO;
import com.br.manager.domain.shift_cachs.dto.ShiftResponseDTO;
import com.br.manager.domain.shift_cachs.service.ShiftService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shifts")
public class ShiftOpeningController {

    @Autowired
    private ShiftService shiftService;

    @PostMapping("/open")
    public ResponseEntity<ShiftResponseDTO> openShift(@Valid @RequestBody ShiftOpenRequestDTO request) {
        return ResponseEntity.ok(shiftService.openShift(request));
    }
}
