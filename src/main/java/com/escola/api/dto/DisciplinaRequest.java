package com.escola.api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DisciplinaRequest {
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @Size(max = 10, message = "Código deve ter no máximo 10 caracteres")
    private String codigo;

    @Size(max = 500)
    private String descricao;

    @Min(value = 1, message = "Carga horária deve ser maior que 0")
    private Integer cargaHoraria;

    private Long professorId;
}
