package com.br.manager.domain.turno_caixa.controller;

import com.br.manager.domain.turno_caixa.dto.TurnoAberturaRequestDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoResponseDTO;
import com.br.manager.domain.turno_caixa.service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/turnos")
public class TurnoAberturaController {

    @Autowired
    private TurnoService turnoService;

    @PostMapping("/abrir")
    public ResponseEntity<TurnoResponseDTO> abertoTurno(@Valid @RequestBody TurnoAberturaRequestDTO request) {
        return ResponseEntity.ok(turnoService.abertoTurno(request));
    }
}
