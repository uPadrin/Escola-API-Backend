package com.escola.api.dto;

import com.escola.api.enums.Semestre;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class NotaRequest {
    @NotNull(message = "ID do aluno é obrigatório")
    private Long alunoId;

    @NotNull(message = "ID da disciplina é obrigatório")
    private Long disciplinaId;

    @DecimalMin(value = "0.0", message = "Nota deve ser entre 0 e 10")
    @DecimalMax(value = "10.0", message = "Nota deve ser entre 0 e 10")
    private Double notaBimestre1;

    @DecimalMin(value = "0.0", message = "Nota deve ser entre 0 e 10")
    @DecimalMax(value = "10.0", message = "Nota deve ser entre 0 e 10")
    private Double notaBimestre2;

    @DecimalMin(value = "0.0", message = "Nota deve ser entre 0 e 10")
    @DecimalMax(value = "10.0", message = "Nota deve ser entre 0 e 10")
    private Double notaRecuperacao;

    @Min(value = 0, message = "Faltas não podem ser negativas")
    private Integer faltas = 0;

    @NotNull(message = "Semestre é obrigatório")
    private Semestre semestre;

    @NotNull(message = "Ano é obrigatório")
    @Min(value = 2000, message = "Ano inválido")
    private Integer ano;
}
