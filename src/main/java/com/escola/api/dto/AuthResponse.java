package com.escola.api.dto;

import com.escola.api.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;           // access token JWT (curta duração)
    private String refreshToken;    // refresh token (longa duração, persiste no banco)
    private String tipo;            // sempre "Bearer"
    private Long id;
    private String nome;
    private String email;
    private Role role;
}
