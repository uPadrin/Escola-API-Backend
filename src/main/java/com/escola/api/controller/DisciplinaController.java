package com.escola.api.controller;

import com.escola.api.config.PageableUtils;
import com.escola.api.dto.DisciplinaRequest;
import com.escola.api.dto.DisciplinaResponse;
import com.escola.api.dto.PageResponse;
import com.escola.api.service.DisciplinaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// =====================================================
// DisciplinaController.java
// Versão com paginação.
//
// GET /api/disciplinas?page=0&size=10&sort=nome
// GET /api/disciplinas/ativas?page=0&size=10
// GET /api/disciplinas/todas  ← sem paginação (para selects)
// =====================================================

@RestController
@RequestMapping("/api/disciplinas")
@RequiredArgsConstructor
@Tag(name = "Disciplinas", description = "Gerenciamento de disciplinas com paginação")
public class DisciplinaController {

    private final DisciplinaService disciplinaService;

    @GetMapping
    @Operation(summary = "Listar todas as disciplinas com paginação")
    public ResponseEntity<PageResponse<DisciplinaResponse>> listar(
            @RequestParam(defaultValue = "0")    int page,
            @RequestParam(defaultValue = "10")   int size,
            @RequestParam(defaultValue = "nome") String sort,
            @RequestParam(defaultValue = "asc")  String direction
    ) {
        Pageable pageable = PageableUtils.of(page, size, sort, direction);
        return ResponseEntity.ok(disciplinaService.listar(pageable));
    }

    @GetMapping("/ativas")
    @Operation(summary = "Listar disciplinas ativas com paginação")
    public ResponseEntity<PageResponse<DisciplinaResponse>> listarAtivas(
            @RequestParam(defaultValue = "0")    int page,
            @RequestParam(defaultValue = "10")   int size,
            @RequestParam(defaultValue = "nome") String sort,
            @RequestParam(defaultValue = "asc")  String direction
    ) {
        Pageable pageable = PageableUtils.of(page, size, sort, direction);
        return ResponseEntity.ok(disciplinaService.listarAtivas(pageable));
    }

    @GetMapping("/professor/{professorId}")
    @Operation(summary = "Listar disciplinas de um professor com paginação")
    public ResponseEntity<PageResponse<DisciplinaResponse>> listarPorProfessor(
            @PathVariable Long professorId,
            @RequestParam(defaultValue = "0")    int page,
            @RequestParam(defaultValue = "10")   int size,
            @RequestParam(defaultValue = "nome") String sort,
            @RequestParam(defaultValue = "asc")  String direction
    ) {
        Pageable pageable = PageableUtils.of(page, size, sort, direction);
        return ResponseEntity.ok(disciplinaService.listarPorProfessor(professorId, pageable));
    }

    @GetMapping("/todas")
    @Operation(
        summary = "Listar todas as disciplinas SEM paginação",
        description = "Use este endpoint para preencher selects e dropdowns no frontend."
    )
    public ResponseEntity<List<DisciplinaResponse>> listarTodasSemPaginacao() {
        return ResponseEntity.ok(disciplinaService.listarTodasSemPaginacao());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar disciplina por ID")
    public ResponseEntity<DisciplinaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(disciplinaService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Criar nova disciplina")
    @PreAuthorize("hasAnyRole('DIRETOR', 'SECRETARIO')")
    public ResponseEntity<DisciplinaResponse> criar(@RequestBody @Valid DisciplinaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(disciplinaService.criar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar disciplina")
    @PreAuthorize("hasAnyRole('DIRETOR', 'SECRETARIO')")
    public ResponseEntity<DisciplinaResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DisciplinaRequest request) {
        return ResponseEntity.ok(disciplinaService.atualizar(id, request));
    }

    @PatchMapping("/{id}/ativar")
    @Operation(summary = "Ativar disciplina")
    @PreAuthorize("hasAnyRole('DIRETOR', 'SECRETARIO')")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        disciplinaService.alterarStatus(id, true);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar disciplina")
    @PreAuthorize("hasAnyRole('DIRETOR', 'SECRETARIO')")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        disciplinaService.alterarStatus(id, false);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir disciplina")
    @PreAuthorize("hasAnyRole('DIRETOR', 'SECRETARIO')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        disciplinaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
