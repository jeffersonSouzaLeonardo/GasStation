package com.br.manager.domain.organizacao.controller;

import com.br.manager.domain.organizacao.dto.UsuarioInputDTO;
import com.br.manager.domain.organizacao.dto.UsuarioResponseDTO;
import com.br.manager.domain.organizacao.usecase.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuario")
public class UsuarioUIController {

    @Autowired
    private UsuarioService usuarioService;

    @Valid
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> create(@RequestBody UsuarioInputDTO inputDTO) {
        return ResponseEntity.ok(usuarioService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> getAll(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/busca")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarPorDescricao(
            @RequestParam(name = "descricao", required = false) String descricao,
            @RequestParam(name = "nome", required = false) String nome) {
        String buscaValor = descricao != null ? descricao : nome;
        if (buscaValor == null || buscaValor.isBlank()) {
            return ResponseEntity.ok(usuarioService.findAll());
        }
        return ResponseEntity.ok(usuarioService.findByDescricao(buscaValor));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> getId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(usuarioService.find(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") UUID id) {
        usuarioService.delete(id);
    }

    @Valid
    @PutMapping
    public ResponseEntity<UsuarioResponseDTO> update(@RequestBody UsuarioInputDTO inputDTO) {
        return ResponseEntity.ok(usuarioService.update(inputDTO));
    }
}
