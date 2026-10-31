package com.br.manager.domain.shift_cachs.controller;

import com.br.manager.domain.shift_cachs.dto.CashSessionCloseInputDTO;
import com.br.manager.domain.shift_cachs.dto.CashSessionInputDTO;
import com.br.manager.domain.shift_cachs.dto.CashSessionResponseDTO;
import com.br.manager.domain.shift_cachs.service.CashSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cash-sessions")
public class CashSessionController {

    private final CashSessionService cashSessionService;

    public CashSessionController(CashSessionService cashSessionService) {
        this.cashSessionService = cashSessionService;
    }

    @PostMapping("/open")
    public ResponseEntity<CashSessionResponseDTO> open(@Valid @RequestBody CashSessionInputDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cashSessionService.open(request));
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<CashSessionResponseDTO> close(@PathVariable UUID id,
                                                        @Valid @RequestBody(required = false) CashSessionCloseInputDTO request) {
        return ResponseEntity.ok(cashSessionService.close(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CashSessionResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(cashSessionService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<CashSessionResponseDTO>> findByShift(@RequestParam UUID shiftId) {
        return ResponseEntity.ok(cashSessionService.findByShift(shiftId));
    }
}
