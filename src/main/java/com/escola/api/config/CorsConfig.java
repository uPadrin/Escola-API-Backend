package com.escola.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

// =====================================================
// CorsConfig.java
// Libera o frontend React (localhost:3000) para
// fazer requisições ao backend (localhost:8080).
// Sem isso o navegador bloqueia todas as chamadas.
// =====================================================

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Origens permitidas (frontend React em dev e build)
        config.setAllowedOrigins(List.of(
            "http://localhost:3000",
            "http://localhost:5173"   // Vite, caso use no futuro
        ));

        // Métodos HTTP permitidos
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Headers permitidos nas requisições
        config.setAllowedHeaders(List.of("*"));

        // Permite envio de credenciais (cookie / Authorization header)
        config.setAllowCredentials(true);

        // Aplica essa configuração para todos os endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}