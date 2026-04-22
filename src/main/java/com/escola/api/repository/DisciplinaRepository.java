package com.escola.api.repository;

import com.escola.api.entity.Disciplina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// =====================================================
// DisciplinaRepository.java
// Versão com suporte a paginação via Pageable.
// =====================================================

@Repository
public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {

    Optional<Disciplina> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);

    // ── Paginados ────────────────────────────────────────

    // Lista por status com paginação
    Page<Disciplina> findByAtiva(boolean ativa, Pageable pageable);

    // Lista por professor com paginação
    Page<Disciplina> findByProfessorId(Long professorId, Pageable pageable);

    // Sem paginação (para selects/dropdowns no frontend)
    List<Disciplina> findByAtiva(boolean ativa);
    List<Disciplina> findByProfessorId(Long professorId);
}
