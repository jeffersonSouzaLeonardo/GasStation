package com.br.manager.domain.organization.controller;

import com.br.manager.domain.organization.dto.UserInputDTO;
import com.br.manager.domain.organization.dto.UserResponseDTO;
import com.br.manager.domain.organization.usecase.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Valid
    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@RequestBody UserInputDTO inputDTO) {
        return ResponseEntity.ok(userService.create(inputDTO));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAll(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDTO>> searchByDescription(
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "name", required = false) String name) {
        String searchValue = description != null ? description : name;
        if (searchValue == null || searchValue.isBlank()) {
            return ResponseEntity.ok(userService.findAll());
        }
        return ResponseEntity.ok(userService.findByDescription(searchValue));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(userService.find(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") UUID id) {
        userService.delete(id);
    }

    @Valid
    @PutMapping
    public ResponseEntity<UserResponseDTO> update(@RequestBody UserInputDTO inputDTO) {
        return ResponseEntity.ok(userService.update(inputDTO));
    }
}
