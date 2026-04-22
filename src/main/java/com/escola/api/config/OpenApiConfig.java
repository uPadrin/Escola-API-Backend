package com.escola.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title = "Sistema de Gerenciamento Escolar",
        version = "1.0.0",
        description = "API REST para gerenciamento de alunos, notas e médias escolares. " +
                      "Permite que secretários e diretores gerenciem perfis de alunos e professores, " +
                      "enquanto professores podem lançar notas e faltas por semestre.",
        contact = @Contact(name = "Suporte", email = "suporte@escola.com.br")
    ),
    servers = @Server(url = "http://localhost:8080", description = "Servidor Local"),
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT token de autenticação. Faça login em /api/auth/login e use o token retornado.",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
@Configuration
public class OpenApiConfig {
}
