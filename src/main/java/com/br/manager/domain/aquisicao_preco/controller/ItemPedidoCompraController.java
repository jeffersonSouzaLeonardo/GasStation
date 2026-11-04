package com.br.manager.domain.aquisicao_preco.controller;

import com.br.manager.domain.aquisicao_preco.dto.ItemPedidoCompraInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemPedidoCompraResponseDTO;
import com.br.manager.domain.aquisicao_preco.service.ItemPedidoCompraService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/item-pedido-compra")
public class ItemPedidoCompraController {

    @Autowired
    private ItemPedidoCompraService compraOrderItemService;

    @Valid
    @PostMapping
    public ResponseEntity<ItemPedidoCompraResponseDTO> create(@RequestBody ItemPedidoCompraInputDTO inputDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compraOrderItemService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<ItemPedidoCompraResponseDTO>> getAll() {
        return ResponseEntity.ok(compraOrderItemService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemPedidoCompraResponseDTO> getId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(compraOrderItemService.findById(id));
    }

    @GetMapping("/por-pedido-compra/{compraOrderId}")
    public ResponseEntity<List<ItemPedidoCompraResponseDTO>> getByPedidoCompra(@PathVariable("compraOrderId") UUID compraOrderId) {
        return ResponseEntity.ok(compraOrderItemService.findByPedidoCompraId(compraOrderId));
    }

    @Valid
    @PatchMapping
    public ResponseEntity<ItemPedidoCompraResponseDTO> update(@RequestBody ItemPedidoCompraInputDTO inputDTO) {
        return ResponseEntity.ok(compraOrderItemService.update(inputDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") UUID id) {
        compraOrderItemService.delete(id);
    }
}
