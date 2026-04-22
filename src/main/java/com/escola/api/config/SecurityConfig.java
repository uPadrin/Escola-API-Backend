package com.escola.api.config;

import com.escola.api.repository.UsuarioRepository;
import com.escola.api.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UsuarioRepository usuarioRepository;
    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(UsuarioRepository usuarioRepository,
            @Lazy JwtAuthenticationFilter jwtAuthFilter) {
        this.usuarioRepository = usuarioRepository;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Público
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // Alunos: criação/edição apenas DIRETOR e SECRETARIO
                        .requestMatchers(HttpMethod.POST, "/api/alunos/**").hasAnyRole("DIRETOR", "SECRETARIO")
                        .requestMatchers(HttpMethod.PUT, "/api/alunos/**").hasAnyRole("DIRETOR", "SECRETARIO")
                        .requestMatchers(HttpMethod.DELETE, "/api/alunos/**").hasAnyRole("DIRETOR", "SECRETARIO")
                        .requestMatchers(HttpMethod.GET, "/api/alunos/**")
                        .hasAnyRole("DIRETOR", "SECRETARIO", "PROFESSOR")

                        // Usuários (professores, secretários): apenas DIRETOR e SECRETARIO gerenciam
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/**").hasAnyRole("DIRETOR", "SECRETARIO")
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/**").hasAnyRole("DIRETOR", "SECRETARIO")
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("DIRETOR")
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/**").hasAnyRole("DIRETOR", "SECRETARIO")

                        // Disciplinas: DIRETOR e SECRETARIO criam, PROFESSOR pode visualizar
                        .requestMatchers(HttpMethod.POST, "/api/disciplinas/**").hasAnyRole("DIRETOR", "SECRETARIO")
                        .requestMatchers(HttpMethod.PUT, "/api/disciplinas/**").hasAnyRole("DIRETOR", "SECRETARIO")
                        .requestMatchers(HttpMethod.DELETE, "/api/disciplinas/**").hasAnyRole("DIRETOR", "SECRETARIO")
                        .requestMatchers(HttpMethod.GET, "/api/disciplinas/**").authenticated()

                        // Notas: PROFESSOR lança, DIRETOR e SECRETARIO visualizam
                        .requestMatchers(HttpMethod.POST, "/api/notas/**").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.PUT, "/api/notas/**").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/notas/**").hasAnyRole("PROFESSOR", "DIRETOR")
                        .requestMatchers(HttpMethod.GET, "/api/notas/**").authenticated()

                        .anyRequest().authenticated())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(frame -> frame.disable())); // H2 console

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
