package com.br.manager.domain.turno_caixa.controller;

import com.br.manager.domain.turno_caixa.dto.TurnoResumoResponseDTO;
import com.br.manager.domain.turno_caixa.service.TurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/turnos")
public class TurnoResumoController {

    @Autowired
    private TurnoService turnoService;

    @GetMapping("/{id}/resumo")
    public ResponseEntity<TurnoResumoResponseDTO> getTurnoResumo(@PathVariable UUID id) {
        return ResponseEntity.ok(turnoService.getTurnoResumo(id));
    }
}
