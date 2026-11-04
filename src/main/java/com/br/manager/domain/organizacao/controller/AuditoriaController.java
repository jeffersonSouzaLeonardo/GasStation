package com.br.manager.domain.organizacao.controller;

import com.br.manager.domain.organizacao.dto.AuditoriaInputDTO;
import com.br.manager.domain.organizacao.dto.AuditoriaResponseDTO;
import com.br.manager.domain.organizacao.usecase.AuditoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {

    @Autowired
    private AuditoriaService auditoriaService;

    @Valid
    @PostMapping
    public ResponseEntity<AuditoriaResponseDTO> create(@RequestBody AuditoriaInputDTO inputDTO) {
        return ResponseEntity.ok(auditoriaService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<AuditoriaResponseDTO>> getAll(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(auditoriaService.findAll());
    }

    @GetMapping("/busca")
    public ResponseEntity<List<AuditoriaResponseDTO>> buscarPorDescricao(
            @RequestParam(name = "descricao", required = false) String descricao,
            @RequestParam(name = "nome", required = false) String nome,
            @RequestParam(name = "entityName", required = false) String entityName) {
        String buscaValor = descricao != null ? descricao : nome != null ? nome : entityName;
        if (buscaValor == null || buscaValor.isBlank()) {
            return ResponseEntity.ok(auditoriaService.findAll());
        }
        return ResponseEntity.ok(auditoriaService.findByDescricao(buscaValor));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditoriaResponseDTO> getId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(auditoriaService.find(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") UUID id) {
        auditoriaService.delete(id);
    }

    @Valid
    @PutMapping
    public ResponseEntity<AuditoriaResponseDTO> update(@RequestBody AuditoriaInputDTO inputDTO) {
        return ResponseEntity.ok(auditoriaService.update(inputDTO));
    }
}
