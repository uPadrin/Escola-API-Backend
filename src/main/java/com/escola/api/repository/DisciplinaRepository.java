package com.escola.api.repository;

import com.escola.api.entity.Disciplina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {

    Optional<Disciplina> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);

    Page<Disciplina> findByAtiva(boolean ativa, Pageable pageable);

    Page<Disciplina> findByProfessorId(Long professorId, Pageable pageable);

    List<Disciplina> findByAtiva(boolean ativa);
    List<Disciplina> findByProfessorId(Long professorId);
}
