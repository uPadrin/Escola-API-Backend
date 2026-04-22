package com.escola.api.service;

import com.escola.api.dto.AuthResponse;
import com.escola.api.dto.LoginRequest;
import com.escola.api.dto.RefreshTokenRequest;
import com.escola.api.dto.UsuarioRequest;
import com.escola.api.dto.UsuarioResponse;
import com.escola.api.entity.RefreshToken;
import com.escola.api.entity.Usuario;
import com.escola.api.exception.BusinessException;
import com.escola.api.repository.UsuarioRepository;
import com.escola.api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// =====================================================
// AuthService.java — atualizado com refresh token
// =====================================================

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    /**
     * Login: autentica, gera access token + refresh token.
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Autentica via Spring Security (verifica senha BCrypt)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Gera o access token JWT (duração curta: 15 min em prod)
        String accessToken = jwtService.gerarToken(usuario);

        // Gera o refresh token (duração longa: 7 dias)
        RefreshToken refreshToken = refreshTokenService.criar(usuario);

        return buildResponse(usuario, accessToken, refreshToken.getToken());
    }

    /**
     * Refresh: valida o refresh token e emite novo access token.
     * O refresh token em si não é renovado (rotação simples).
     */
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        // Valida o refresh token (lança exceção se expirado/inválido)
        RefreshToken refreshToken = refreshTokenService.validar(request.getRefreshToken());
        Usuario usuario = refreshToken.getUsuario();

        // Emite novo access token
        String novoAccessToken = jwtService.gerarToken(usuario);

        return buildResponse(usuario, novoAccessToken, refreshToken.getToken());
    }

    /**
     * Logout: invalida o refresh token no banco.
     * O access token expira naturalmente (stateless).
     */
    @Transactional
    public void logout(RefreshTokenRequest request) {
        refreshTokenService.revogar(request.getRefreshToken());
    }

    /**
     * Registrar: cria novo usuário no sistema.
     */
    @Transactional
    public UsuarioResponse registrar(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + request.getEmail());
        }

        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .role(request.getRole())
                .ativo(true)
                .build();

        return toResponse(usuarioRepository.save(usuario));
    }

    // ─── Helpers ─────────────────────────────────────────

    private AuthResponse buildResponse(Usuario u, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .tipo("Bearer")
                .id(u.getId())
                .nome(u.getNome())
                .email(u.getEmail())
                .role(u.getRole())
                .build();
    }

    public static UsuarioResponse toResponse(Usuario u) {
        return UsuarioResponse.builder()
                .id(u.getId())
                .nome(u.getNome())
                .email(u.getEmail())
                .role(u.getRole())
                .ativo(u.isAtivo())
                .criadoEm(u.getCriadoEm())
                .build();
    }
}
