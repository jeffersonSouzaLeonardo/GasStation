package com.br.manager.domain.organizacao.controller;

import com.br.manager.domain.organizacao.dto.EmpresaInputDTO;
import com.br.manager.domain.organizacao.dto.EmpresaResponseDTO;
import com.br.manager.domain.organizacao.usecase.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/empresa")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    @Valid
    @PostMapping
    public ResponseEntity<EmpresaResponseDTO> create(@RequestBody EmpresaInputDTO inputDTO) {
        return ResponseEntity.ok(empresaService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<EmpresaResponseDTO>> getAll(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(empresaService.findAll());
    }

    @GetMapping("/busca")
    public ResponseEntity<List<EmpresaResponseDTO>> buscarPorDescricao(
            @RequestParam(name = "descricao", required = false) String descricao,
            @RequestParam(name = "nome", required = false) String nome) {
        String buscaValor = descricao != null ? descricao : nome;
        if (buscaValor == null || buscaValor.isBlank()) {
            return ResponseEntity.ok(empresaService.findAll());
        }
        return ResponseEntity.ok(empresaService.findByDescricao(buscaValor));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> getId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(empresaService.find(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") UUID id) {
        empresaService.delete(id);
    }

    @Valid
    @PutMapping
    public ResponseEntity<EmpresaResponseDTO> update(@RequestBody EmpresaInputDTO inputDTO) {
        return ResponseEntity.ok(empresaService.update(inputDTO));
    }
}
