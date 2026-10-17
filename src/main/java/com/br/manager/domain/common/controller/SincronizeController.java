package com.br.manager.domain.common.controller;

import com.br.manager.domain.common.SincronizeDTO;
import com.br.manager.domain.common.entity.Sincronize;
import com.br.manager.domain.common.service.SincronizeService;
import com.br.manager.domain.organization.dto.UserInputDTO;
import com.br.manager.domain.organization.dto.UserResponseDTO;
import com.br.manager.domain.organization.usecase.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sincronize")
public class SincronizeController {

    @Autowired
    private SincronizeService sincronizeService;

    @Valid
    @PutMapping
    public ResponseEntity<Sincronize> update(@RequestBody SincronizeDTO sincronizeDTO) {
        return ResponseEntity.ok(sincronizeService.update(sincronizeDTO));
    }
}
