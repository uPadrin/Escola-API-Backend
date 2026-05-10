package com.escola.api.repository;

import com.escola.api.entity.Aluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Optional<Aluno> findByMatricula(String matricula);
    Optional<Aluno> findByEmail(String email);
    boolean existsByMatricula(String matricula);
    boolean existsByEmail(String email);

    Page<Aluno> findByAtivo(boolean ativo, Pageable pageable);

    @Query("SELECT a FROM Aluno a WHERE LOWER(a.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Page<Aluno> findByNomeContainingIgnoreCase(@Param("nome") String nome, Pageable pageable);

    @Query("SELECT a FROM Aluno a WHERE LOWER(a.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND a.ativo = :ativo")
    Page<Aluno> findByNomeContainingIgnoreCaseAndAtivo(
            @Param("nome") String nome,
            @Param("ativo") boolean ativo,
            Pageable pageable
    );
}
