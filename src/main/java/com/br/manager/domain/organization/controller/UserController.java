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
@RequestMapping("/user-synchronize")
public class UserController {

    private final String ENTITY_NAME = "USER";

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAll(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(userService.findChangedSinceSynchronize(ENTITY_NAME));
    }



}
