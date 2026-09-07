package com.zara.backend.security;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    JwtAuthenticationFilter.class
            );

    private final JwtUtil jwtUtil;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public JwtAuthenticationFilter(
            JwtUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

    // =====================================================
    // JWT FILTER
    // =====================================================

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        // =================================================
        // NO AUTHORIZATION HEADER
        // =================================================

        if (authorizationHeader == null ||
                authorizationHeader.trim().isEmpty()) {

            filterChain.doFilter(request, response);
            return;
        }

        // =================================================
        // INVALID AUTHORIZATION FORMAT
        // =================================================

        if (!authorizationHeader.startsWith("Bearer ")) {

            SecurityContextHolder.clearContext();

            logger.debug(
                    "Invalid Authorization header format for {}",
                    request.getRequestURI()
            );

            filterChain.doFilter(request, response);
            return;
        }

        // =================================================
        // EXTRACT TOKEN
        // =================================================

        String token =
                authorizationHeader
                        .substring(7)
                        .trim();

        // =================================================
        // EMPTY TOKEN
        // =================================================

        if (token.isEmpty()) {

            SecurityContextHolder.clearContext();

            filterChain.doFilter(request, response);
            return;
        }

        // =================================================
        // BASIC JWT STRUCTURE CHECK
        // =================================================

        if (token.split("\\.").length != 3) {

            SecurityContextHolder.clearContext();

            logger.debug(
                    "Invalid JWT structure for {}",
                    request.getRequestURI()
            );

            filterChain.doFilter(request, response);
            return;
        }

        // =================================================
        // VALIDATE JWT
        // =================================================

        try {

            Claims claims =
                    jwtUtil.validateToken(token);

            // =============================================
            // GET EMAIL
            // =============================================

            String email =
                    claims.getSubject();

            if (email == null ||
                    email.trim().isEmpty()) {

                SecurityContextHolder.clearContext();

                logger.debug(
                        "JWT subject is missing for {}",
                        request.getRequestURI()
                );

                filterChain.doFilter(request, response);
                return;
            }

            // =============================================
            // GET ROLE
            // =============================================

            String role =
                    claims.get(
                            "role",
                            String.class
                    );

            if (role == null ||
                    role.trim().isEmpty()) {

                role = "USER";
            }

            role =
                    role.trim()
                            .toUpperCase();

            // =============================================
            // CREATE AUTHORITY
            // =============================================

            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority(
                            "ROLE_" + role
                    );

            // =============================================
            // CREATE AUTHENTICATION
            // =============================================

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(authority)
                    );

            // =============================================
            // SET SECURITY CONTEXT
            // =============================================

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            logger.debug(
                    "JWT authenticated user={} role={} uri={}",
                    email,
                    role,
                    request.getRequestURI()
            );

        } catch (Exception e) {

            SecurityContextHolder.clearContext();

            logger.debug(
                    "JWT validation failed for {}: {}",
                    request.getRequestURI(),
                    e.getMessage()
            );
        }

        // =================================================
        // CONTINUE REQUEST
        // =================================================

        filterChain.doFilter(request, response);
    }
}
