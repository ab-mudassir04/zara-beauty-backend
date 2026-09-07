package com.zara.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zara.backend.entity.Role;
import com.zara.backend.entity.User;
import com.zara.backend.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // =====================================================
    // GET ALL CUSTOMERS
    // =====================================================

    public List<User> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .filter(user -> isUserRole(user))
                .toList();
    }

    // =====================================================
    // GET CUSTOMER BY ID
    // =====================================================

    public User getUserById(Long id) {

        if (id == null) {
            throw new RuntimeException("User ID is required");
        }

        return userRepository
                .findByIdAndRole(
                        id,
                        Role.USER.name()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Customer not found"
                        )
                );
    }

    // =====================================================
    // GET CUSTOMER BY EMAIL
    // =====================================================

    public User getUserByEmail(String email) {

        if (email == null ||
                email.trim().isEmpty()) {

            throw new RuntimeException(
                    "Email is required"
            );
        }

        String normalizedEmail =
                email.trim().toLowerCase();

        return userRepository
                .findByEmailAndRole(
                        normalizedEmail,
                        Role.USER.name()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Customer not found"
                        )
                );
    }

    // =====================================================
    // CREATE USER
    // =====================================================

    public User createUser(User user) {

        if (user == null) {
            throw new RuntimeException(
                    "User data is required"
            );
        }

        if (user.getName() == null ||
                user.getName().trim().isEmpty()) {

            throw new RuntimeException(
                    "Name is required"
            );
        }

        if (user.getEmail() == null ||
                user.getEmail().trim().isEmpty()) {

            throw new RuntimeException(
                    "Email is required"
            );
        }

        String email =
                user.getEmail()
                        .trim()
                        .toLowerCase();

        if (userRepository.existsByEmail(email)) {

            throw new RuntimeException(
                    "Email already registered"
            );
        }

        user.setName(
                user.getName().trim()
        );

        user.setEmail(email);

        user.setRole(
                Role.USER.name()
        );

        if (user.getPhone() != null) {
            user.setPhone(
                    user.getPhone().trim()
            );
        }

        if (user.getAddress() != null) {
            user.setAddress(
                    user.getAddress().trim()
            );
        }

        return userRepository.save(user);
    }

    // =====================================================
    // UPDATE USER
    // =====================================================

    public User updateUser(
            Long id,
            User request
    ) {

        if (id == null) {
            throw new RuntimeException(
                    "User ID is required"
            );
        }

        if (request == null) {
            throw new RuntimeException(
                    "User data is required"
            );
        }

        User existingUser =
                userRepository
                        .findByIdAndRole(
                                id,
                                Role.USER.name()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customer not found"
                                )
                        );

        // NAME
        if (request.getName() != null &&
                !request.getName().trim().isEmpty()) {

            existingUser.setName(
                    request.getName().trim()
            );
        }

        // EMAIL
        if (request.getEmail() != null &&
                !request.getEmail().trim().isEmpty()) {

            String newEmail =
                    request.getEmail()
                            .trim()
                            .toLowerCase();

            if (!newEmail.equalsIgnoreCase(
                    existingUser.getEmail()
            )) {

                if (userRepository.existsByEmail(
                        newEmail
                )) {

                    throw new RuntimeException(
                            "Email already registered"
                    );
                }

                existingUser.setEmail(newEmail);
            }
        }

        // PHONE
        if (request.getPhone() != null) {

            String phone =
                    request.getPhone().trim();

            if (!phone.isEmpty() &&
                    !phone.matches(
                            "^[6-9][0-9]{9}$"
                    )) {

                throw new RuntimeException(
                        "Enter a valid 10-digit mobile number"
                );
            }

            existingUser.setPhone(
                    phone.isEmpty()
                            ? null
                            : phone
            );
        }

        // ADDRESS
        if (request.getAddress() != null) {

            existingUser.setAddress(
                    request.getAddress().trim()
            );
        }

        // NEVER allow this service to change role
        existingUser.setRole(
                Role.USER.name()
        );

        // Password is intentionally untouched

        return userRepository.save(existingUser);
    }

    // =====================================================
    // DELETE CUSTOMER
    // =====================================================

    public void deleteUser(Long id) {

        if (id == null) {
            throw new RuntimeException(
                    "User ID is required"
            );
        }

        User user =
                userRepository
                        .findByIdAndRole(
                                id,
                                Role.USER.name()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customer not found"
                                )
                        );

        userRepository.delete(user);
    }

    // =====================================================
    // COUNT CUSTOMERS
    // =====================================================

    public long countUsers() {

        return userRepository.countByRoleIgnoreCase(
                Role.USER.name()
        );
    }

    // =====================================================
    // ROLE CHECK
    // =====================================================

    private boolean isUserRole(User user) {

        if (user == null ||
                user.getRole() == null) {

            return false;
        }

        return Role.USER.name()
                .equalsIgnoreCase(
                        user.getRole().trim()
                );
    }
}