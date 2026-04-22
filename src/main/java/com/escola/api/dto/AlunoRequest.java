package com.escola.api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AlunoRequest {
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @Email(message = "Email inválido")
    private String email;

    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDate dataNascimento;

    @Pattern(regexp = "\\(\\d{2}\\)\\s?\\d{4,5}-\\d{4}", message = "Telefone inválido. Use: (xx) xxxxx-xxxx")
    private String telefone;

    private String endereco;
}
