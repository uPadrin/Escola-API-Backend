package com.escola.api.service;

import com.escola.api.dto.AlunoRequest;
import com.escola.api.dto.AlunoResponse;
import com.escola.api.dto.PageResponse;
import com.escola.api.entity.Aluno;
import com.escola.api.exception.BusinessException;
import com.escola.api.exception.ResourceNotFoundException;
import com.escola.api.repository.AlunoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Random;

// =====================================================
// AlunoService.java
// Versão com paginação.
// O método listar() recebe Pageable e retorna PageResponse.
// =====================================================

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository;

    /**
     * Lista alunos com paginação e filtros opcionais.
     *
     * @param nome        filtra por nome (parcial, case insensitive) — null = sem filtro
     * @param apenasAtivos true = só alunos com matrícula ativa
     * @param pageable    configuração de página, tamanho e ordenação
     */
    public PageResponse<AlunoResponse> listar(String nome, boolean apenasAtivos, Pageable pageable) {
        Page<Aluno> page;

        boolean temNome = nome != null && !nome.isBlank();

        if (temNome && apenasAtivos) {
            // Busca por nome + só ativos
            page = alunoRepository.findByNomeContainingIgnoreCaseAndAtivo(nome, true, pageable);

        } else if (temNome) {
            // Busca por nome em todos
            page = alunoRepository.findByNomeContainingIgnoreCase(nome, pageable);

        } else if (apenasAtivos) {
            // Só ativos sem filtro de nome
            page = alunoRepository.findByAtivo(true, pageable);

        } else {
            // Todos os alunos — findAll(Pageable) vem do JpaRepository
            page = alunoRepository.findAll(pageable);
        }

        // Converte Page<Aluno> → PageResponse<AlunoResponse>
        return PageResponse.de(page.map(this::toResponse));
    }

    // ── Métodos de item único (sem paginação) ─────────────

    public AlunoResponse buscarPorId(Long id) {
        return alunoRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com id: " + id));
    }

    public AlunoResponse buscarPorMatricula(String matricula) {
        return alunoRepository.findByMatricula(matricula)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com matrícula: " + matricula));
    }

    @Transactional
    public AlunoResponse criar(AlunoRequest request) {
        if (request.getEmail() != null && alunoRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + request.getEmail());
        }

        Aluno aluno = Aluno.builder()
                .nome(request.getNome())
                .matricula(gerarMatricula())
                .email(request.getEmail())
                .dataNascimento(request.getDataNascimento())
                .telefone(request.getTelefone())
                .endereco(request.getEndereco())
                .ativo(true)
                .build();

        return toResponse(alunoRepository.save(aluno));
    }

    @Transactional
    public AlunoResponse atualizar(Long id, AlunoRequest request) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com id: " + id));

        if (request.getEmail() != null
                && !request.getEmail().equals(aluno.getEmail())
                && alunoRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já em uso: " + request.getEmail());
        }

        aluno.setNome(request.getNome());
        aluno.setEmail(request.getEmail());
        aluno.setDataNascimento(request.getDataNascimento());
        aluno.setTelefone(request.getTelefone());
        aluno.setEndereco(request.getEndereco());

        return toResponse(alunoRepository.save(aluno));
    }

    @Transactional
    public void alterarStatus(Long id, boolean ativo) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com id: " + id));
        aluno.setAtivo(ativo);
        alunoRepository.save(aluno);
    }

    @Transactional
    public void deletar(Long id) {
        if (!alunoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Aluno não encontrado com id: " + id);
        }
        alunoRepository.deleteById(id);
    }

    // ── Helpers ───────────────────────────────────────────

    private String gerarMatricula() {
        String ano = String.valueOf(LocalDate.now().getYear());
        String seq = String.format("%05d", new Random().nextInt(99999) + 1);
        String matricula = ano + seq;
        while (alunoRepository.existsByMatricula(matricula)) {
            seq = String.format("%05d", new Random().nextInt(99999) + 1);
            matricula = ano + seq;
        }
        return matricula;
    }

    public AlunoResponse toResponse(Aluno a) {
        return AlunoResponse.builder()
                .id(a.getId())
                .nome(a.getNome())
                .matricula(a.getMatricula())
                .email(a.getEmail())
                .dataNascimento(a.getDataNascimento())
                .telefone(a.getTelefone())
                .endereco(a.getEndereco())
                .ativo(a.isAtivo())
                .criadoEm(a.getCriadoEm())
                .build();
    }
}
