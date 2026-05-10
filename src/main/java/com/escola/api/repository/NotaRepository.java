package com.escola.api.repository;

import com.escola.api.entity.Nota;
import com.escola.api.enums.Semestre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {

        Optional<Nota> findByAlunoIdAndDisciplinaIdAndSemestreAndAno(
                        Long alunoId, Long disciplinaId, Semestre semestre, Integer ano);

        List<Nota> findByAlunoIdAndAno(Long alunoId, Integer ano);

        @Query("SELECT AVG(n.mediaFinal) FROM Nota n WHERE n.aluno.id = :alunoId AND n.ano = :ano")
        Double calcularMediaGeralAluno(@Param("alunoId") Long alunoId, @Param("ano") Integer ano);

        Page<Nota> findByAlunoId(Long alunoId, Pageable pageable);

        Page<Nota> findByAlunoIdAndAno(Long alunoId, Integer ano, Pageable pageable);

        Page<Nota> findByDisciplinaIdAndSemestreAndAno(
                        Long disciplinaId, Semestre semestre, Integer ano, Pageable pageable);

        Page<Nota> findByProfessorId(Long professorId, Pageable pageable);
}
