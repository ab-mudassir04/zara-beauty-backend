package com.zara.backend.service;

import org.springframework.stereotype.Service;

import com.zara.backend.dto.AdminProfileResponse;
import com.zara.backend.dto.UpdateAdminProfileRequest;
import com.zara.backend.entity.Role;
import com.zara.backend.entity.User;
import com.zara.backend.repository.UserRepository;

@Service
public class AdminProfileService {

    private final UserRepository userRepository;

    public AdminProfileService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    // =====================================================
    // GET ADMIN PROFILE
    // =====================================================

    public AdminProfileResponse getAdminProfile(
            String email
    ) {

        if (email == null ||
                email.trim().isEmpty()) {

            throw new RuntimeException(
                    "Authentication information is missing"
            );
        }

        String currentEmail =
                email.trim().toLowerCase();

        User admin =
                userRepository
                        .findByEmailAndRole(
                                currentEmail,
                                Role.ADMIN.name()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Admin account not found"
                                )
                        );

        return convertToResponse(admin);
    }

    // =====================================================
    // UPDATE ADMIN PROFILE
    // =====================================================

    public AdminProfileResponse updateAdminProfile(
            String currentEmail,
            UpdateAdminProfileRequest request
    ) {

        if (currentEmail == null ||
                currentEmail.trim().isEmpty()) {

            throw new RuntimeException(
                    "Authentication information is missing"
            );
        }

        if (request == null) {

            throw new RuntimeException(
                    "Admin profile data is required"
            );
        }

        if (request.getName() == null ||
                request.getName().trim().isEmpty()) {

            throw new RuntimeException(
                    "Admin name is required"
            );
        }

        if (request.getEmail() == null ||
                request.getEmail().trim().isEmpty()) {

            throw new RuntimeException(
                    "Admin email is required"
            );
        }

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

        // -----------------------------------------
        // FIND ADMIN
        // -----------------------------------------

        User admin =
                userRepository
                        .findByEmailAndRole(
                                oldEmail,
                                Role.ADMIN.name()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Admin account not found"
                                )
                        );

        // -----------------------------------------
        // EMAIL CHANGE
        // -----------------------------------------

        if (!oldEmail.equalsIgnoreCase(newEmail)) {

            userRepository
                    .findByEmail(newEmail)
                    .ifPresent(existingUser -> {

                        if (!existingUser
                                .getId()
                                .equals(admin.getId())) {

                            throw new RuntimeException(
                                    "Email is already registered."
                            );
                        }
                    });

            admin.setEmail(newEmail);
        }

        // -----------------------------------------
        // PHONE VALIDATION
        // -----------------------------------------

        if (!newPhone.isEmpty() &&
                !newPhone.matches(
                        "^[6-9][0-9]{9}$"
                )) {

            throw new RuntimeException(
                    "Enter a valid 10-digit mobile number."
            );
        }

        // -----------------------------------------
        // UPDATE
        // -----------------------------------------

        admin.setName(newName);

        admin.setPhone(
                newPhone.isEmpty()
                        ? null
                        : newPhone
        );

        admin.setAddress(newAddress);

        // -----------------------------------------
        // NEVER CHANGE ADMIN ROLE
        // -----------------------------------------

        admin.setRole(Role.ADMIN.name());

        // -----------------------------------------
        // SAVE
        // -----------------------------------------

        User savedAdmin =
                userRepository.save(admin);

        return convertToResponse(savedAdmin);
    }

    // =====================================================
    // CONVERT ENTITY -> RESPONSE
    // =====================================================

    private AdminProfileResponse convertToResponse(
            User admin
    ) {

        return new AdminProfileResponse(
                admin.getId(),
                admin.getName(),
                admin.getEmail(),
                admin.getPhone(),
                admin.getAddress(),
                admin.getRole()
        );
    }
}