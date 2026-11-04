package com.br.manager.domain.turno_caixa.controller;

import com.br.manager.domain.turno_caixa.dto.FechamentoPagamentoInputDTO;
import com.br.manager.domain.turno_caixa.dto.FechamentoPagamentoResponseDTO;
import com.br.manager.domain.turno_caixa.service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/turnos")
public class TurnoFechamentoPagamentoController {

    @Autowired
    private TurnoService turnoService;

    @PostMapping("/{id}/fechamentos-pagamento")
    public ResponseEntity<FechamentoPagamentoResponseDTO> registerFechamentoPagamento(
            @PathVariable UUID id,
            @Valid @RequestBody FechamentoPagamentoInputDTO request) {
        return ResponseEntity.ok(turnoService.registerFechamentoPagamento(id, request));
    }
}
