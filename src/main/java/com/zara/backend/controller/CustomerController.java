package com.zara.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.zara.backend.entity.User;
import com.zara.backend.service.UserService;

@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:4173",
        "http://localhost:5174"
})
@PreAuthorize("hasRole('ADMIN')")
public class CustomerController {

    private final UserService userService;

    public CustomerController(UserService userService) {
        this.userService = userService;
    }

    // =====================================================
    // GET ALL CUSTOMERS
    // =====================================================

    @GetMapping
    public ResponseEntity<?> getAllCustomers() {

        try {

            List<User> customers =
                    userService.getAllUsers();

            return ResponseEntity.ok(customers);

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
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(
            @PathVariable Long id
    ) {

        try {

            User customer =
                    userService.getUserById(id);

            return ResponseEntity.ok(customer);

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
    // DELETE CUSTOMER
    // =====================================================

    @DeleteMapping("/{id}")
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
    // =====================================================

    @GetMapping("/count")
    public ResponseEntity<?> countCustomers() {

        try {

            return ResponseEntity.ok(
                    userService.countUsers()
            );

        } catch (Exception e) {

            return ResponseEntity
                    .status(500)
                    .body(
                            "Unable to count customers"
                    );
        }
    }
}