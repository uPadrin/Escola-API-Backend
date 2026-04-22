package com.escola.api.controller;

import com.escola.api.config.PageableUtils;
import com.escola.api.dto.AlunoRequest;
import com.escola.api.dto.AlunoResponse;
import com.escola.api.dto.PageResponse;
import com.escola.api.service.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// =====================================================
// AlunoController.java
// Versão com paginação no endpoint de listagem.
//
// Query params disponíveis em GET /api/alunos:
//   ?page=0          número da página (começa em 0)
//   &size=10         itens por página (máx 100)
//   &sort=nome       campo de ordenação
//   &direction=asc   direção: asc ou desc
//   &nome=joão       filtro parcial por nome
//   &apenasAtivos=true  filtra só matrículas ativas
// =====================================================

@RestController
@RequestMapping("/api/alunos")
@RequiredArgsConstructor
@Tag(name = "Alunos", description = "Gerenciamento de alunos com paginação")
public class AlunoController {

    private final AlunoService alunoService;

    @GetMapping
    @Operation(
        summary = "Listar alunos com paginação",
        description = "Suporta filtro por nome e por status. " +
                      "Parâmetros: page, size, sort, direction, nome, apenasAtivos."
    )
    @PreAuthorize("hasAnyRole('DIRETOR', 'SECRETARIO', 'PROFESSOR')")
    public ResponseEntity<PageResponse<AlunoResponse>> listar(
            // Filtros
            @Parameter(description = "Busca parcial por nome (case insensitive)")
            @RequestParam(required = false) String nome,

            @Parameter(description = "true = retorna só alunos com matrícula ativa")
            @RequestParam(defaultValue = "false") boolean apenasAtivos,

            // Paginação
            @Parameter(description = "Número da página — começa em 0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Itens por página — máximo 100")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Campo de ordenação: nome, matricula, criadoEm")
            @RequestParam(defaultValue = "nome") String sort,

            @Parameter(description = "Direção: asc ou desc")
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Pageable pageable = PageableUtils.of(page, size, sort, direction);
        return ResponseEntity.ok(alunoService.listar(nome, apenasAtivos, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar aluno por ID")
    @PreAuthorize("hasAnyRole('DIRETOR', 'SECRETARIO', 'PROFESSOR')")
    public ResponseEntity<AlunoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alunoService.buscarPorId(id));
    }

    @GetMapping("/matricula/{matricula}")
    @Operation(summary = "Buscar aluno por matrícula")
    @PreAuthorize("hasAnyRole('DIRETOR', 'SECRETARIO', 'PROFESSOR')")
    public ResponseEntity<AlunoResponse> buscarPorMatricula(@PathVariable String matricula) {
        return ResponseEntity.ok(alunoService.buscarPorMatricula(matricula));
    }

    @PostMapping
    @Operation(summary = "Criar novo aluno — matrícula gerada automaticamente")
    @PreAuthorize("hasAnyRole('DIRETOR', 'SECRETARIO')")
    public ResponseEntity<AlunoResponse> criar(@RequestBody @Valid AlunoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alunoService.criar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados do aluno")
    @PreAuthorize("hasAnyRole('DIRETOR', 'SECRETARIO')")
    public ResponseEntity<AlunoResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid AlunoRequest request) {
        return ResponseEntity.ok(alunoService.atualizar(id, request));
    }

    @PatchMapping("/{id}/ativar")
    @Operation(summary = "Ativar matrícula do aluno")
    @PreAuthorize("hasAnyRole('DIRETOR', 'SECRETARIO')")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        alunoService.alterarStatus(id, true);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Trancar matrícula do aluno")
    @PreAuthorize("hasAnyRole('DIRETOR', 'SECRETARIO')")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        alunoService.alterarStatus(id, false);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir aluno permanentemente")
    @PreAuthorize("hasRole('DIRETOR')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        alunoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
