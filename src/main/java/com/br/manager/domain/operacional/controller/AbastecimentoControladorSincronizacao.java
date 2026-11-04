package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.operacional.dto.AbastecimentoRequestDTO;
import com.br.manager.domain.operacional.usecase.AbastecimentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/abastecimento-sincronizacao")
public class AbastecimentoControladorSincronizacao {

    private final AbastecimentoService abastecimentoService;

    public AbastecimentoControladorSincronizacao(AbastecimentoService abastecimentoService) {
        this.abastecimentoService = abastecimentoService;
    }

    @PostMapping
    public ResponseEntity<Void> receive(@Valid @RequestBody List<AbastecimentoRequestDTO> abastecimentos) {
        abastecimentoService.saveAll(abastecimentos);
        return ResponseEntity.ok().build();
    }
}
