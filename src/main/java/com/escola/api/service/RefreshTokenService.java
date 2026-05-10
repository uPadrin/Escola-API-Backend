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

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-expiration:604800000}")
    private long refreshExpiration;


    @Transactional
    public RefreshToken criar(Usuario usuario) {
        refreshTokenRepository.deleteByUsuario(usuario);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .usuario(usuario)
                .expiracao(Instant.now().plusMillis(refreshExpiration))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }


    @Transactional
    public RefreshToken validar(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Refresh token inválido ou não encontrado."));

        if (refreshToken.isExpirado()) {
            refreshTokenRepository.delete(refreshToken);
            throw new BusinessException(
                "Sessão expirada. Faça login novamente. [REFRESH_EXPIRED]"
            );
        }

        return refreshToken;
    }


    @Transactional
    public void revogar(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshTokenRepository::delete);
    }


    @Transactional
    public void revogarTodos(Usuario usuario) {
        refreshTokenRepository.deleteByUsuario(usuario);
    }


    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void limparExpirados() {
        refreshTokenRepository.deleteAllExpired();
    }
}
