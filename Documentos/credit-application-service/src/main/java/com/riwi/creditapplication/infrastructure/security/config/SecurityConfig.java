package com.riwi.creditapplication.infrastructure.security.config;

import com.riwi.creditapplication.infrastructure.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security Configuration for JWT Stateless Authentication.
 * Configures Spring Security with JWT and role-based authorization.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Configure Security Filter Chain with JWT authentication.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (sin /api porque ya está en el context path)
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Affiliate endpoints - ROLE_AFILIADO
                        .requestMatchers(HttpMethod.POST, "/affiliates").permitAll() // Registration
                        .requestMatchers(HttpMethod.GET, "/affiliates/me").hasRole("AFILIADO")
                        .requestMatchers(HttpMethod.PUT, "/affiliates/me").hasRole("AFILIADO")

                        // Credit Application endpoints - ROLE_AFILIADO
                        .requestMatchers(HttpMethod.POST, "/credit-applications").hasRole("AFILIADO")
                        .requestMatchers(HttpMethod.GET, "/credit-applications/my-applications").hasRole("AFILIADO")
                        .requestMatchers(HttpMethod.GET, "/credit-applications/{id}")
                        .hasAnyRole("AFILIADO", "ANALISTA", "ADMIN")

                        // Evaluation endpoints - ROLE_ANALISTA
                        .requestMatchers(HttpMethod.POST, "/credit-applications/{id}/evaluate")
                        .hasAnyRole("ANALISTA", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/credit-applications/pending")
                        .hasAnyRole("ANALISTA", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/credit-applications/under-evaluation")
                        .hasAnyRole("ANALISTA", "ADMIN")

                        // Admin endpoints - ROLE_ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/affiliates").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")

                        // All other requests require authentication
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configure Authentication Provider with UserDetailsService and
     * PasswordEncoder.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Configure Authentication Manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configure Password Encoder (BCrypt).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
