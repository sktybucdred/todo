package org.example.dziennikmonolith.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/ws/**").allowedOrigins("http://localhost:8080");
                registry.addMapping("/uploads/**").allowedOrigins("http://localhost:8080");
            }
        };
    }
}
