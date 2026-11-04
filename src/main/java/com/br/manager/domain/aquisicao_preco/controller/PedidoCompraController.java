package com.br.manager.domain.aquisicao_preco.controller;

import com.br.manager.domain.aquisicao_preco.dto.PedidoCompraInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.PedidoCompraResponseDTO;
import com.br.manager.domain.aquisicao_preco.service.PedidoCompraService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pedido-compra")
public class PedidoCompraController {

    @Autowired
    private PedidoCompraService compraOrderService;

    @PostMapping
    public ResponseEntity<PedidoCompraResponseDTO> create(@Valid @RequestBody PedidoCompraInputDTO inputDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compraOrderService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<PedidoCompraResponseDTO>> findAll() {
        return ResponseEntity.ok(compraOrderService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoCompraResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(compraOrderService.findById(id));
    }

    @PatchMapping()
    public ResponseEntity<PedidoCompraResponseDTO> update(@Valid @RequestBody PedidoCompraInputDTO inputDTO) {
        return ResponseEntity.ok(compraOrderService.update(inputDTO));
    }

    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<PedidoCompraResponseDTO> approve(@PathVariable UUID id) {
        return ResponseEntity.ok(compraOrderService.approve(id));
    }

    @PatchMapping("/{id}/enviar")
    public ResponseEntity<PedidoCompraResponseDTO> send(@PathVariable UUID id) {
        return ResponseEntity.ok(compraOrderService.send(id));
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<PedidoCompraResponseDTO> complete(@PathVariable UUID id) {
        return ResponseEntity.ok(compraOrderService.complete(id));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PedidoCompraResponseDTO> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(compraOrderService.cancel(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        compraOrderService.delete(id);
    }
}
