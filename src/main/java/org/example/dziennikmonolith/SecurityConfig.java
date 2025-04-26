package org.example.dziennikmonolith;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // --- Konfiguracja H2 Console (jeśli używasz) ---
                .csrf(csrf -> csrf.ignoringRequestMatchers(toH2Console()))
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                // --- Koniec Konfiguracji H2 Console ---

                .authorizeHttpRequests(auth -> auth
                        // .requestMatchers(toH2Console()).permitAll() // Nadal możesz chcieć zezwolić na H2
                        .anyRequest().permitAll() // !!!! ZEZWÓL NA WSZYSTKIE ŻĄDANIA !!!!
                );
        // Nie konfiguruj UserDetailsService ani formLogin, jeśli nie są potrzebne

        return http.build();
    }
}
