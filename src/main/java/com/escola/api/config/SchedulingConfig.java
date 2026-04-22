package com.escola.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

// =====================================================
// SchedulingConfig.java
// Habilita o @Scheduled para a limpeza automática
// de refresh tokens expirados no RefreshTokenService.
// =====================================================

@Configuration
@EnableScheduling
public class SchedulingConfig {
}
