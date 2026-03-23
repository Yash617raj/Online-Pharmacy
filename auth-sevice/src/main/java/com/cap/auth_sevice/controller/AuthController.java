package com.cap.auth_sevice.controller;

import com.cap.auth_sevice.dto.*;
import com.cap.auth_sevice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    // SIGNUP
    @PostMapping("/signup")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request) {

        service.register(request);
        return ResponseEntity.ok("User Registered Successfully");
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthRequest request) {

        return ResponseEntity.ok(service.login(request));
    }
}
