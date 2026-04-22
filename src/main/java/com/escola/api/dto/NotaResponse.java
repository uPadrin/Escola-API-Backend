package com.escola.api.dto;

import com.escola.api.enums.Semestre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotaResponse {
    private Long id;
    private Long alunoId;
    private String alunoNome;
    private String alunoMatricula;
    private Long disciplinaId;
    private String disciplinaNome;
    private Long professorId;
    private String professorNome;
    private Double notaBimestre1;
    private Double notaBimestre2;
    private Double notaRecuperacao;
    private Double mediaFinal;
    private Integer faltas;
    private Semestre semestre;
    private Integer ano;
    private String situacao;
    private LocalDateTime lancadoEm;
    private LocalDateTime atualizadoEm;
}
