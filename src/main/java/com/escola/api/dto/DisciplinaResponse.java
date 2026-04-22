package com.escola.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisciplinaResponse {
    private Long id;
    private String nome;
    private String codigo;
    private String descricao;
    private Integer cargaHoraria;
    private boolean ativa;
    private Long professorId;
    private String professorNome;
    private LocalDateTime criadoEm;
}
