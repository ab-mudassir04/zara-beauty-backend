package com.zara.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.zara.backend.dto.AuthResponse;
import com.zara.backend.dto.LoginRequest;
import com.zara.backend.dto.RegisterRequest;
import com.zara.backend.dto.UpdateProfileRequest;
import com.zara.backend.entity.Role;
import com.zara.backend.entity.User;
import com.zara.backend.repository.UserRepository;
import com.zara.backend.security.JwtUtil;

@Service
public class AuthService {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public AuthService(
            UserRepository repository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {

        this.repository = repository;

        this.passwordEncoder = passwordEncoder;

        this.jwtUtil = jwtUtil;
    }


    // =====================================================
    // REGISTER
    // =====================================================

    public AuthResponse register(
            RegisterRequest request
    ) {

        if (request == null) {

            throw new RuntimeException(
                    "Registration data is required"
            );
        }


        // =================================================
        // NAME VALIDATION
        // =================================================

        if (request.getName() == null ||
                request.getName().trim().isEmpty()) {

            throw new RuntimeException(
                    "Name is required"
            );
        }


        // =================================================
        // EMAIL VALIDATION
        // =================================================

        if (request.getEmail() == null ||
                request.getEmail().trim().isEmpty()) {

            throw new RuntimeException(
                    "Email is required"
            );
        }


        // =================================================
        // PASSWORD VALIDATION
        // =================================================

        if (request.getPassword() == null ||
                request.getPassword().length() < 6) {

            throw new RuntimeException(
                    "Password must contain at least 6 characters"
            );
        }


        // =================================================
        // CLEAN INPUT
        // =================================================

        String name =
                request.getName()
                        .trim();


        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();


        String phone =
                request.getPhone() != null
                        ? request.getPhone().trim()
                        : "";


        String address =
                request.getAddress() != null
                        ? request.getAddress().trim()
                        : "";


        // =================================================
        // EMAIL CHECK
        // =================================================

        if (repository.existsByEmail(email)) {

            throw new RuntimeException(
                    "Email already registered"
            );
        }


        // =================================================
        // CREATE USER
        // =================================================

        User user = new User();

        user.setName(name);

        user.setEmail(email);


        // =================================================
        // PASSWORD ENCRYPTION
        // =================================================

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );


        // =================================================
        // DEFAULT ROLE
        // =================================================

        /*
         * IMPORTANT:
         *
         * Public registration can NEVER create
         * an ADMIN account.
         *
         * Every newly registered user is USER.
         */

        user.setRole(
                Role.USER.name()
        );


        user.setPhone(phone);

        user.setAddress(address);


        // =================================================
        // SAVE USER
        // =================================================

        User savedUser =
                repository.save(user);


        // =================================================
        // GENERATE JWT
        // =================================================

        String token =
                jwtUtil.generateToken(
                        savedUser.getEmail(),
                        savedUser.getRole()
                );


        // =================================================
        // RESPONSE
        // =================================================

        return new AuthResponse(

                savedUser.getId(),

                token,

                savedUser.getRole(),

                savedUser.getName(),

                savedUser.getEmail(),

                savedUser.getPhone(),

                savedUser.getAddress()
        );
    }


    // =====================================================
    // LOGIN
    // =====================================================

    public AuthResponse login(
            LoginRequest request
    ) {

        if (request == null) {

            throw new RuntimeException(
                    "Invalid login request"
            );
        }


        // =================================================
        // VALIDATE LOGIN INPUT
        // =================================================

        if (request.getEmail() == null ||
                request.getPassword() == null) {

            throw new RuntimeException(
                    "Email and password are required"
            );
        }


        // =================================================
        // CLEAN EMAIL
        // =================================================

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();


        // =================================================
        // FIND USER
        // =================================================

        User user =
                repository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid email or password"
                                )
                        );


        // =================================================
        // VERIFY PASSWORD
        // =================================================

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {

            throw new RuntimeException(
                    "Invalid email or password"
            );
        }


        // =================================================
        // NORMALIZE ROLE
        // =================================================

        String role =
                normalizeRole(
                        user.getRole()
                );


        // =================================================
        // DEFAULT ROLE
        // =================================================

        if (role == null) {

            role =
                    Role.USER.name();

            user.setRole(role);

            user =
                    repository.save(user);
        }


        // =================================================
        // GENERATE JWT
        // =================================================

        String token =
                jwtUtil.generateToken(
                        user.getEmail(),
                        role
                );


        // =================================================
        // RESPONSE
        // =================================================

        return new AuthResponse(

                user.getId(),

                token,

                role,

                user.getName(),

                user.getEmail(),

                user.getPhone(),

                user.getAddress()
        );
    }


    // =====================================================
    // GET CURRENT USER PROFILE
    // =====================================================

    public AuthResponse getProfile(
            String currentEmail
    ) {

        if (currentEmail == null ||
                currentEmail.trim().isEmpty()) {

            throw new RuntimeException(
                    "Authentication information is missing"
            );
        }


        // =================================================
        // CLEAN EMAIL
        // =================================================

        String email =
                currentEmail
                        .trim()
                        .toLowerCase();


        // =================================================
        // FIND USER
        // =================================================

        User user =
                repository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );


        // =================================================
        // NORMALIZE ROLE
        // =================================================

        String role =
                normalizeRole(
                        user.getRole()
                );


        // =================================================
        // DEFAULT ROLE
        // =================================================

        if (role == null) {

            role =
                    Role.USER.name();

            user.setRole(role);

            user =
                    repository.save(user);
        }


        // =================================================
        // RESPONSE
        // =================================================

        return new AuthResponse(

                user.getId(),

                null,

                role,

                user.getName(),

                user.getEmail(),

                user.getPhone(),

                user.getAddress()
        );
    }


    // =====================================================
    // UPDATE CURRENT USER PROFILE
    // =====================================================

    public AuthResponse updateProfile(

            String currentEmail,

            UpdateProfileRequest request

    ) {

        if (currentEmail == null ||
                currentEmail.trim().isEmpty()) {

            throw new RuntimeException(
                    "Authentication information is missing"
            );
        }


        // =================================================
        // REQUEST VALIDATION
        // =================================================

        if (request == null) {

            throw new RuntimeException(
                    "Profile data is required"
            );
        }


        if (request.getName() == null ||
                request.getName().trim().isEmpty()) {

            throw new RuntimeException(
                    "Name is required"
            );
        }


        if (request.getEmail() == null ||
                request.getEmail().trim().isEmpty()) {

            throw new RuntimeException(
                    "Email is required"
            );
        }


        // =================================================
        // CLEAN INPUT
        // =================================================

        String oldEmail =
                currentEmail
                        .trim()
                        .toLowerCase();


        String newName =
                request.getName()
                        .trim();


        String newEmail =
                request.getEmail()
                        .trim()
                        .toLowerCase();


        String newPhone =
                request.getPhone() != null
                        ? request.getPhone().trim()
                        : "";


        String newAddress =
                request.getAddress() != null
                        ? request.getAddress().trim()
                        : "";


        // =================================================
        // FIND CURRENT USER
        // =================================================

        User user =
                repository
                        .findByEmail(oldEmail)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );


        // =================================================
        // EMAIL CHANGE
        // =================================================

        if (!oldEmail.equalsIgnoreCase(newEmail)) {

            if (repository.existsByEmail(newEmail)) {

                throw new RuntimeException(
                        "Email already registered"
                );
            }


            user.setEmail(newEmail);
        }


        // =================================================
        // UPDATE USER DETAILS
        // =================================================

        user.setName(newName);

        user.setPhone(newPhone);

        user.setAddress(newAddress);


        // =================================================
        // NEVER ALLOW PROFILE UPDATE
        // TO CHANGE ROLE
        // =================================================

        String role =
                normalizeRole(
                        user.getRole()
                );


        if (role == null) {

            role =
                    Role.USER.name();

            user.setRole(role);
        }


        // =================================================
        // SAVE USER
        // =================================================

        User updatedUser =
                repository.save(user);


        // =================================================
        // GENERATE NEW JWT
        //
        // Important because email may have changed.
        // =================================================

        String token =
                jwtUtil.generateToken(
                        updatedUser.getEmail(),
                        role
                );


        // =================================================
        // RESPONSE
        // =================================================

        return new AuthResponse(

                updatedUser.getId(),

                token,

                role,

                updatedUser.getName(),

                updatedUser.getEmail(),

                updatedUser.getPhone(),

                updatedUser.getAddress()
        );
    }


    // =====================================================
    // NORMALIZE ROLE
    // =====================================================

    private String normalizeRole(
            String role
    ) {

        if (role == null ||
                role.trim().isEmpty()) {

            return null;
        }


        return role
                .trim()
                .toUpperCase();
    }
}