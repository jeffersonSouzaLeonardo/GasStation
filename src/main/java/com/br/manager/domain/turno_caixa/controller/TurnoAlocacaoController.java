package com.br.manager.domain.turno_caixa.controller;

import com.br.manager.domain.turno_caixa.dto.TurnoAlocacaoInputDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoAlocacaoResponseDTO;
import com.br.manager.domain.turno_caixa.service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/turnos")
public class TurnoAlocacaoController {

    @Autowired
    private TurnoService turnoService;

    @PostMapping("/{id}/alocacoes")
    public ResponseEntity<TurnoAlocacaoResponseDTO> addAlocacao(
            @PathVariable UUID id,
            @Valid @RequestBody TurnoAlocacaoInputDTO request) {
        return ResponseEntity.ok(turnoService.addOrUpdateAlocacao(id, request));
    }
}
