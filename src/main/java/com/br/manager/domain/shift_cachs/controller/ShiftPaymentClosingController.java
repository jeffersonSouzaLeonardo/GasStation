package com.br.manager.domain.shift_cachs.controller;

import com.br.manager.domain.shift_cachs.dto.PaymentClosingInputDTO;
import com.br.manager.domain.shift_cachs.dto.PaymentClosingResponseDTO;
import com.br.manager.domain.shift_cachs.service.ShiftService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shifts")
public class ShiftPaymentClosingController {

    @Autowired
    private ShiftService shiftService;

    @PostMapping("/{id}/payment-closings")
    public ResponseEntity<PaymentClosingResponseDTO> registerPaymentClosing(
            @PathVariable UUID id,
            @Valid @RequestBody PaymentClosingInputDTO request) {
        return ResponseEntity.ok(shiftService.registerPaymentClosing(id, request));
    }
}
