package com.br.manager.domain.common.controller;

import com.br.manager.domain.common.dto.SincronizeDTO;
import com.br.manager.domain.common.entity.Synchronize;
import com.br.manager.domain.common.service.SynchronizeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sincronize")
public class SynchronizeController {

    @Autowired
    private SynchronizeService synchronizeService;

    @Valid
    @PutMapping
    public ResponseEntity<Synchronize> update(@RequestBody SincronizeDTO sincronizeDTO) {
        return ResponseEntity.ok(synchronizeService.update(sincronizeDTO));
    }
}
