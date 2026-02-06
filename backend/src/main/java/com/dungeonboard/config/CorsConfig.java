package com.dungeonboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow specific origins from environment variable or defaults
        config.setAllowedOrigins(Arrays.asList(
            "https://dungeon-board-game.vercel.app",
            "https://dungeon-board-beta-ten.vercel.app",
            "http://localhost:5173",
            "http://localhost:5174"
        ));

        // Allow credentials
        config.setAllowCredentials(true);

        // Allow all headers
        config.setAllowedHeaders(Arrays.asList("*"));

        // Allow common methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Expose headers if needed
        config.addExposedHeader("Authorization");

        // Cache preflight response for 1 hour
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
