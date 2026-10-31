package com.br.manager.domain.shift_cachs.controller;

import com.br.manager.domain.shift_cachs.dto.ShiftSyncDTO;
import com.br.manager.domain.shift_cachs.service.ShiftSyncService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/shift-synchronize")
public class ShiftSynchronizeController {

    private final ShiftSyncService shiftSyncService;

    public ShiftSynchronizeController(ShiftSyncService shiftSyncService) {
        this.shiftSyncService = shiftSyncService;
    }

    @PostMapping
    public ResponseEntity<Void> receive(@Valid @RequestBody List<ShiftSyncDTO> shifts) {
        shiftSyncService.saveAll(shifts);
        return ResponseEntity.ok().build();
    }
}
