package com.br.manager.domain.aquisicao_preco.controller;

import com.br.manager.domain.aquisicao_preco.dto.RecebimentoMercadoriaInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.RecebimentoMercadoriaResponseDTO;
import com.br.manager.domain.aquisicao_preco.service.RecebimentoMercadoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recebimento-mercadoria")
public class RecebimentoMercadoriaController {

    @Autowired
    private RecebimentoMercadoriaService mercadoriaReciboService;

    @Valid
    @PostMapping
    public ResponseEntity<RecebimentoMercadoriaResponseDTO> create(@RequestBody RecebimentoMercadoriaInputDTO inputDTO) {
        return ResponseEntity.ok(mercadoriaReciboService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<RecebimentoMercadoriaResponseDTO>> getAll(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(mercadoriaReciboService.findAll());
    }

    @GetMapping("/por-fornecedor/{fornecedorId}")
    public ResponseEntity<List<RecebimentoMercadoriaResponseDTO>> getByFornecedor(@PathVariable("fornecedorId") UUID fornecedorId) {
        return ResponseEntity.ok(mercadoriaReciboService.findByFornecedor(fornecedorId));
    }

    @GetMapping("/por-pedido-compra/{compraOrderId}")
    public ResponseEntity<List<RecebimentoMercadoriaResponseDTO>> getByPedidoCompra(@PathVariable("compraOrderId") UUID compraOrderId) {
        return ResponseEntity.ok(mercadoriaReciboService.findByPedidoCompra(compraOrderId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecebimentoMercadoriaResponseDTO> getId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(mercadoriaReciboService.find(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") UUID id) {
        mercadoriaReciboService.delete(id);
    }

    @Valid
    @PatchMapping
    public ResponseEntity<RecebimentoMercadoriaResponseDTO> update(@RequestBody RecebimentoMercadoriaInputDTO inputDTO) {
        return ResponseEntity.ok(mercadoriaReciboService.update(inputDTO));
    }
}
