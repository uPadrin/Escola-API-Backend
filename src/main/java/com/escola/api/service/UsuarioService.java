package com.escola.api.service;

import com.escola.api.dto.PageResponse;
import com.escola.api.dto.UsuarioRequest;
import com.escola.api.dto.UsuarioResponse;
import com.escola.api.entity.Usuario;
import com.escola.api.enums.Role;
import com.escola.api.exception.BusinessException;
import com.escola.api.exception.ResourceNotFoundException;
import com.escola.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// =====================================================
// UsuarioService.java
// Versão com paginação para listagem geral e por role.
// =====================================================

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Lista todos os usuários com paginação.
     */
    public PageResponse<UsuarioResponse> listar(Pageable pageable) {
        return PageResponse.de(
            usuarioRepository.findAll(pageable).map(AuthService::toResponse)
        );
    }

    /**
     * Lista usuários por role com paginação.
     */
    public PageResponse<UsuarioResponse> listarPorRole(Role role, Pageable pageable) {
        return PageResponse.de(
            usuarioRepository.findByRole(role, pageable).map(AuthService::toResponse)
        );
    }

    /**
     * Lista professores sem paginação — usado em selects/dropdowns.
     * Retorna lista completa pois a quantidade de professores tende
     * a ser pequena e o select precisa de todos os itens.
     */
    public List<UsuarioResponse> listarProfessores() {
        return usuarioRepository.findByRole(Role.PROFESSOR,
                org.springframework.data.domain.Pageable.unpaged())
                .stream()
                .map(AuthService::toResponse)
                .collect(Collectors.toList());
    }

    public UsuarioResponse buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(AuthService::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));

        if (!usuario.getEmail().equals(request.getEmail())
                && usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já em uso: " + request.getEmail());
        }

        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setRole(request.getRole());

        if (request.getSenha() != null && !request.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        }

        return AuthService.toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void alterarStatus(Long id, boolean ativo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));
        usuario.setAtivo(ativo);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void deletar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado com id: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}
