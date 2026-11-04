package com.br.manager.domain.turno_caixa.controller;

import com.br.manager.domain.turno_caixa.dto.SessaoCaixaFechamentoInputDTO;
import com.br.manager.domain.turno_caixa.dto.SessaoCaixaInputDTO;
import com.br.manager.domain.turno_caixa.dto.SessaoCaixaResponseDTO;
import com.br.manager.domain.turno_caixa.service.SessaoCaixaService;
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
@RequestMapping("/api/v1/sessoes-caixa")
public class SessaoCaixaController {

    private final SessaoCaixaService dinheiroSessaoService;

    public SessaoCaixaController(SessaoCaixaService dinheiroSessaoService) {
        this.dinheiroSessaoService = dinheiroSessaoService;
    }

    @PostMapping("/abrir")
    public ResponseEntity<SessaoCaixaResponseDTO> open(@Valid @RequestBody SessaoCaixaInputDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dinheiroSessaoService.open(request));
    }

    @PostMapping("/{id}/fechar")
    public ResponseEntity<SessaoCaixaResponseDTO> close(@PathVariable UUID id,
                                                        @Valid @RequestBody(required = false) SessaoCaixaFechamentoInputDTO request) {
        return ResponseEntity.ok(dinheiroSessaoService.close(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessaoCaixaResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(dinheiroSessaoService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<SessaoCaixaResponseDTO>> findByTurno(@RequestParam UUID turnoId) {
        return ResponseEntity.ok(dinheiroSessaoService.findByTurno(turnoId));
    }
}
