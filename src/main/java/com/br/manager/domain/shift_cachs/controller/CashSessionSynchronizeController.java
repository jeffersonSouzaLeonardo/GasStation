package com.br.manager.domain.shift_cachs.controller;

import com.br.manager.domain.shift_cachs.dto.CashSessionSyncDTO;
import com.br.manager.domain.shift_cachs.service.CashSessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cash-session-synchronize")
public class CashSessionSynchronizeController {

    private final CashSessionService cashSessionService;

    public CashSessionSynchronizeController(CashSessionService cashSessionService) {
        this.cashSessionService = cashSessionService;
    }

    @PostMapping
    public ResponseEntity<Void> receive(@Valid @RequestBody List<CashSessionSyncDTO> sessions) {
        cashSessionService.saveAll(sessions);
        return ResponseEntity.ok().build();
    }
}
