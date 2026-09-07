package com.zara.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zara.backend.dto.LoginRequest;
import com.zara.backend.dto.RegisterRequest;
import com.zara.backend.service.AuthService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService service;

    public AuthController(
            AuthService service
    ) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request
    ) {

        try {

            return ResponseEntity.ok(
                    service.register(request)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request
    ) {

        try {

            return ResponseEntity.ok(
                    service.login(request)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(401)
                    .body(e.getMessage());
        }
    }
}