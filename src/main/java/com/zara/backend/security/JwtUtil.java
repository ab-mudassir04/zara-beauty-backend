package com.zara.backend.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expiration;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration:86400000}") long expiration) {

        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "JWT secret is required"
            );
        }

        String cleanSecret = secret.trim();

        byte[] secretBytes =
                cleanSecret.getBytes(StandardCharsets.UTF_8);

        /*
         * HS256 requires a sufficiently strong secret.
         */
        if (secretBytes.length < 32) {
            throw new IllegalArgumentException(
                    "JWT secret must contain at least 32 characters"
            );
        }

        this.secretKey =
                Keys.hmacShaKeyFor(secretBytes);

        this.expiration = expiration;
    }

    // =====================================================
    // GENERATE TOKEN
    // =====================================================

    public String generateToken(
            String email,
            String role) {

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Email is required"
            );
        }

        String normalizedEmail =
                email.trim().toLowerCase();

        String normalizedRole =
                role == null || role.trim().isEmpty()
                        ? "USER"
                        : role.trim().toUpperCase();

        return Jwts.builder()
                .setSubject(normalizedEmail)
                .claim("role", normalizedRole)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + expiration
                        )
                )
                .signWith(secretKey)
                .compact();
    }

    // =====================================================
    // VALIDATE TOKEN
    // =====================================================

    public Claims validateToken(String token) {

        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "JWT token is required"
            );
        }

        String cleanToken = token.trim();

        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(cleanToken)
                .getBody();
    }

    // =====================================================
    // GET EMAIL
    // =====================================================

    public String getEmailFromToken(String token) {

        Claims claims =
                validateToken(token);

        return claims.getSubject();
    }

    // =====================================================
    // GET ROLE
    // =====================================================

    public String getRoleFromToken(String token) {

        Claims claims =
                validateToken(token);

        String role =
                claims.get("role", String.class);

        if (role == null || role.trim().isEmpty()) {
            return "USER";
        }

        return role.trim().toUpperCase();
    }

    // =====================================================
    // CHECK TOKEN
    // =====================================================

    public boolean isTokenValid(String token) {

        try {
            validateToken(token);
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
