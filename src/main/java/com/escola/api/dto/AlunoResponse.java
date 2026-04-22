package com.escola.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlunoResponse {
    private Long id;
    private String nome;
    private String matricula;
    private String email;
    private LocalDate dataNascimento;
    private String telefone;
    private String endereco;
    private boolean ativo;
    private LocalDateTime criadoEm;
}
