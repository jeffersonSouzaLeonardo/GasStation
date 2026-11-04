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
@RequestMapping("/usuario-sincronizacao")
public class UsuarioController {

    private final String ENTITY_NAME = "USUARIO";

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> getAll(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(usuarioService.buscarAlteradosDesdeSincronizacao(ENTITY_NAME));
    }

}
