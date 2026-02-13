package com.br.manager.infra.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    // TODO : Precisa criar outra classe que ira trabalhar apenas em produção e configurar o cors para o spring-security

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Aplica a todos os endpoints
                        .allowedOriginPatterns("http://localhost:*") // Substitua pela URL do seu front
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("Content-Type", "x-tenant-id") // Libera os headers específicos
                        .exposedHeaders("x-tenant-id") // Permite que o front leia o header na resposta, se necessário
                        .allowCredentials(true);
            }
        };
    }
}