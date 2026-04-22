package com.escola.api.controller;

import com.escola.api.config.PageableUtils;
import com.escola.api.dto.PageResponse;
import com.escola.api.dto.UsuarioRequest;
import com.escola.api.dto.UsuarioResponse;
import com.escola.api.enums.Role;
import com.escola.api.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// =====================================================
// UsuarioController.java
// Versão com paginação no endpoint de listagem.
//
// GET /api/usuarios?page=0&size=10&sort=nome&direction=asc
// GET /api/usuarios/role/PROFESSOR?page=0&size=10
// GET /api/usuarios/professores  ← sem paginação (para selects)
// =====================================================

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento de usuários com paginação")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Listar todos os usuários com paginação")
    @PreAuthorize("hasAnyRole('DIRETOR', 'SECRETARIO')")
    public ResponseEntity<PageResponse<UsuarioResponse>> listar(
            @RequestParam(defaultValue = "0")    int page,
            @RequestParam(defaultValue = "10")   int size,
            @RequestParam(defaultValue = "nome") String sort,
            @RequestParam(defaultValue = "asc")  String direction
    ) {
        Pageable pageable = PageableUtils.of(page, size, sort, direction);
        return ResponseEntity.ok(usuarioService.listar(pageable));
    }

    @GetMapping("/role/{role}")
    @Operation(summary = "Listar usuários por role com paginação")
    @PreAuthorize("hasAnyRole('DIRETOR', 'SECRETARIO')")
    public ResponseEntity<PageResponse<UsuarioResponse>> listarPorRole(
            @PathVariable Role role,
            @RequestParam(defaultValue = "0")    int page,
            @RequestParam(defaultValue = "10")   int size,
            @RequestParam(defaultValue = "nome") String sort,
            @RequestParam(defaultValue = "asc")  String direction
    ) {
        Pageable pageable = PageableUtils.of(page, size, sort, direction);
        return ResponseEntity.ok(usuarioService.listarPorRole(role, pageable));
    }

    @GetMapping("/professores")
    @Operation(
        summary = "Listar todos os professores (sem paginação)",
        description = "Retorna a lista completa de professores para uso em selects e dropdowns."
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UsuarioResponse>> listarProfessores() {
        return ResponseEntity.ok(usuarioService.listarProfessores());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID")
    @PreAuthorize("hasAnyRole('DIRETOR', 'SECRETARIO')")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário")
    @PreAuthorize("hasAnyRole('DIRETOR', 'SECRETARIO')")
    public ResponseEntity<UsuarioResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid UsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.atualizar(id, request));
    }

    @PatchMapping("/{id}/ativar")
    @Operation(summary = "Ativar usuário")
    @PreAuthorize("hasAnyRole('DIRETOR', 'SECRETARIO')")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        usuarioService.alterarStatus(id, true);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar usuário")
    @PreAuthorize("hasAnyRole('DIRETOR', 'SECRETARIO')")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        usuarioService.alterarStatus(id, false);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário — apenas DIRETOR")
    @PreAuthorize("hasRole('DIRETOR')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
