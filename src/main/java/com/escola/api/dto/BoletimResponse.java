package com.escola.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoletimResponse {
    private Long alunoId;
    private String alunoNome;
    private String alunoMatricula;
    private Integer ano;
    private Double mediaGeral;
    private List<NotaResponse> notas;
    private Integer totalFaltas;
}
