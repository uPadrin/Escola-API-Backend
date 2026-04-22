package com.escola.api.repository;

import com.escola.api.entity.Aluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// =====================================================
// AlunoRepository.java
// Versão com suporte a paginação via Pageable.
// O Spring Data monta o SQL com LIMIT/OFFSET automaticamente.
// =====================================================

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Optional<Aluno> findByMatricula(String matricula);
    Optional<Aluno> findByEmail(String email);
    boolean existsByMatricula(String matricula);
    boolean existsByEmail(String email);

    // ── Paginados ────────────────────────────────────────

    // Lista todos os alunos paginado
    // Equivalente ao findAll(Pageable) herdado do JpaRepository
    // mas declarado explicitamente para clareza

    // Lista por status com paginação
    Page<Aluno> findByAtivo(boolean ativo, Pageable pageable);

    // Busca por nome (case insensitive) com paginação
    @Query("SELECT a FROM Aluno a WHERE LOWER(a.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Page<Aluno> findByNomeContainingIgnoreCase(@Param("nome") String nome, Pageable pageable);

    // Busca por nome + filtro de status com paginação
    @Query("SELECT a FROM Aluno a WHERE LOWER(a.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND a.ativo = :ativo")
    Page<Aluno> findByNomeContainingIgnoreCaseAndAtivo(
            @Param("nome") String nome,
            @Param("ativo") boolean ativo,
            Pageable pageable
    );
}
