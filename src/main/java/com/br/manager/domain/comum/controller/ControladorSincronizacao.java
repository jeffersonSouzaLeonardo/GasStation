package com.br.manager.domain.comum.controller;

import com.br.manager.domain.comum.dto.SincronizacaoDTO;
import com.br.manager.domain.comum.entity.Sincronizacao;
import com.br.manager.domain.comum.service.ServicoSincronizacao;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sincronizacao")
public class ControladorSincronizacao {

    @Autowired
    private ServicoSincronizacao sincronizacaoService;

    @Valid
    @PutMapping
    public ResponseEntity<Sincronizacao> update(@RequestBody SincronizacaoDTO sincronizacaoDTO) {
        return ResponseEntity.ok(sincronizacaoService.update(sincronizacaoDTO));
    }
}
