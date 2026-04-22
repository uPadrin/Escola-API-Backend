package com.escola.api.service;

import com.escola.api.dto.BoletimResponse;
import com.escola.api.dto.NotaRequest;
import com.escola.api.dto.NotaResponse;
import com.escola.api.dto.PageResponse;
import com.escola.api.entity.Aluno;
import com.escola.api.entity.Disciplina;
import com.escola.api.entity.Nota;
import com.escola.api.entity.Usuario;
import com.escola.api.enums.Role;
import com.escola.api.enums.Semestre;
import com.escola.api.exception.BusinessException;
import com.escola.api.exception.ResourceNotFoundException;
import com.escola.api.repository.AlunoRepository;
import com.escola.api.repository.DisciplinaRepository;
import com.escola.api.repository.NotaRepository;
import com.escola.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// =====================================================
// NotaService.java
// Versão com paginação nas listagens.
// Boletim continua sem paginação (retorna todas as notas
// do aluno no ano para gerar o relatório completo).
// =====================================================

@Service
@RequiredArgsConstructor
public class NotaService {

    private final NotaRepository notaRepository;
    private final AlunoRepository alunoRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Lista notas de um aluno com paginação.
     */
    public PageResponse<NotaResponse> listarPorAluno(Long alunoId, Pageable pageable) {
        return PageResponse.de(
            notaRepository.findByAlunoId(alunoId, pageable).map(this::toResponse)
        );
    }

    /**
     * Lista notas de um aluno em um ano com paginação.
     */
    public PageResponse<NotaResponse> listarPorAlunoEAno(Long alunoId, Integer ano, Pageable pageable) {
        return PageResponse.de(
            notaRepository.findByAlunoIdAndAno(alunoId, ano, pageable).map(this::toResponse)
        );
    }

    /**
     * Lista notas de uma disciplina por semestre/ano com paginação.
     */
    public PageResponse<NotaResponse> listarPorDisciplina(
            Long disciplinaId, Semestre semestre, Integer ano, Pageable pageable) {
        return PageResponse.de(
            notaRepository.findByDisciplinaIdAndSemestreAndAno(
                disciplinaId, semestre, ano, pageable
            ).map(this::toResponse)
        );
    }

    public NotaResponse buscarPorId(Long id) {
        return notaRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Nota não encontrada com id: " + id));
    }

    /**
     * Boletim: SEM paginação — retorna todas as notas do aluno
     * no ano para montar o relatório completo de uma vez.
     */
    public BoletimResponse gerarBoletim(Long alunoId, Integer ano) {
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com id: " + alunoId));

        // Usa a versão sem Pageable para pegar tudo
        List<NotaResponse> notas = notaRepository
                .findByAlunoIdAndAno(alunoId, ano)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        Double mediaGeral = notaRepository.calcularMediaGeralAluno(alunoId, ano);
        int totalFaltas = notas.stream()
                .mapToInt(n -> n.getFaltas() != null ? n.getFaltas() : 0)
                .sum();

        return BoletimResponse.builder()
                .alunoId(aluno.getId())
                .alunoNome(aluno.getNome())
                .alunoMatricula(aluno.getMatricula())
                .ano(ano)
                .mediaGeral(mediaGeral != null ? Math.round(mediaGeral * 100.0) / 100.0 : null)
                .notas(notas)
                .totalFaltas(totalFaltas)
                .build();
    }

    @Transactional
    public NotaResponse lancar(NotaRequest request) {
        Usuario professor = getProfessorAutenticado();

        Aluno aluno = alunoRepository.findById(request.getAlunoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aluno não encontrado com id: " + request.getAlunoId()));

        Disciplina disciplina = disciplinaRepository.findById(request.getDisciplinaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Disciplina não encontrada com id: " + request.getDisciplinaId()));

        if (disciplina.getProfessor() != null
                && !disciplina.getProfessor().getId().equals(professor.getId())) {
            throw new BusinessException(
                    "Você não é o professor responsável pela disciplina: " + disciplina.getNome());
        }

        notaRepository.findByAlunoIdAndDisciplinaIdAndSemestreAndAno(
                request.getAlunoId(), request.getDisciplinaId(),
                request.getSemestre(), request.getAno())
                .ifPresent(n -> { throw new BusinessException(
                        "Já existe nota lançada para este aluno nesta disciplina/semestre/ano."); });

        Nota nota = Nota.builder()
                .aluno(aluno).disciplina(disciplina).professor(professor)
                .notaBimestre1(request.getNotaBimestre1())
                .notaBimestre2(request.getNotaBimestre2())
                .notaRecuperacao(request.getNotaRecuperacao())
                .faltas(request.getFaltas())
                .semestre(request.getSemestre())
                .ano(request.getAno())
                .build();

        return toResponse(notaRepository.save(nota));
    }

    @Transactional
    public NotaResponse atualizar(Long id, NotaRequest request) {
        Usuario professor = getProfessorAutenticado();

        Nota nota = notaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota não encontrada com id: " + id));

        if (!nota.getProfessor().getId().equals(professor.getId())
                && professor.getRole() != Role.DIRETOR) {
            throw new BusinessException("Você não tem permissão para editar esta nota");
        }

        nota.setNotaBimestre1(request.getNotaBimestre1());
        nota.setNotaBimestre2(request.getNotaBimestre2());
        nota.setNotaRecuperacao(request.getNotaRecuperacao());
        nota.setFaltas(request.getFaltas());

        return toResponse(notaRepository.save(nota));
    }

    @Transactional
    public void deletar(Long id) {
        if (!notaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nota não encontrada com id: " + id);
        }
        notaRepository.deleteById(id);
    }

    // ── Helpers ───────────────────────────────────────────

    private Usuario getProfessorAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new BusinessException("Usuário autenticado não encontrado"));
    }

    public NotaResponse toResponse(Nota n) {
        return NotaResponse.builder()
                .id(n.getId())
                .alunoId(n.getAluno().getId())
                .alunoNome(n.getAluno().getNome())
                .alunoMatricula(n.getAluno().getMatricula())
                .disciplinaId(n.getDisciplina().getId())
                .disciplinaNome(n.getDisciplina().getNome())
                .professorId(n.getProfessor().getId())
                .professorNome(n.getProfessor().getNome())
                .notaBimestre1(n.getNotaBimestre1())
                .notaBimestre2(n.getNotaBimestre2())
                .notaRecuperacao(n.getNotaRecuperacao())
                .mediaFinal(n.getMediaFinal())
                .faltas(n.getFaltas())
                .semestre(n.getSemestre())
                .ano(n.getAno())
                .situacao(n.getSituacao())
                .lancadoEm(n.getLancadoEm())
                .atualizadoEm(n.getAtualizadoEm())
                .build();
    }
}
