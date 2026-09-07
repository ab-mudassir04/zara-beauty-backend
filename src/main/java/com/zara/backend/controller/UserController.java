package com.zara.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.zara.backend.dto.AuthResponse;
import com.zara.backend.dto.UpdateProfileRequest;
import com.zara.backend.entity.User;
import com.zara.backend.service.AuthService;
import com.zara.backend.service.UserService;

@RestController
@RequestMapping("/users")
@CrossOrigin(
        origins = {
                "http://localhost:5173",
                "http://localhost:4173",
                "http://localhost:5174"
        }
)
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    public UserController(
            AuthService authService,
            UserService userService
    ) {
        this.authService = authService;
        this.userService = userService;
    }

    // =====================================================
    // GET CURRENT USER PROFILE
    // =====================================================

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(
            Authentication authentication
    ) {

        try {

            if (authentication == null) {

                return ResponseEntity
                        .status(401)
                        .body("User is not authenticated");
            }

            String email =
                    authentication.getName();

            AuthResponse response =
                    authService.getProfile(email);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (Exception e) {

            return ResponseEntity
                    .status(500)
                    .body(
                            "Unable to fetch profile: "
                                    + e.getMessage()
                    );
        }
    }

    // =====================================================
    // UPDATE CURRENT USER PROFILE
    // =====================================================

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            Authentication authentication,
            @RequestBody UpdateProfileRequest request
    ) {

        try {

            if (authentication == null) {

                return ResponseEntity
                        .status(401)
                        .body("User is not authenticated");
            }

            String email =
                    authentication.getName();

            AuthResponse response =
                    authService.updateProfile(
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
                            "Profile update failed: "
                                    + e.getMessage()
                    );
        }
    }

    // =====================================================
    // GET ALL CUSTOMERS
    // ADMIN ONLY
    //
    // Frontend uses:
    // GET /users/customers
    // =====================================================

    @GetMapping("/customers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllCustomers() {

        try {

            List<User> customers =
                    userService.getAllUsers();

            return ResponseEntity.ok(customers);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (Exception e) {

            return ResponseEntity
                    .status(500)
                    .body(
                            "Unable to load customers: "
                                    + e.getMessage()
                    );
        }
    }

    // =====================================================
    // GET ALL CUSTOMERS
    // ADMIN ONLY
    //
    // Also supports:
    // GET /users
    // =====================================================

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllCustomersLegacy() {

        try {

            List<User> customers =
                    userService.getAllUsers();

            return ResponseEntity.ok(customers);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (Exception e) {

            return ResponseEntity
                    .status(500)
                    .body(
                            "Unable to load customers: "
                                    + e.getMessage()
                    );
        }
    }

    // =====================================================
    // GET CUSTOMER BY ID
    // ADMIN ONLY
    // =====================================================

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCustomerById(
            @PathVariable Long id
    ) {

        try {

            User user =
                    userService.getUserById(id);

            return ResponseEntity.ok(user);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(404)
                    .body(e.getMessage());

        } catch (Exception e) {

            return ResponseEntity
                    .status(500)
                    .body(
                            "Unable to load customer: "
                                    + e.getMessage()
                    );
        }
    }

    // =====================================================
    // UPDATE CUSTOMER
    // ADMIN ONLY
    // =====================================================

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCustomer(
            @PathVariable Long id,
            @RequestBody User request
    ) {

        try {

            User updatedUser =
                    userService.updateUser(
                            id,
                            request
                    );

            return ResponseEntity.ok(updatedUser);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (Exception e) {

            return ResponseEntity
                    .status(500)
                    .body(
                            "Unable to update customer: "
                                    + e.getMessage()
                    );
        }
    }

    // =====================================================
    // DELETE CUSTOMER
    // ADMIN ONLY
    // =====================================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCustomer(
            @PathVariable Long id
    ) {

        try {

            userService.deleteUser(id);

            return ResponseEntity.ok(
                    "Customer deleted successfully"
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(404)
                    .body(e.getMessage());

        } catch (Exception e) {

            return ResponseEntity
                    .status(500)
                    .body(
                            "Unable to delete customer: "
                                    + e.getMessage()
                    );
        }
    }

    // =====================================================
    // COUNT CUSTOMERS
    // ADMIN ONLY
    // =====================================================

    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> countCustomers() {

        try {

            long count =
                    userService.countUsers();

            return ResponseEntity.ok(count);

        } catch (Exception e) {

            return ResponseEntity
                    .status(500)
                    .body(
                            "Unable to count customers"
                    );
        }
    }
}