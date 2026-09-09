package com.zara.backend.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.zara.backend.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // =====================================================
    // PASSWORD ENCODER
    // =====================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // =====================================================
    // SECURITY FILTER CHAIN
    // =====================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

            // =================================================
            // CORS
            // =================================================

            .cors(cors -> {})

            // =================================================
            // CSRF
            // =================================================

            .csrf(csrf -> csrf.disable())

            // =================================================
            // SESSION MANAGEMENT
            // =================================================

            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            // =================================================
            // AUTHORIZATION
            // =================================================

            .authorizeHttpRequests(auth -> auth

                // =================================================
                // ACTUATOR HEALTH
                // Render health check
                // =================================================

                .requestMatchers("/actuator/health")
                .permitAll()

                // =================================================
                // AUTH
                // =================================================

                .requestMatchers("/auth/**")
                .permitAll()

                // =================================================
                // PRODUCTS
                // =================================================

                .requestMatchers(
                        HttpMethod.GET,
                        "/products/**"
                )
                .permitAll()

                .requestMatchers(
                        HttpMethod.POST,
                        "/products/**"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                        HttpMethod.PUT,
                        "/products/**"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                        HttpMethod.DELETE,
                        "/products/**"
                )
                .hasRole("ADMIN")

                // =================================================
                // CATEGORIES
                // =================================================

                .requestMatchers(
                        HttpMethod.GET,
                        "/categories/**"
                )
                .permitAll()

                .requestMatchers(
                        HttpMethod.POST,
                        "/categories/**"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                        HttpMethod.PUT,
                        "/categories/**"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                        HttpMethod.DELETE,
                        "/categories/**"
                )
                .hasRole("ADMIN")

                // =================================================
                // REVIEWS
                // =================================================

                .requestMatchers(
                        HttpMethod.GET,
                        "/reviews/**"
                )
                .permitAll()

                .requestMatchers(
                        HttpMethod.POST,
                        "/reviews/**"
                )
                .authenticated()

                .requestMatchers(
                        HttpMethod.PUT,
                        "/reviews/**"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                        HttpMethod.DELETE,
                        "/reviews/**"
                )
                .hasRole("ADMIN")

                // =================================================
                // NEWSLETTER
                // =================================================

                .requestMatchers(
                        HttpMethod.POST,
                        "/newsletter/subscribe"
                )
                .permitAll()

                .requestMatchers(
                        HttpMethod.GET,
                        "/newsletter/subscribers/**"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                        HttpMethod.DELETE,
                        "/newsletter/subscribers/**"
                )
                .hasRole("ADMIN")

                // =================================================
                // USER PROFILE
                // =================================================

                .requestMatchers(
                        "/users/profile"
                )
                .authenticated()

                // =================================================
                // ADMIN USER APIs
                // =================================================

                .requestMatchers(
                        "/users/customers"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                        "/users/customers/**"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                        "/users/**"
                )
                .hasRole("ADMIN")

                // =================================================
                // ADMIN APIs
                // =================================================

                .requestMatchers(
                        "/api/admin/**"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                        "/admin/profile/**"
                )
                .hasRole("ADMIN")

                // =================================================
                // COUPONS
                // =================================================

                .requestMatchers(
                        "/coupons/**"
                )
                .hasRole("ADMIN")

                // =================================================
                // ORDERS
                // =================================================

                .requestMatchers(
                        HttpMethod.POST,
                        "/orders"
                )
                .authenticated()

                .requestMatchers(
                        HttpMethod.GET,
                        "/orders/user/**"
                )
                .authenticated()

                .requestMatchers(
                        HttpMethod.GET,
                        "/orders"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                        HttpMethod.GET,
                        "/orders/*"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                        HttpMethod.PUT,
                        "/orders/*/status"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                        HttpMethod.PUT,
                        "/orders/*/payment-status"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                        "/orders/**"
                )
                .hasRole("ADMIN")

                // =================================================
                // PAYMENTS
                // =================================================

                .requestMatchers(
                        HttpMethod.POST,
                        "/payments/create/**"
                )
                .authenticated()

                .requestMatchers(
                        HttpMethod.POST,
                        "/payments/verify"
                )
                .authenticated()

                .requestMatchers(
                        HttpMethod.GET,
                        "/payments/order/**"
                )
                .authenticated()

                .requestMatchers(
                        HttpMethod.POST,
                        "/payments/refund/**"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                        HttpMethod.GET,
                        "/payments"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                        HttpMethod.GET,
                        "/payments/*"
                )
                .hasRole("ADMIN")

                // =================================================
                // EVERYTHING ELSE
                // =================================================

                .anyRequest()
                .authenticated()
            )

            // =================================================
            // JWT FILTER
            // =================================================

            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    // =====================================================
    // CORS CONFIGURATION
    // =====================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        // -----------------------------------------------------
        // FRONTEND ORIGINS
        // -----------------------------------------------------

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173",
                        "http://localhost:5174",
                        "http://localhost:4173",
                        "https://zarabeauty-collection.netlify.app"
                )
        );

        // -----------------------------------------------------
        // HTTP METHODS
        // -----------------------------------------------------

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        // -----------------------------------------------------
        // HEADERS
        // -----------------------------------------------------

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setExposedHeaders(
                List.of("Authorization")
        );

        // -----------------------------------------------------
        // CREDENTIALS
        // -----------------------------------------------------

        configuration.setAllowCredentials(true);

        // -----------------------------------------------------
        // REGISTER CORS
        // -----------------------------------------------------

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}
