package com.zara.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.zara.backend.dto.AdminProfileResponse;
import com.zara.backend.dto.UpdateAdminProfileRequest;
import com.zara.backend.service.AdminProfileService;

@RestController
@RequestMapping("/admin/profile")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminProfileController {

    private final AdminProfileService adminProfileService;

    public AdminProfileController(
            AdminProfileService adminProfileService
    ) {
        this.adminProfileService = adminProfileService;
    }

    // =====================================================
    // GET ADMIN PROFILE
    // =====================================================

    @GetMapping
    public ResponseEntity<?> getAdminProfile(
            Authentication authentication
    ) {

        try {

            if (authentication == null) {

                return ResponseEntity
                        .status(401)
                        .body(
                                "Admin is not authenticated"
                        );
            }

            String email =
                    authentication.getName();

            AdminProfileResponse response =
                    adminProfileService
                            .getAdminProfile(email);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (Exception e) {

            return ResponseEntity
                    .status(500)
                    .body(
                            "Unable to fetch admin profile: "
                                    + e.getMessage()
                    );
        }
    }

    // =====================================================
    // UPDATE ADMIN PROFILE
    // =====================================================

    @PutMapping
    public ResponseEntity<?> updateAdminProfile(
            Authentication authentication,
            @RequestBody UpdateAdminProfileRequest request
    ) {

        try {

            if (authentication == null) {

                return ResponseEntity
                        .status(401)
                        .body(
                                "Admin is not authenticated"
                        );
            }

            String email =
                    authentication.getName();

            AdminProfileResponse response =
                    adminProfileService
                            .updateAdminProfile(
                                    email,
                                    request
                            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (Exception e) {

            return ResponseEntity
                    .status(500)
                    .body(
                            "Admin profile update failed: "
                                    + e.getMessage()
                    );
        }
    }
}