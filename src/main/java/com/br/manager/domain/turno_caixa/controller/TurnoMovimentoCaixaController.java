package com.br.manager.domain.turno_caixa.controller;

import com.br.manager.domain.turno_caixa.dto.MovimentoCaixaInputDTO;
import com.br.manager.domain.turno_caixa.dto.MovimentoCaixaResponseDTO;
import com.br.manager.domain.turno_caixa.service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/turnos")
public class TurnoMovimentoCaixaController {

    @Autowired
    private TurnoService turnoService;

    @PostMapping("/{id}/movimentos-caixa")
    public ResponseEntity<MovimentoCaixaResponseDTO> registerMovimentoCaixa(
            @PathVariable UUID id,
            @Valid @RequestBody MovimentoCaixaInputDTO request) {
        return ResponseEntity.ok(turnoService.registerMovimentoCaixa(id, request));
    }
}
