package com.escola.api.repository;

import com.escola.api.entity.RefreshToken;
import com.escola.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Busca o token para validar na requisição de refresh
    Optional<RefreshToken> findByToken(String token);

    // Remove TODOS os tokens de um usuário (logout de todos os dispositivos)
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.usuario = :usuario")
    void deleteByUsuario(@Param("usuario") Usuario usuario);

    // Remove tokens expirados (para limpeza periódica)
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiracao < CURRENT_TIMESTAMP")
    void deleteAllExpired();
}
