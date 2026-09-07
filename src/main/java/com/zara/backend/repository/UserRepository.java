package com.zara.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zara.backend.entity.User;

public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByEmail(
            String email
    );

    Optional<User> findByIdAndRole(
            Long id,
            String role
    );

    Optional<User> findByEmailAndRole(
            String email,
            String role
    );

    boolean existsByEmail(
            String email
    );

    long countByRoleIgnoreCase(
            String role
    );
}