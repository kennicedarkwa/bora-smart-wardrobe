package com.example.sea2_bora;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // ⭐ API freigeben
                        .requestMatchers("/api/benutzer/registrieren").permitAll()
                        .requestMatchers("/api/benutzer/login").permitAll()
                        .requestMatchers("/api/benutzer/logout").permitAll()
                        .requestMatchers("/api/benutzer/**").permitAll()

                        // ⭐ ALLE DEINE HTML-ORDNER freigeben
                        .requestMatchers("/homepage/**").permitAll()
                        .requestMatchers("/benutzer/**").permitAll()
                        .requestMatchers("/kleidung/**").permitAll()
                        .requestMatchers("/empfelung/**").permitAll()
                        .requestMatchers("/washkorb/**").permitAll()
                        .requestMatchers("/weather/**").permitAll()
                        .requestMatchers("/outfit/**").permitAll()

                        // ⭐ statische Dateien
                        .requestMatchers("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg").permitAll()

                        // ⭐ H2-Konsole
                        .requestMatchers("/h2-console/**").permitAll()

                        // ⭐ Rest nur für eingeloggte Benutzer
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
