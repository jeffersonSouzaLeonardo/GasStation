package com.br.manager.domain.aquisicao_preco.controller;

import com.br.manager.domain.aquisicao_preco.dto.ItemRecebimentoMercadoriaInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemRecebimentoMercadoriaResponseDTO;
import com.br.manager.domain.aquisicao_preco.service.ItemRecebimentoMercadoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/item-recebimento-mercadoria")
public class ItemRecebimentoMercadoriaController {

    @Autowired
    private ItemRecebimentoMercadoriaService mercadoriaReciboItemService;

    @Valid
    @PostMapping
    public ResponseEntity<ItemRecebimentoMercadoriaResponseDTO> create(@RequestBody ItemRecebimentoMercadoriaInputDTO inputDTO) {
        return ResponseEntity.ok(mercadoriaReciboItemService.saveRecebimentoMercadoriaItem(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<ItemRecebimentoMercadoriaResponseDTO>> getAll(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(mercadoriaReciboItemService.findAll());
    }

    @GetMapping("/por-recebimento-mercadoria/{mercadoriaReciboId}")
    public ResponseEntity<List<ItemRecebimentoMercadoriaResponseDTO>> getByRecebimentoMercadoria(@PathVariable("mercadoriaReciboId") UUID mercadoriaReciboId) {
        return ResponseEntity.ok(mercadoriaReciboItemService.findByRecebimentoMercadoria(mercadoriaReciboId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemRecebimentoMercadoriaResponseDTO> getId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(mercadoriaReciboItemService.find(id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") UUID id) {
        mercadoriaReciboItemService.delete(id);
    }

    @Valid
    @PatchMapping
    public ResponseEntity<ItemRecebimentoMercadoriaResponseDTO> update(@RequestBody ItemRecebimentoMercadoriaInputDTO inputDTO) {
        return ResponseEntity.ok(mercadoriaReciboItemService.update(inputDTO));
    }

    @PostMapping("/recebimento-mercadoria/{mercadoriaReciboId}")
    public ResponseEntity<List<ItemRecebimentoMercadoriaResponseDTO>> saveByRecebimentoMercadoria(@PathVariable("mercadoriaReciboId") UUID mercadoriaReciboId,
                                                                             @RequestBody List<ItemRecebimentoMercadoriaInputDTO> inputDTOs) {
        return ResponseEntity.ok(mercadoriaReciboItemService.saveRecebimentoMercadoriaItens(mercadoriaReciboId, inputDTOs));
    }
}
