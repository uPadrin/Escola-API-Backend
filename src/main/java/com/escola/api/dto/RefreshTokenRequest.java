package com.escola.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// =====================================================
// DTOs do fluxo de Refresh Token
// =====================================================

// ─── Request: corpo do POST /api/auth/refresh ────────
@Data
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token é obrigatório")
    private String refreshToken;
}
