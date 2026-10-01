package com.br.manager.domain.shift_cachs.controller;

import com.br.manager.domain.shift_cachs.dto.NozzleReadingInputDTO;
import com.br.manager.domain.shift_cachs.dto.NozzleReadingResponseDTO;
import com.br.manager.domain.shift_cachs.service.ShiftService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shifts")
public class ShiftNozzleReadingController {

    @Autowired
    private ShiftService shiftService;

    @PostMapping("/{id}/nozzle-readings")
    public ResponseEntity<NozzleReadingResponseDTO> registerNozzleReading(
            @PathVariable UUID id,
            @Valid @RequestBody NozzleReadingInputDTO request) {
        return ResponseEntity.ok(shiftService.registerNozzleReading(id, request));
    }
}
