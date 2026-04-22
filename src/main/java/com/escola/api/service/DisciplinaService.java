package com.escola.api.service;

import com.escola.api.dto.DisciplinaRequest;
import com.escola.api.dto.DisciplinaResponse;
import com.escola.api.dto.PageResponse;
import com.escola.api.entity.Disciplina;
import com.escola.api.entity.Usuario;
import com.escola.api.enums.Role;
import com.escola.api.exception.BusinessException;
import com.escola.api.exception.ResourceNotFoundException;
import com.escola.api.repository.DisciplinaRepository;
import com.escola.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// =====================================================
// DisciplinaService.java
// Versão com paginação.
// Mantém métodos sem paginação para selects do frontend.
// =====================================================

@Service
@RequiredArgsConstructor
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Lista todas as disciplinas com paginação.
     */
    public PageResponse<DisciplinaResponse> listar(Pageable pageable) {
        return PageResponse.de(
            disciplinaRepository.findAll(pageable).map(this::toResponse)
        );
    }

    /**
     * Lista disciplinas ativas com paginação.
     */
    public PageResponse<DisciplinaResponse> listarAtivas(Pageable pageable) {
        return PageResponse.de(
            disciplinaRepository.findByAtiva(true, pageable).map(this::toResponse)
        );
    }

    /**
     * Lista disciplinas por professor com paginação.
     */
    public PageResponse<DisciplinaResponse> listarPorProfessor(Long professorId, Pageable pageable) {
        return PageResponse.de(
            disciplinaRepository.findByProfessorId(professorId, pageable).map(this::toResponse)
        );
    }

    /**
     * Lista todas as disciplinas SEM paginação.
     * Usado no select de vínculo de professor no frontend.
     */
    public List<DisciplinaResponse> listarTodasSemPaginacao() {
        return disciplinaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public DisciplinaResponse buscarPorId(Long id) {
        return disciplinaRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada com id: " + id));
    }

    @Transactional
    public DisciplinaResponse criar(DisciplinaRequest request) {
        if (request.getCodigo() != null && disciplinaRepository.existsByCodigo(request.getCodigo())) {
            throw new BusinessException("Código de disciplina já cadastrado: " + request.getCodigo());
        }

        Disciplina disciplina = Disciplina.builder()
                .nome(request.getNome())
                .codigo(request.getCodigo())
                .descricao(request.getDescricao())
                .cargaHoraria(request.getCargaHoraria())
                .ativa(true)
                .build();

        if (request.getProfessorId() != null) {
            Usuario professor = buscarProfessor(request.getProfessorId());
            disciplina.setProfessor(professor);
        }

        return toResponse(disciplinaRepository.save(disciplina));
    }

    @Transactional
    public DisciplinaResponse atualizar(Long id, DisciplinaRequest request) {
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada com id: " + id));

        if (request.getCodigo() != null
                && !request.getCodigo().equals(disciplina.getCodigo())
                && disciplinaRepository.existsByCodigo(request.getCodigo())) {
            throw new BusinessException("Código de disciplina já em uso: " + request.getCodigo());
        }

        disciplina.setNome(request.getNome());
        disciplina.setCodigo(request.getCodigo());
        disciplina.setDescricao(request.getDescricao());
        disciplina.setCargaHoraria(request.getCargaHoraria());
        disciplina.setProfessor(
            request.getProfessorId() != null ? buscarProfessor(request.getProfessorId()) : null
        );

        return toResponse(disciplinaRepository.save(disciplina));
    }

    @Transactional
    public void alterarStatus(Long id, boolean ativa) {
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada com id: " + id));
        disciplina.setAtiva(ativa);
        disciplinaRepository.save(disciplina);
    }

    @Transactional
    public void deletar(Long id) {
        if (!disciplinaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Disciplina não encontrada com id: " + id);
        }
        disciplinaRepository.deleteById(id);
    }

    // ── Helpers ───────────────────────────────────────────

    private Usuario buscarProfessor(Long professorId) {
        Usuario professor = usuarioRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Professor não encontrado com id: " + professorId));
        if (professor.getRole() != Role.PROFESSOR) {
            throw new BusinessException("O usuário informado não é um professor");
        }
        return professor;
    }

    public DisciplinaResponse toResponse(Disciplina d) {
        return DisciplinaResponse.builder()
                .id(d.getId())
                .nome(d.getNome())
                .codigo(d.getCodigo())
                .descricao(d.getDescricao())
                .cargaHoraria(d.getCargaHoraria())
                .ativa(d.isAtiva())
                .professorId(d.getProfessor() != null ? d.getProfessor().getId() : null)
                .professorNome(d.getProfessor() != null ? d.getProfessor().getNome() : null)
                .criadoEm(d.getCriadoEm())
                .build();
    }
}
