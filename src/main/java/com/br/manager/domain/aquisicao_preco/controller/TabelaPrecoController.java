package com.br.manager.domain.aquisicao_preco.controller;

import com.br.manager.domain.aquisicao_preco.dto.TabelaPrecoInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.TabelaPrecoResponseDTO;
import com.br.manager.domain.aquisicao_preco.service.TabelaPrecoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tabela-preco")
public class TabelaPrecoController {

    @Autowired
    private TabelaPrecoService precoTabelaService;

    @Valid
    @PostMapping
    public ResponseEntity<TabelaPrecoResponseDTO> create(@RequestBody TabelaPrecoInputDTO inputDTO) {
        return ResponseEntity.ok(precoTabelaService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<TabelaPrecoResponseDTO>> getAll(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(precoTabelaService.findAll());
    }

    @GetMapping("/busca")
    public ResponseEntity<List<TabelaPrecoResponseDTO>> buscarPorDescricao(
            @RequestParam(name = "descricao", required = false) String descricao,
            @RequestParam(name = "nome", required = false) String nome) {
        String buscaValor = descricao != null ? descricao : nome;
        if (buscaValor == null || buscaValor.isBlank()) {
            return ResponseEntity.ok(precoTabelaService.findAll());
        }
        return ResponseEntity.ok(precoTabelaService.findByDescricao(buscaValor));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TabelaPrecoResponseDTO> getId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(precoTabelaService.find(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") UUID id) {
        precoTabelaService.delete(id);
    }

    @Valid
    @PatchMapping
    public ResponseEntity<TabelaPrecoResponseDTO> update(@RequestBody TabelaPrecoInputDTO inputDTO) {
        return ResponseEntity.ok(precoTabelaService.update(inputDTO));
    }
}
