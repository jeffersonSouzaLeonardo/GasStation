package com.br.manager.domain.aquisicao_preco.controller;

import com.br.manager.domain.aquisicao_preco.dto.ItemPrecoInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemPrecoResponseDTO;
import com.br.manager.domain.aquisicao_preco.service.ItemPrecoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/item-preco")
public class ItemPrecoController {

    @Autowired
    private ItemPrecoService precoItemService;

    @Valid
    @PostMapping
    public ResponseEntity<ItemPrecoResponseDTO> create(@RequestBody ItemPrecoInputDTO inputDTO) {
        return ResponseEntity.ok(precoItemService.saveItemPreco(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<ItemPrecoResponseDTO>> getAll(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(precoItemService.findAll());
    }

    @GetMapping("/por-tabela-preco/{precoTabelaId}")
    public ResponseEntity<List<ItemPrecoResponseDTO>> getByTabelaPreco(@PathVariable("precoTabelaId") UUID precoTabelaId) {
        return ResponseEntity.ok(precoItemService.findByTabelaPreco(precoTabelaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemPrecoResponseDTO> getId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(precoItemService.find(id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") UUID id) {
        precoItemService.delete(id);
    }

    @Valid
    @PatchMapping
    public ResponseEntity<ItemPrecoResponseDTO> update(@RequestBody ItemPrecoInputDTO inputDTO) {
        return ResponseEntity.ok(precoItemService.update(inputDTO));
    }

    // Batch save for a given preco tabela
    @PostMapping("/tabela-preco/{precoTabelaId}")
    public ResponseEntity<List<ItemPrecoResponseDTO>> saveByTabelaPreco(@PathVariable("precoTabelaId") UUID precoTabelaId,
                                                                       @RequestBody List<ItemPrecoInputDTO> inputDTOs) {
        return ResponseEntity.ok(precoItemService.saveItemPrecos(precoTabelaId, inputDTOs));
    }
}
