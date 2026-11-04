package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.operacional.dto.CombustivelInputDTO;
import com.br.manager.domain.operacional.dto.CombustivelResponseDTO;
import com.br.manager.domain.operacional.usecase.CombustivelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/combustivel")
public class CombustivelController {

    @Valid
    @PostMapping
    public ResponseEntity<CombustivelResponseDTO> create(@RequestBody CombustivelInputDTO inputDTO){
        CombustivelResponseDTO combustivelResponseDTO = combustivelService.create(inputDTO);
        return ResponseEntity.ok(combustivelResponseDTO);
    }

    @Autowired
    private CombustivelService combustivelService;

    @GetMapping
    public ResponseEntity<List<CombustivelResponseDTO>> getAll(@RequestHeader HttpHeaders headers){
        return ResponseEntity.ok(combustivelService.findAll());
    }

    @GetMapping("/busca")
        public ResponseEntity<List<CombustivelResponseDTO>> buscarPorDescricao(@RequestParam(name = "descricao", required = false) String descricao){
        if(descricao == null || descricao.isBlank()){
             return ResponseEntity.ok(combustivelService.findAll());
        }
        return ResponseEntity.ok(combustivelService.findByDescricao(descricao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CombustivelResponseDTO> getId(@PathVariable("id") Long id){
        return ResponseEntity.ok(combustivelService.find(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long id){
        combustivelService.delete(id);
    }

    @Valid
    @PutMapping()
    public ResponseEntity<CombustivelResponseDTO> update(@RequestBody CombustivelInputDTO inputDTO){
        CombustivelResponseDTO combustivelResponseDTO = combustivelService.update(inputDTO);
        return ResponseEntity.ok(combustivelResponseDTO);
    }

}
