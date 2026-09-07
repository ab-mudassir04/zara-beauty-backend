package com.zara.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.zara.backend.dto.CreateOrderRequest;
import com.zara.backend.dto.OrderResponse;
import com.zara.backend.entity.Order;
import com.zara.backend.entity.OrderStatus;
import com.zara.backend.entity.PaymentStatus;
import com.zara.backend.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // =====================================================
    // CREATE ORDER
    // CUSTOMER + ADMIN
    // =====================================================

    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestBody CreateOrderRequest request,
            Authentication authentication) {

        try {

            if (authentication == null ||
                    !authentication.isAuthenticated()) {

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("User is not authenticated");
            }

            String email = authentication.getName();

            Order order =
                    orderService.createOrder(
                            request,
                            email);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(OrderResponse.fromEntity(order));

        } catch (SecurityException e) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unable to create order");
        }
    }

    // =====================================================
    // GET ALL ORDERS
    // ADMIN ONLY
    // =====================================================

    @GetMapping
    public ResponseEntity<?> getAllOrders() {

        try {

            List<OrderResponse> orders =
                    orderService
                            .getAllOrders()
                            .stream()
                            .map(OrderResponse::fromEntity)
                            .toList();

            return ResponseEntity.ok(orders);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unable to load orders");
        }
    }

    // =====================================================
    // GET ORDER BY ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(
            @PathVariable Long id) {

        try {

            Order order =
                    orderService.getOrderById(id);

            return ResponseEntity.ok(
                    OrderResponse.fromEntity(order));

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unable to load order");
        }
    }

    // =====================================================
    // GET USER ORDERS
    // CUSTOMER
    // =====================================================

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserOrders(
            @PathVariable Long userId,
            Authentication authentication) {

        try {

            if (authentication == null ||
                    !authentication.isAuthenticated()) {

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("User is not authenticated");
            }

            String email =
                    authentication.getName();

            List<OrderResponse> orders =
                    orderService
                            .getUserOrders(
                                    userId,
                                    email)
                            .stream()
                            .map(OrderResponse::fromEntity)
                            .toList();

            return ResponseEntity.ok(orders);

        } catch (SecurityException e) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unable to load user orders");
        }
    }

    // =====================================================
    // UPDATE ORDER STATUS
    // ADMIN ONLY
    // =====================================================

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody StatusRequest request) {

        try {

            // =============================================
            // VALIDATE REQUEST
            // =============================================

            if (request == null ||
                    request.getStatus() == null ||
                    request.getStatus().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body("Order status is required");
            }

            // =============================================
            // CONVERT STRING -> ENUM
            // =============================================

            OrderStatus status;

            try {

                status =
                        OrderStatus.valueOf(
                                request.getStatus()
                                        .trim()
                                        .toUpperCase());

            } catch (IllegalArgumentException e) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Invalid order status: "
                                        + request.getStatus());
            }

            // =============================================
            // UPDATE ORDER
            // =============================================

            Order order =
                    orderService.updateOrderStatus(
                            id,
                            status);

            // =============================================
            // RETURN DTO
            // =============================================

            return ResponseEntity.ok(
                    OrderResponse.fromEntity(order));

        } catch (RuntimeException e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unable to update order status");
        }
    }

    // =====================================================
    // UPDATE PAYMENT STATUS
    // ADMIN ONLY
    // =====================================================

    @PutMapping("/{id}/payment-status")
    public ResponseEntity<?> updatePaymentStatus(
            @PathVariable Long id,
            @RequestBody PaymentStatusRequest request) {

        try {

            // =============================================
            // VALIDATE REQUEST
            // =============================================

            if (request == null ||
                    request.getPaymentStatus() == null ||
                    request.getPaymentStatus()
                            .trim()
                            .isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body("Payment status is required");
            }

            // =============================================
            // CONVERT STRING -> ENUM
            // =============================================

            PaymentStatus paymentStatus;

            try {

                paymentStatus =
                        PaymentStatus.valueOf(
                                request.getPaymentStatus()
                                        .trim()
                                        .toUpperCase());

            } catch (IllegalArgumentException e) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Invalid payment status: "
                                        + request.getPaymentStatus());
            }

            // =============================================
            // UPDATE PAYMENT
            // =============================================

            Order order =
                    orderService.updatePaymentStatus(
                            id,
                            paymentStatus);

            // =============================================
            // RETURN DTO
            // =============================================

            return ResponseEntity.ok(
                    OrderResponse.fromEntity(order));

        } catch (RuntimeException e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unable to update payment status");
        }
    }

    // =====================================================
    // ORDER STATUS REQUEST DTO
    // =====================================================

    public static class StatusRequest {

        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    // =====================================================
    // PAYMENT STATUS REQUEST DTO
    // =====================================================

    public static class PaymentStatusRequest {

        private String paymentStatus;

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }
    }
}