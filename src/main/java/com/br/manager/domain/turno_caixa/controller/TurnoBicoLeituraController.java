package com.br.manager.domain.turno_caixa.controller;

import com.br.manager.domain.turno_caixa.dto.BicoLeituraInputDTO;
import com.br.manager.domain.turno_caixa.dto.BicoLeituraResponseDTO;
import com.br.manager.domain.turno_caixa.service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/turnos")
public class TurnoBicoLeituraController {

    @Autowired
    private TurnoService turnoService;

    @PostMapping("/{id}/leituras-bico")
    public ResponseEntity<BicoLeituraResponseDTO> registerBicoLeitura(
            @PathVariable UUID id,
            @Valid @RequestBody BicoLeituraInputDTO request) {
        return ResponseEntity.ok(turnoService.registerBicoLeitura(id, request));
    }
}
