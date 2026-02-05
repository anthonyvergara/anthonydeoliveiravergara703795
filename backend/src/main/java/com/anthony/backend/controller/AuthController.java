package com.anthony.backend.controller;

import com.anthony.backend.application.service.AuthService;
import com.anthony.backend.controller.dto.AuthResponse;
import com.anthony.backend.controller.dto.LoginRequest;
import com.anthony.backend.controller.dto.RefreshTokenRequest;
import com.anthony.backend.controller.dto.RegisterRequest;
import com.anthony.backend.domain.exception.BaseExceptionController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "API para autenticação e registro de usuários")
public class AuthController extends BaseExceptionController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário", description = "Endpoint restrito para ADMIN criar novos usuários")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Autenticar usuário e obter tokens (access e refresh)")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(
        summary = "Renovar access token",
        description = "Envia o refresh token para obter um novo access token sem precisar fazer login novamente"
    )
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
    }
}

