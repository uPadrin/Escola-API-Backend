package com.escola.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

// =====================================================
// RefreshToken.java
// Entidade que persiste tokens de renovação no banco.
//
// Fluxo:
//   1. Login  → gera access token (15 min) + refresh token (7 dias)
//   2. Access expirado → POST /api/auth/refresh com o refresh token
//   3. Backend valida refresh → emite novo access token
//   4. Logout → invalida o refresh token no banco
// =====================================================

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Token UUID único gerado para cada sessão
    @Column(nullable = false, unique = true, length = 200)
    private String token;

    // Usuário dono deste token
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Data/hora de expiração (7 dias por padrão)
    @Column(nullable = false)
    private Instant expiracao;

    // Verifica se o token ainda é válido
    public boolean isExpirado() {
        return Instant.now().isAfter(expiracao);
    }
}
