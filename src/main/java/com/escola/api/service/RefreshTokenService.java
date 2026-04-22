package com.escola.api.service;

import com.escola.api.entity.RefreshToken;
import com.escola.api.entity.Usuario;
import com.escola.api.exception.BusinessException;
import com.escola.api.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

// =====================================================
// RefreshTokenService.java
// Gerencia a criação, validação e remoção de
// refresh tokens persistidos no banco.
// =====================================================

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // Duração do refresh token em ms — padrão: 7 dias
    @Value("${app.jwt.refresh-expiration:604800000}")
    private long refreshExpiration;

    /**
     * Cria um novo refresh token para o usuário.
     * Invalida qualquer token anterior do mesmo usuário
     * (cada usuário tem apenas 1 sessão ativa por vez).
     */
    @Transactional
    public RefreshToken criar(Usuario usuario) {
        // Remove tokens anteriores do usuário (logout automático de sessões antigas)
        refreshTokenRepository.deleteByUsuario(usuario);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .usuario(usuario)
                .expiracao(Instant.now().plusMillis(refreshExpiration))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Valida o token recebido na requisição de refresh.
     * Lança exceção se inválido ou expirado.
     */
    @Transactional
    public RefreshToken validar(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Refresh token inválido ou não encontrado."));

        if (refreshToken.isExpirado()) {
            // Remove do banco e pede novo login
            refreshTokenRepository.delete(refreshToken);
            throw new BusinessException(
                "Sessão expirada. Faça login novamente. [REFRESH_EXPIRED]"
            );
        }

        return refreshToken;
    }

    /**
     * Invalida o refresh token (logout).
     */
    @Transactional
    public void revogar(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshTokenRepository::delete);
    }

    /**
     * Remove todos os tokens de um usuário (logout global).
     */
    @Transactional
    public void revogarTodos(Usuario usuario) {
        refreshTokenRepository.deleteByUsuario(usuario);
    }

    /**
     * Limpeza automática de tokens expirados.
     * Executa todo dia à meia-noite.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void limparExpirados() {
        refreshTokenRepository.deleteAllExpired();
    }
}
