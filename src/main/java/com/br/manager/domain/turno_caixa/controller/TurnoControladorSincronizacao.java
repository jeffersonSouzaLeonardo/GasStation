package com.br.manager.domain.turno_caixa.controller;

import com.br.manager.domain.turno_caixa.dto.TurnoSincronizacaoDTO;
import com.br.manager.domain.turno_caixa.service.TurnoSincronizacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/turno-sincronizacao")
public class TurnoControladorSincronizacao {

    private final TurnoSincronizacaoService turnoSincronizacaoService;

    public TurnoControladorSincronizacao(TurnoSincronizacaoService turnoSincronizacaoService) {
        this.turnoSincronizacaoService = turnoSincronizacaoService;
    }

    @PostMapping
    public ResponseEntity<Void> receive(@Valid @RequestBody List<TurnoSincronizacaoDTO> turnos) {
        turnoSincronizacaoService.saveAll(turnos);
        return ResponseEntity.ok().build();
    }
}
