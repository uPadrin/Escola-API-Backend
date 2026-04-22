package com.escola.api.controller;

import com.escola.api.dto.AuthResponse;
import com.escola.api.dto.LoginRequest;
import com.escola.api.dto.RefreshTokenRequest;
import com.escola.api.dto.UsuarioRequest;
import com.escola.api.dto.UsuarioResponse;
import com.escola.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// =====================================================
// AuthController.java — atualizado
//
// Novos endpoints:
//   POST /api/auth/refresh  — renova o access token
//   POST /api/auth/logout   — invalida a sessão
// =====================================================

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login, logout e renovação de token")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
        summary = "Realizar login",
        description = "Retorna access token (JWT, 15 min) e refresh token (7 dias). " +
                      "Armazene o refreshToken no localStorage para renovação automática."
    )
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(
        summary = "Renovar access token",
        description = "Usa o refreshToken para emitir um novo accessToken sem precisar fazer login novamente. " +
                      "O refreshToken permanece o mesmo até expirar (7 dias) ou ser invalidado pelo logout."
    )
    public ResponseEntity<AuthResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    @Operation(
        summary = "Realizar logout",
        description = "Invalida o refreshToken no banco. O accessToken expira naturalmente (stateless)."
    )
    public ResponseEntity<Void> logout(@RequestBody @Valid RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/registrar")
    @Operation(
        summary = "Registrar novo usuário",
        description = "Requer role DIRETOR ou SECRETARIO."
    )
    @PreAuthorize("hasAnyRole('DIRETOR', 'SECRETARIO')")
    public ResponseEntity<UsuarioResponse> registrar(@RequestBody @Valid UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(request));
    }
}
