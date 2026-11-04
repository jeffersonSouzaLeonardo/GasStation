package com.br.manager.domain.organizacao.controller;

import com.br.manager.domain.organizacao.dto.VinculoUsuarioInputDTO;
import com.br.manager.domain.organizacao.dto.VinculoUsuarioResponseDTO;
import com.br.manager.domain.organizacao.usecase.VinculoUsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/vinculo-usuario")
public class VinculoUsuarioController {

    @Autowired
    private VinculoUsuarioService usuarioVinculoService;

    @Valid
    @PostMapping
    public ResponseEntity<VinculoUsuarioResponseDTO> create(@RequestBody VinculoUsuarioInputDTO inputDTO) {
        return ResponseEntity.ok(usuarioVinculoService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<VinculoUsuarioResponseDTO>> getAll(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(usuarioVinculoService.findAll());
    }

    @GetMapping("/busca")
    public ResponseEntity<List<VinculoUsuarioResponseDTO>> buscarPorDescricao(
            @RequestParam(name = "descricao", required = false) String descricao,
            @RequestParam(name = "nome", required = false) String nome,
            @RequestParam(name = "cargo", required = false) String cargo) {
        String buscaValor = descricao != null ? descricao : nome != null ? nome : cargo;
        if (buscaValor == null || buscaValor.isBlank()) {
            return ResponseEntity.ok(usuarioVinculoService.findAll());
        }
        return ResponseEntity.ok(usuarioVinculoService.findByDescricao(buscaValor));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VinculoUsuarioResponseDTO> getId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(usuarioVinculoService.find(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") UUID id) {
        usuarioVinculoService.delete(id);
    }

    @Valid
    @PutMapping
    public ResponseEntity<VinculoUsuarioResponseDTO> update(@RequestBody VinculoUsuarioInputDTO inputDTO) {
        return ResponseEntity.ok(usuarioVinculoService.update(inputDTO));
    }
}
