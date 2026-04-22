package com.escola.api.controller;

import com.escola.api.config.PageableUtils;
import com.escola.api.dto.BoletimResponse;
import com.escola.api.dto.NotaRequest;
import com.escola.api.dto.NotaResponse;
import com.escola.api.dto.PageResponse;
import com.escola.api.enums.Semestre;
import com.escola.api.service.NotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// =====================================================
// NotaController.java
// Versão com paginação nas listagens.
// Boletim não é paginado — retorna o relatório completo.
// =====================================================

@RestController
@RequestMapping("/api/notas")
@RequiredArgsConstructor
@Tag(name = "Notas", description = "Lançamento e consulta de notas com paginação")
public class NotaController {

    private final NotaService notaService;

    @GetMapping("/aluno/{alunoId}")
    @Operation(summary = "Listar notas de um aluno com paginação")
    public ResponseEntity<PageResponse<NotaResponse>> listarPorAluno(
            @PathVariable Long alunoId,
            @RequestParam(defaultValue = "0")           int page,
            @RequestParam(defaultValue = "10")          int size,
            @RequestParam(defaultValue = "lancadoEm")   String sort,
            @RequestParam(defaultValue = "desc")        String direction
    ) {
        Pageable pageable = PageableUtils.of(page, size, sort, direction);
        return ResponseEntity.ok(notaService.listarPorAluno(alunoId, pageable));
    }

    @GetMapping("/aluno/{alunoId}/ano/{ano}")
    @Operation(summary = "Listar notas de um aluno em um ano com paginação")
    public ResponseEntity<PageResponse<NotaResponse>> listarPorAlunoEAno(
            @PathVariable Long alunoId,
            @PathVariable Integer ano,
            @RequestParam(defaultValue = "0")           int page,
            @RequestParam(defaultValue = "10")          int size,
            @RequestParam(defaultValue = "lancadoEm")   String sort,
            @RequestParam(defaultValue = "desc")        String direction
    ) {
        Pageable pageable = PageableUtils.of(page, size, sort, direction);
        return ResponseEntity.ok(notaService.listarPorAlunoEAno(alunoId, ano, pageable));
    }

    @GetMapping("/disciplina/{disciplinaId}")
    @Operation(summary = "Listar notas de uma disciplina por semestre/ano com paginação")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'DIRETOR', 'SECRETARIO')")
    public ResponseEntity<PageResponse<NotaResponse>> listarPorDisciplina(
            @PathVariable Long disciplinaId,
            @RequestParam Semestre semestre,
            @RequestParam int ano,
            @RequestParam(defaultValue = "0")           int page,
            @RequestParam(defaultValue = "20")          int size,
            @RequestParam(defaultValue = "alunoNome")   String sort,
            @RequestParam(defaultValue = "asc")         String direction
    ) {
        Pageable pageable = PageableUtils.of(page, size, sort, direction);
        return ResponseEntity.ok(notaService.listarPorDisciplina(disciplinaId, semestre, ano, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar nota por ID")
    public ResponseEntity<NotaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(notaService.buscarPorId(id));
    }

    @GetMapping("/boletim/aluno/{alunoId}/ano/{ano}")
    @Operation(
        summary = "Gerar boletim completo do aluno (sem paginação)",
        description = "Retorna todas as notas do aluno no ano para o relatório completo."
    )
    public ResponseEntity<BoletimResponse> gerarBoletim(
            @PathVariable Long alunoId,
            @PathVariable Integer ano) {
        return ResponseEntity.ok(notaService.gerarBoletim(alunoId, ano));
    }

    @PostMapping
    @Operation(summary = "Lançar nota")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<NotaResponse> lancar(@RequestBody @Valid NotaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notaService.lancar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar nota lançada")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<NotaResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid NotaRequest request) {
        return ResponseEntity.ok(notaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover nota")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'DIRETOR')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        notaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
