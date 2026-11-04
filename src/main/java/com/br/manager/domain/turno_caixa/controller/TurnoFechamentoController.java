package com.br.manager.domain.turno_caixa.controller;

import com.br.manager.domain.turno_caixa.dto.TurnoFechamentoRequestDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoResponseDTO;
import com.br.manager.domain.turno_caixa.service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/turnos")
public class TurnoFechamentoController {

    @Autowired
    private TurnoService turnoService;

    @PostMapping("/{id}/fechar")
    public ResponseEntity<TurnoResponseDTO> closeTurno(
            @PathVariable UUID id,
            @Valid @RequestBody TurnoFechamentoRequestDTO request) {
        return ResponseEntity.ok(turnoService.closeTurno(id, request));
    }
}
