package com.br.manager.domain.turno_caixa.controller;

import com.br.manager.domain.turno_caixa.dto.SessaoCaixaSincronizacaoDTO;
import com.br.manager.domain.turno_caixa.service.SessaoCaixaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sessao-caixa-sincronizacao")
public class SessaoCaixaControladorSincronizacao {

    private final SessaoCaixaService dinheiroSessaoService;

    public SessaoCaixaControladorSincronizacao(SessaoCaixaService dinheiroSessaoService) {
        this.dinheiroSessaoService = dinheiroSessaoService;
    }

    @PostMapping
    public ResponseEntity<Void> receive(@Valid @RequestBody List<SessaoCaixaSincronizacaoDTO> sessoes) {
        dinheiroSessaoService.saveAll(sessoes);
        return ResponseEntity.ok().build();
    }
}
