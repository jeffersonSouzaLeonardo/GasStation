package com.br.manager.domain.organization.controller;

import com.br.manager.domain.organization.dto.UserLinkInputDTO;
import com.br.manager.domain.organization.dto.UserLinkResponseDTO;
import com.br.manager.domain.organization.usecase.UserLinkService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user-link")
public class UserLinkController {

    @Autowired
    private UserLinkService userLinkService;

    @Valid
    @PostMapping
    public ResponseEntity<UserLinkResponseDTO> create(@RequestBody UserLinkInputDTO inputDTO) {
        return ResponseEntity.ok(userLinkService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<UserLinkResponseDTO>> getAll(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(userLinkService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserLinkResponseDTO>> searchByDescription(
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "role", required = false) String role) {
        String searchValue = description != null ? description : name != null ? name : role;
        if (searchValue == null || searchValue.isBlank()) {
            return ResponseEntity.ok(userLinkService.findAll());
        }
        return ResponseEntity.ok(userLinkService.findByDescription(searchValue));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserLinkResponseDTO> getId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(userLinkService.find(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") UUID id) {
        userLinkService.delete(id);
    }

    @Valid
    @PutMapping
    public ResponseEntity<UserLinkResponseDTO> update(@RequestBody UserLinkInputDTO inputDTO) {
        return ResponseEntity.ok(userLinkService.update(inputDTO));
    }
}
