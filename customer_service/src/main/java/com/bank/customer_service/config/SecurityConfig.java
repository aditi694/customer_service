package com.bank.customer_service.config;

import com.bank.customer_service.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // PUBLIC
                        .requestMatchers("/auth/login", "/auth/register").permitAll()

                        // STAFF + ADMIN
                        .requestMatchers(HttpMethod.POST, "/customers")
                        .hasAnyRole("STAFF", "ADMIN")

                        // ADMIN ONLY (FIXED PATHS)
                        .requestMatchers(HttpMethod.PUT, "/customers/*/block")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/customers/*/close")
                        .hasRole("ADMIN")

                        // ANY AUTHENTICATED USER
                        .requestMatchers("/customers/**")
                        .authenticated()

                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
