package com.zara.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zara.backend.dto.PaymentVerificationRequest;
import com.zara.backend.entity.Order;
import com.zara.backend.entity.OrderStatus;
import com.zara.backend.entity.Payment;
import com.zara.backend.entity.PaymentMethod;
import com.zara.backend.entity.PaymentStatus;
import com.zara.backend.repository.OrderRepository;
import com.zara.backend.repository.PaymentRepository;
import com.zara.backend.service.RazorpayService;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final RazorpayService razorpayService;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public PaymentController(
            RazorpayService razorpayService,
            OrderRepository orderRepository,
            PaymentRepository paymentRepository) {

        this.razorpayService = razorpayService;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    // =====================================================
    // CREATE RAZORPAY ORDER
    // =====================================================

    @PostMapping("/create/{orderId}")
    public ResponseEntity<?> createPayment(
            @PathVariable Long orderId,
            Authentication authentication) {

        try {

            // -------------------------------------------------
            // AUTHENTICATION
            // -------------------------------------------------

            if (authentication == null
                    || !authentication.isAuthenticated()) {

                return ResponseEntity
                        .status(401)
                        .body("User is not authenticated");
            }

            // -------------------------------------------------
            // FIND ORDER
            // -------------------------------------------------

            Order order = orderRepository
                    .findById(orderId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Order not found"));

            // -------------------------------------------------
            // VALIDATE ORDER AMOUNT
            // -------------------------------------------------

            if (order.getTotalAmount() == null
                    || order.getTotalAmount() <= 0) {

                throw new RuntimeException(
                        "Order amount is invalid");
            }

            // -------------------------------------------------
            // CREATE RAZORPAY ORDER
            // -------------------------------------------------

            com.razorpay.Order razorpayOrder =
                    razorpayService.createPaymentOrder(
                            order.getTotalAmount(),
                            "order_" + order.getId());

            String razorpayOrderId =
                    razorpayOrder.get("id");

            // -------------------------------------------------
            // FIND EXISTING PAYMENT
            // -------------------------------------------------

            Payment payment =
                    paymentRepository
                            .findByOrderId(order.getId())
                            .orElse(null);

            if (payment == null) {

                payment = new Payment();

                payment.setOrderId(
                        order.getId());
            }

            // -------------------------------------------------
            // PAYMENT DATA
            // -------------------------------------------------

            payment.setAmount(
                    order.getTotalAmount());

            payment.setPaymentMethod(
                    PaymentMethod.RAZORPAY);

            payment.setPaymentStatus(
                    PaymentStatus.PENDING);

            payment.setRazorpayOrderId(
                    razorpayOrderId);

            payment.setRazorpayPaymentId(null);

            paymentRepository.save(payment);

            // -------------------------------------------------
            // RESPONSE
            // -------------------------------------------------

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "key",
                    razorpayService.getKeyId());

            response.put(
                    "razorpayOrderId",
                    razorpayOrderId);

            response.put(
                    "amount",
                    razorpayOrder.get("amount"));

            response.put(
                    "currency",
                    razorpayOrder.get("currency"));

            response.put(
                    "orderId",
                    order.getId());

            response.put(
                    "paymentId",
                    payment.getId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =====================================================
    // VERIFY RAZORPAY PAYMENT
    // =====================================================

    @PostMapping("/verify")
    @Transactional
    public ResponseEntity<?> verifyPayment(
            @RequestBody PaymentVerificationRequest request,
            Authentication authentication) {

        try {

            // -------------------------------------------------
            // AUTHENTICATION
            // -------------------------------------------------

            if (authentication == null
                    || !authentication.isAuthenticated()) {

                return ResponseEntity
                        .status(401)
                        .body("User is not authenticated");
            }

            // -------------------------------------------------
            // VALIDATION
            // -------------------------------------------------

            if (request == null
                    || request.getOrderId() == null
                    || request.getRazorpayOrderId() == null
                    || request.getRazorpayPaymentId() == null
                    || request.getRazorpaySignature() == null) {

                throw new RuntimeException(
                        "Payment verification data is incomplete");
            }

            // -------------------------------------------------
            // VERIFY RAZORPAY SIGNATURE
            // -------------------------------------------------

            boolean valid =
                    razorpayService.verifyPayment(
                            request.getRazorpayOrderId(),
                            request.getRazorpayPaymentId(),
                            request.getRazorpaySignature());

            if (!valid) {

                throw new RuntimeException(
                        "Invalid payment signature");
            }

            // -------------------------------------------------
            // FIND ORDER
            // -------------------------------------------------

            Order order =
                    orderRepository
                            .findById(request.getOrderId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Order not found"));

            // -------------------------------------------------
            // FIND PAYMENT
            // -------------------------------------------------

            Payment payment =
                    paymentRepository
                            .findByOrderId(order.getId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Payment record not found"));

            // -------------------------------------------------
            // VERIFY RAZORPAY ORDER ID
            // -------------------------------------------------

            if (payment.getRazorpayOrderId() == null
                    || !payment.getRazorpayOrderId()
                            .equals(
                                    request.getRazorpayOrderId())) {

                throw new RuntimeException(
                        "Razorpay order ID does not match");
            }

            // -------------------------------------------------
            // PAYMENT ALREADY REFUNDED
            // -------------------------------------------------

            if (payment.getPaymentStatus()
                    == PaymentStatus.REFUNDED) {

                throw new RuntimeException(
                        "Payment has already been refunded");
            }

            // -------------------------------------------------
            // UPDATE PAYMENT
            // -------------------------------------------------

            payment.setPaymentStatus(
                    PaymentStatus.PAID);

            payment.setRazorpayPaymentId(
                    request.getRazorpayPaymentId());

            paymentRepository.save(payment);

            // -------------------------------------------------
            // UPDATE ORDER
            // -------------------------------------------------

            order.setPaymentStatus(
                    PaymentStatus.PAID);

            order.setOrderStatus(
                    OrderStatus.CONFIRMED);

            orderRepository.save(order);

            // -------------------------------------------------
            // RESPONSE
            // -------------------------------------------------

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "message",
                    "Payment verified successfully");

            response.put(
                    "orderId",
                    order.getId());

            response.put(
                    "paymentId",
                    payment.getId());

            response.put(
                    "amount",
                    payment.getAmount());

            response.put(
                    "paymentStatus",
                    payment.getPaymentStatus());

            response.put(
                    "orderStatus",
                    order.getOrderStatus());

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =====================================================
    // GET ALL PAYMENTS
    // ADMIN
    // =====================================================

    @GetMapping
    public ResponseEntity<?> getAllPayments() {

        try {

            List<Payment> payments =
                    paymentRepository.findAll();

            return ResponseEntity.ok(payments);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(500)
                    .body("Unable to load payments");
        }
    }

    // =====================================================
    // GET PAYMENT BY ID
    // ADMIN
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getPayment(
            @PathVariable Long id) {

        try {

            Payment payment =
                    paymentRepository
                            .findById(id)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Payment not found"));

            return ResponseEntity.ok(payment);

        } catch (Exception e) {

            return ResponseEntity
                    .status(404)
                    .body("Payment not found");
        }
    }

    // =====================================================
    // GET PAYMENT BY ORDER ID
    // CUSTOMER / ADMIN
    // =====================================================

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getPaymentByOrderId(
            @PathVariable Long orderId,
            Authentication authentication) {

        try {

            if (authentication == null
                    || !authentication.isAuthenticated()) {

                return ResponseEntity
                        .status(401)
                        .body("User is not authenticated");
            }

            Payment payment =
                    paymentRepository
                            .findByOrderId(orderId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Payment not found for this order"));

            return ResponseEntity.ok(payment);

        } catch (Exception e) {

            return ResponseEntity
                    .status(404)
                    .body(e.getMessage());
        }
    }

    // =====================================================
    // REFUND PAYMENT
    //
    // RAZORPAY:
    // Real Razorpay refund
    //
    // COD:
    // Manual admin refund
    // =====================================================

    @PostMapping("/refund/{paymentId}")
    @Transactional
    public ResponseEntity<?> refundPayment(
            @PathVariable Long paymentId,
            Authentication authentication) {

        try {

            // -------------------------------------------------
            // AUTHENTICATION
            // -------------------------------------------------

            if (authentication == null
                    || !authentication.isAuthenticated()) {

                return ResponseEntity
                        .status(401)
                        .body("User is not authenticated");
            }

            // -------------------------------------------------
            // FIND PAYMENT
            // -------------------------------------------------

            Payment payment =
                    paymentRepository
                            .findById(paymentId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Payment not found"));

            // -------------------------------------------------
            // ONLY PAID PAYMENTS
            // -------------------------------------------------

            if (payment.getPaymentStatus()
                    != PaymentStatus.PAID) {

                throw new RuntimeException(
                        "Only paid payments can be refunded");
            }

            // -------------------------------------------------
            // COD MANUAL REFUND
            // -------------------------------------------------

            if (payment.getPaymentMethod()
                    == PaymentMethod.COD) {

                // No Razorpay API call.
                // Admin manually confirms refund.

                payment.setPaymentStatus(
                        PaymentStatus.REFUNDED);

                paymentRepository.save(payment);

                // ---------------------------------------------
                // UPDATE ORDER
                // ---------------------------------------------

                orderRepository
                        .findById(payment.getOrderId())
                        .ifPresent(order -> {

                            order.setPaymentStatus(
                                    PaymentStatus.REFUNDED);

                            order.setOrderStatus(
                                    OrderStatus.CANCELLED);

                            orderRepository.save(order);
                        });

                // ---------------------------------------------
                // RESPONSE
                // ---------------------------------------------

                Map<String, Object> response =
                        new HashMap<>();

                response.put(
                        "message",
                        "COD payment manually marked as refunded");

                response.put(
                        "paymentId",
                        payment.getId());

                response.put(
                        "orderId",
                        payment.getOrderId());

                response.put(
                        "amount",
                        payment.getAmount());

                response.put(
                        "paymentMethod",
                        payment.getPaymentMethod());

                response.put(
                        "paymentStatus",
                        payment.getPaymentStatus());

                response.put(
                        "refundType",
                        "MANUAL_COD");

                return ResponseEntity.ok(response);
            }

            // -------------------------------------------------
            // RAZORPAY REFUND
            // -------------------------------------------------

            if (payment.getPaymentMethod()
                    == PaymentMethod.RAZORPAY) {

                if (payment.getRazorpayPaymentId() == null
                        || payment.getRazorpayPaymentId()
                                .trim()
                                .isEmpty()) {

                    throw new RuntimeException(
                            "Razorpay payment ID not found");
                }

                // ---------------------------------------------
                // CALL RAZORPAY REFUND API
                // ---------------------------------------------

                razorpayService.refundPayment(
                        payment.getRazorpayPaymentId(),
                        payment.getAmount());

                // ---------------------------------------------
                // UPDATE PAYMENT
                // ---------------------------------------------

                payment.setPaymentStatus(
                        PaymentStatus.REFUNDED);

                paymentRepository.save(payment);

                // ---------------------------------------------
                // UPDATE ORDER
                // ---------------------------------------------

                orderRepository
                        .findById(payment.getOrderId())
                        .ifPresent(order -> {

                            order.setPaymentStatus(
                                    PaymentStatus.REFUNDED);

                            order.setOrderStatus(
                                    OrderStatus.CANCELLED);

                            orderRepository.save(order);
                        });

                // ---------------------------------------------
                // RESPONSE
                // ---------------------------------------------

                Map<String, Object> response =
                        new HashMap<>();

                response.put(
                        "message",
                        "Razorpay payment refunded successfully");

                response.put(
                        "paymentId",
                        payment.getId());

                response.put(
                        "orderId",
                        payment.getOrderId());

                response.put(
                        "amount",
                        payment.getAmount());

                response.put(
                        "paymentMethod",
                        payment.getPaymentMethod());

                response.put(
                        "paymentStatus",
                        payment.getPaymentStatus());

                response.put(
                        "refundType",
                        "RAZORPAY");

                return ResponseEntity.ok(response);
            }

            // -------------------------------------------------
            // UNKNOWN PAYMENT METHOD
            // -------------------------------------------------

            throw new RuntimeException(
                    "Unsupported payment method");

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}