package com.escola.api.config;

import com.escola.api.entity.Aluno;
import com.escola.api.entity.Disciplina;
import com.escola.api.entity.Nota;
import com.escola.api.entity.Usuario;
import com.escola.api.enums.Role;
import com.escola.api.enums.Semestre;
import com.escola.api.repository.AlunoRepository;
import com.escola.api.repository.DisciplinaRepository;
import com.escola.api.repository.NotaRepository;
import com.escola.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

        @Bean
        CommandLineRunner initDatabase(
                        UsuarioRepository usuarioRepository,
                        AlunoRepository alunoRepository,
                        DisciplinaRepository disciplinaRepository,
                        NotaRepository notaRepository,
                        PasswordEncoder passwordEncoder) {

                return args -> {
                        if (usuarioRepository.count() == 0) {
                                log.info("=== Inicializando dados de exemplo ===");

                                // Criar Diretor
                                Usuario diretor = usuarioRepository.save(Usuario.builder()
                                                .nome("Carlos Diretor")
                                                .email("diretor@escola.com")
                                                .senha(passwordEncoder.encode("diretor123"))
                                                .role(Role.DIRETOR)
                                                .ativo(true)
                                                .build());

                                // Criar Secretária
                                Usuario secretaria = usuarioRepository.save(Usuario.builder()
                                                .nome("Ana Secretária")
                                                .email("secretaria@escola.com")
                                                .senha(passwordEncoder.encode("secretaria123"))
                                                .role(Role.SECRETARIO)
                                                .ativo(true)
                                                .build());
                                // Criar Professores
                                Usuario profMath = usuarioRepository.save(Usuario.builder()
                                                .nome("Prof. João Matemática")
                                                .email("prof.matematica@escola.com")
                                                .senha(passwordEncoder.encode("professor123"))
                                                .role(Role.PROFESSOR)
                                                .ativo(true)
                                                .build());

                                Usuario profPort = usuarioRepository.save(Usuario.builder()
                                                .nome("Profa. Maria Português")
                                                .email("prof.portugues@escola.com")
                                                .senha(passwordEncoder.encode("professor123"))
                                                .role(Role.PROFESSOR)
                                                .ativo(true)
                                                .build());

                                // Criar Alunos
                                Aluno aluno1 = alunoRepository.save(Aluno.builder()
                                                .nome("Pedro Alves")
                                                .matricula("2024000001")
                                                .email("pedro.alves@email.com")
                                                .dataNascimento(LocalDate.of(2008, 3, 15))
                                                .telefone("(11) 99999-0001")
                                                .endereco("Rua das Flores, 100 - São Paulo/SP")
                                                .ativo(true)
                                                .build());

                                Aluno aluno2 = alunoRepository.save(Aluno.builder()
                                                .nome("Luana Costa")
                                                .matricula("2024000002")
                                                .email("luana.costa@email.com")
                                                .dataNascimento(LocalDate.of(2007, 7, 22))
                                                .telefone("(11) 99999-0002")
                                                .endereco("Av. Principal, 200 - São Paulo/SP")
                                                .ativo(true)
                                                .build());

                                Aluno aluno3 = alunoRepository.save(Aluno.builder()
                                                .nome("Rafael Santos")
                                                .matricula("2024000003")
                                                .email("rafael.santos@email.com")
                                                .dataNascimento(LocalDate.of(2008, 11, 5))
                                                .ativo(true)
                                                .build());

                                // Criar Disciplinas
                                Disciplina matematica = disciplinaRepository.save(Disciplina.builder()
                                                .nome("Matemática")
                                                .codigo("MAT001")
                                                .descricao("Álgebra, geometria e aritmética")
                                                .cargaHoraria(80)
                                                .professor(profMath)
                                                .ativa(true)
                                                .build());

                                Disciplina portugues = disciplinaRepository.save(Disciplina.builder()
                                                .nome("Língua Portuguesa")
                                                .codigo("PORT001")
                                                .descricao("Gramática, interpretação e redação")
                                                .cargaHoraria(80)
                                                .professor(profPort)
                                                .ativa(true)
                                                .build());

                                Disciplina historia = disciplinaRepository.save(Disciplina.builder()
                                                .nome("História")
                                                .codigo("HIS001")
                                                .descricao("História do Brasil e do Mundo")
                                                .cargaHoraria(60)
                                                .ativa(true)
                                                .build());

                                // Lançar Notas (1º Semestre 2025)
                                notaRepository.save(Nota.builder()
                                                .aluno(aluno1).disciplina(matematica).professor(profMath)
                                                .notaBimestre1(8.5).notaBimestre2(7.0)
                                                .faltas(2).semestre(Semestre.PRIMEIRO).ano(2025).build());

                                notaRepository.save(Nota.builder()
                                                .aluno(aluno1).disciplina(portugues).professor(profPort)
                                                .notaBimestre1(6.0).notaBimestre2(5.5).notaRecuperacao(7.0)
                                                .faltas(5).semestre(Semestre.PRIMEIRO).ano(2025).build());

                                notaRepository.save(Nota.builder()
                                                .aluno(aluno2).disciplina(matematica).professor(profMath)
                                                .notaBimestre1(9.5).notaBimestre2(9.0)
                                                .faltas(0).semestre(Semestre.PRIMEIRO).ano(2025).build());

                                notaRepository.save(Nota.builder()
                                                .aluno(aluno2).disciplina(portugues).professor(profPort)
                                                .notaBimestre1(8.0).notaBimestre2(8.5)
                                                .faltas(1).semestre(Semestre.PRIMEIRO).ano(2025).build());

                                notaRepository.save(Nota.builder()
                                                .aluno(aluno3).disciplina(matematica).professor(profMath)
                                                .notaBimestre1(4.0).notaBimestre2(3.5)
                                                .faltas(12).semestre(Semestre.PRIMEIRO).ano(2025).build());
                        }
                        log.info("=== Dados inicializados com sucesso! ===");
                        log.info("Credenciais:");
                        log.info("  DIRETOR    => diretor@escola.com / diretor123");
                        log.info("  SECRETARIA => secretaria@escola.com / secretaria123");
                        log.info("  PROFESSOR  => prof.matematica@escola.com / professor123");
                        log.info("  PROFESSOR  => prof.portugues@escola.com / professor123");
                        log.info("Swagger UI: http://localhost:8080/swagger-ui.html");
                        log.info("H2 Console: http://localhost:8080/h2-console");
                };
        }
}
