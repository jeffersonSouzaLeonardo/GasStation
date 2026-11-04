package com.br.manager.domain.organizacao.controller;

import com.br.manager.domain.organizacao.dto.EstacaoInputDTO;
import com.br.manager.domain.organizacao.dto.EstacaoResponseDTO;
import com.br.manager.domain.organizacao.usecase.EstacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/estacao")
public class EstacaoController {

    @Autowired
    private EstacaoService estacaoService;

    @Valid
    @PostMapping
    public ResponseEntity<EstacaoResponseDTO> create(@RequestBody EstacaoInputDTO inputDTO) {
        return ResponseEntity.ok(estacaoService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<EstacaoResponseDTO>> getAll(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(estacaoService.findAll());
    }

    @GetMapping("/busca")
    public ResponseEntity<List<EstacaoResponseDTO>> buscarPorDescricao(
            @RequestParam(name = "descricao", required = false) String descricao,
            @RequestParam(name = "nome", required = false) String nome) {
        String buscaValor = descricao != null ? descricao : nome;
        if (buscaValor == null || buscaValor.isBlank()) {
            return ResponseEntity.ok(estacaoService.findAll());
        }
        return ResponseEntity.ok(estacaoService.findByDescricao(buscaValor));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstacaoResponseDTO> getId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(estacaoService.find(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") UUID id) {
        estacaoService.delete(id);
    }

    @Valid
    @PutMapping
    public ResponseEntity<EstacaoResponseDTO> update(@RequestBody EstacaoInputDTO inputDTO) {
        return ResponseEntity.ok(estacaoService.update(inputDTO));
    }
}
